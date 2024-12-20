package compose.project.demo.ui.race.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Race
import compose.project.demo.domain.Rider
import compose.project.demo.domain.Stage
import compose.project.demo.domain.StageType
import compose.project.demo.domain.Team
import compose.project.demo.domain.endDate
import compose.project.demo.domain.firebaseDataRepository
import compose.project.demo.domain.firstStage
import compose.project.demo.domain.isActive
import compose.project.demo.domain.isFuture
import compose.project.demo.domain.isPast
import compose.project.demo.domain.isSingleDay
import compose.project.demo.domain.startDate
import compose.project.demo.domain.todayStage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

internal class RaceListViewModel(dataRepository: DataRepository = firebaseDataRepository) :
    ViewModel() {

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
                    }
                    UiState.SeasonInProgressViewState(
                        todayStages = todayStages.toImmutableList(),
                        pastRaces = races.filter(Race::isPast).reversed().toImmutableList(),
                        futureRaces = races.filter(Race::isFuture).toImmutableList(),
                    )
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
                stage.stageResults.time.take(3).map { participantResult ->
                    RiderTimeResult(
                        riders.find { it.id == participantResult.participantId }!!,
                        participantResult.time,
                    )
                }.toImmutableList(),
            )

            StageType.TEAM_TIME_TRIAL -> TodayResults.Teams(
                stage.stageResults.time.take(3).map { participantResult ->
                    TeamTimeResult(
                        teams.find { it.id == participantResult.participantId }!!,
                        participantResult.time,
                    )
                }.toImmutableList(),
            )
        }
    }
}
