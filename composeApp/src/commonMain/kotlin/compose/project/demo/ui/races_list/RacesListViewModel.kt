package compose.project.demo.ui.races_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Race
import compose.project.demo.domain.Rider
import compose.project.demo.domain.Stage
import compose.project.demo.domain.StageType
import compose.project.demo.domain.Team
import compose.project.demo.domain.endDate
import compose.project.demo.domain.firstStage
import compose.project.demo.domain.isActive
import compose.project.demo.domain.isFuture
import compose.project.demo.domain.isPast
import compose.project.demo.domain.isSingleDay
import compose.project.demo.domain.startDate
import compose.project.demo.domain.todayStage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class RacesListViewModel(dataRepository: DataRepository = compose.project.demo.domain.dataRepository) :
    ViewModel() {

    sealed interface TodayResults {
        data class Teams(val teams: List<TeamTimeResult>) : TodayResults
        data class Riders(val riders: List<RiderTimeResult>) : TodayResults
    }

    data class RiderTimeResult(val rider: Rider, val time: Long)

    data class TeamTimeResult(val team: Team, val time: Long)

    sealed class TodayStage(open val race: Race) {
        data class RestDay(override val race: Race) : TodayStage(race)
        data class SingleDayRace(
            override val race: Race,
            val stage: Stage,
            val results: TodayResults,
        ) : TodayStage(race)

        data class MultiStageRace(
            override val race: Race,
            val stage: Stage,
            val stageNumber: Int,
            val results: TodayResults,
        ) :
            TodayStage(race)
    }

    sealed class UiState {
        data class SeasonNotStartedViewState(
            val futureRaces: List<Race>,
        ) : UiState()

        data class SeasonInProgressViewState(
            val pastRaces: List<Race>,
            val todayStages: List<TodayStage>,
            val futureRaces: List<Race>,
        ) : UiState()

        data class SeasonEndedViewState(
            val pastRaces: List<Race>,
        ) : UiState()

        data object EmptyViewState : UiState()

        companion object {
            val Empty = EmptyViewState
        }
    }

    val uiState: StateFlow<UiState> =
        combine(
            dataRepository.races,
            dataRepository.teams,
            dataRepository.riders
        ) { races, teams, riders ->
            val minStartDate = races.first().startDate()
            val maxEndDate = races.last().endDate()
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            when {
                today < minStartDate -> UiState.SeasonNotStartedViewState(races)
                today > maxEndDate -> UiState.SeasonEndedViewState(races)
                else -> {
                    val todayStages = races.filter(Race::isActive).map { race ->
                        val todayStage = race.todayStage()
                        when {
                            race.isSingleDay() -> TodayStage.SingleDayRace(
                                race = race,
                                stage = race.firstStage(),
                                results = stageResults(race.firstStage(), riders, teams),
                            )

                            todayStage != null -> TodayStage.MultiStageRace(
                                race = race,
                                stage = todayStage.first,
                                stageNumber = todayStage.second + 1,
                                results = stageResults(todayStage.first, riders, teams),
                            )

                            else -> TodayStage.RestDay(race)
                        }
                    }
                    UiState.SeasonInProgressViewState(
                        todayStages = todayStages,
                        pastRaces = races.filter(Race::isPast).reversed(),
                        futureRaces = races.filter(Race::isFuture),
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState.Empty,
        )

    private fun stageResults(stage: Stage, riders: List<Rider>, teams: List<Team>): TodayResults {
        return when (stage.stageType) {
            StageType.REGULAR, StageType.INDIVIDUAL_TIME_TRIAL -> TodayResults.Riders(
                stage.stageResults.time.take(3).map { participantResult ->
                    RiderTimeResult(
                        riders.find { it.id == participantResult.participantId }!!,
                        participantResult.time,
                    )
                },
            )

            StageType.TEAM_TIME_TRIAL -> TodayResults.Teams(
                stage.stageResults.time.take(3).map { participantResult ->
                    TeamTimeResult(
                        teams.find { it.id == participantResult.participantId }!!,
                        participantResult.time,
                    )
                },
            )
        }
    }


}