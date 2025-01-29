package io.github.patxibocos.mycyclist.ui.race.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.domain.StageType
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.coroutines.CoroutineContext

internal class RaceListViewModel(
    dataRepository: DataRepository = firebaseDataRepository,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
) :
    ViewModel() {

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

    internal sealed class UiState {
        internal data class SeasonNotStartedViewState(
            val futureRaces: ImmutableList<Race>,
        ) : UiState()

        internal data class SeasonInProgressViewState(
            val pastRaces: ImmutableList<Race>,
            val todayStages: ImmutableList<TodayStage>,
            val futureRaces: ImmutableList<Race>,
        ) : UiState()

        internal data class SeasonEndedViewState(
            val pastRaces: ImmutableList<Race>,
        ) : UiState()

        internal data object EmptyViewState : UiState()
    }

    internal val uiState: StateFlow<UiState?> =
        combine(
            dataRepository.races,
            dataRepository.teams,
            dataRepository.riders,
        ) { races, teams, riders ->
            withContext(defaultDispatcher) {
                val minStartDate = races.first().startDate()
                val maxEndDate = races.last().endDate()
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                when {
                    today < minStartDate -> UiState.SeasonNotStartedViewState(races.toImmutableList())
                    today > maxEndDate -> UiState.SeasonEndedViewState(races.toImmutableList())
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
                        UiState.SeasonInProgressViewState(
                            todayStages = todayStages,
                            pastRaces = pastRaces,
                            futureRaces = futureRaces,
                        )
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

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
