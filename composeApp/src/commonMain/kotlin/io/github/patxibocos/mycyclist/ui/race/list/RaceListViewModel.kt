package io.github.patxibocos.mycyclist.ui.race.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.domain.entity.StageType
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.domain.repository.CyclingDataRepository
import io.github.patxibocos.mycyclist.domain.repository.cyclingDataRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

internal class RaceListViewModel(
    private val dataRepository: CyclingDataRepository = cyclingDataRepository,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
) :
    ViewModel() {

    private val _refreshing = MutableStateFlow(false)

    private companion object {
        private const val RESULTS_TO_DISPLAY = 3
    }

    internal sealed interface TodayResults {
        data class Teams(val teams: ImmutableList<TeamTimeResult>) : TodayResults
        data class Riders(val riders: ImmutableList<RiderTimeResult>) : TodayResults
    }

    internal data class RiderTimeResult(val rider: Rider, val time: Long)

    internal data class TeamTimeResult(val team: Team, val time: Long)

    internal sealed class TodayStage(open val race: Race) {
        internal data class RestDay(override val race: Race) : TodayStage(race)
        internal data class SingleDayRace(
            override val race: Race,
            val stage: Stage,
            val results: TodayResults,
        ) : TodayStage(race)

        internal data class MultiStageRace(
            override val race: Race,
            val stage: Stage,
            val stageNumber: Int,
            val results: TodayResults,
        ) : TodayStage(race)
    }

    internal data class UiState(val refreshing: Boolean, val content: Content)

    internal sealed interface Content {
        data class SeasonNotStartedViewState(
            val futureRaces: ImmutableList<Race>,
        ) : Content

        data class SeasonInProgressViewState(
            val pastRaces: ImmutableList<Race>,
            val todayStages: ImmutableList<TodayStage>,
            val futureRaces: ImmutableList<Race>,
        ) : Content

        data class SeasonEndedViewState(
            val pastRaces: ImmutableList<Race>,
        ) : Content

        data object EmptyViewState : Content
    }

    internal val uiState: StateFlow<UiState?> =
        combine(
            this@RaceListViewModel.dataRepository.cyclingData,
            _refreshing
        ) { (races, teams, riders), refreshing ->
            withContext(defaultDispatcher) {
                val minStartDate = races.first().startDate()
                val maxEndDate = races.last().endDate()
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val content = when {
                    today < minStartDate -> Content.SeasonNotStartedViewState(races.toImmutableList())
                    today > maxEndDate -> Content.SeasonEndedViewState(races.toImmutableList())
                    else -> {
                        val todayStages = races.filter(Race::isActive).map { race ->
                            val todayStage = race.todayStage()
                            when {
                                race.isSingleDay() -> TodayStage.SingleDayRace(
                                    race = race,
                                    stage = race.firstStage(),
                                    results = stageResults(
                                        race.firstStage(),
                                        riders.toImmutableList(),
                                        teams.toImmutableList()
                                    ),
                                )

                                todayStage != null -> TodayStage.MultiStageRace(
                                    race = race,
                                    stage = todayStage.first,
                                    stageNumber = todayStage.second + 1,
                                    results = stageResults(
                                        todayStage.first,
                                        riders.toImmutableList(),
                                        teams.toImmutableList()
                                    ),
                                )

                                else -> TodayStage.RestDay(race)
                            }
                        }.toImmutableList()
                        val pastRaces = races.filter(Race::isPast).reversed().toImmutableList()
                        val futureRaces = races.filter(Race::isFuture).toImmutableList()
                        Content.SeasonInProgressViewState(
                            todayStages = todayStages,
                            pastRaces = pastRaces,
                            futureRaces = futureRaces,
                        )
                    }
                }
                UiState(refreshing, content)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    internal fun refresh() {
        viewModelScope.launch {
            _refreshing.value = true
            val refreshTime = measureTime {
                this@RaceListViewModel.dataRepository.refresh()
            }
            // Show loading at least for a second
            delay(1.seconds.minus(refreshTime))
            _refreshing.value = false
        }
    }

    private fun stageResults(
        stage: Stage,
        riders: ImmutableList<Rider>,
        teams: ImmutableList<Team>
    ): TodayResults {
        return when (stage.stageType) {
            StageType.REGULAR, StageType.INDIVIDUAL_TIME_TRIAL -> TodayResults.Riders(
                stage.stageResults.time.take(RESULTS_TO_DISPLAY).map { participantResult ->
                    RiderTimeResult(
                        riders.find { it.id == participantResult.participantId }!!,
                        participantResult.time,
                    )
                }.toImmutableList(),
            )

            StageType.TEAM_TIME_TRIAL -> TodayResults.Teams(
                stage.stageResults.time.take(RESULTS_TO_DISPLAY).map { participantResult ->
                    TeamTimeResult(
                        teams.find { it.id == participantResult.participantId }!!,
                        participantResult.time,
                    )
                }.toImmutableList(),
            )
        }
    }
}
