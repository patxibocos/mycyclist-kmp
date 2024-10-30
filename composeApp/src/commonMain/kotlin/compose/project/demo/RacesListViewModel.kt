package compose.project.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.Race
import compose.project.demo.domain.Stage
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

class RacesListViewModel(dataRepository: DataRepository = compose.project.demo.dataRepository) :
    ViewModel() {

    sealed class TodayStage(open val race: Race) {
        data class RestDay(override val race: Race) : TodayStage(race)
        data class SingleDayRace(
            override val race: Race,
            val stage: Stage,
//            val results: TodayResults,
        ) : TodayStage(race)

        data class MultiStageRace(
            override val race: Race,
            val stage: Stage,
            val stageNumber: Int,
//            val results: TodayResults,
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
//                                results = stageResults(race.firstStage(), riders, teams),
                            )

                            todayStage != null -> TodayStage.MultiStageRace(
                                race = race,
                                stage = todayStage.first,
                                stageNumber = todayStage.second + 1,
//                                results = stageResults(todayStage.first, riders, teams),
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

}