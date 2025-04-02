package io.github.patxibocos.mycyclist.ui.race.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.domain.repository.DataRepository
import io.github.patxibocos.mycyclist.domain.repository.firebaseDataRepository
import io.github.patxibocos.mycyclist.domain.usecase.ClassificationType
import io.github.patxibocos.mycyclist.domain.usecase.RaceResults
import io.github.patxibocos.mycyclist.domain.usecase.ResultsMode
import io.github.patxibocos.mycyclist.domain.usecase.StageResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class RaceDetailsViewModel(
    private val raceId: String,
    private val stageId: String?,
    dataRepository: DataRepository = firebaseDataRepository,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
    private val raceResults: RaceResults = RaceResults(),
) :
    ViewModel() {

    internal data class UiState(
        val race: Race,
        val currentStageIndex: Int,
        val resultsMode: ResultsMode,
        val classificationType: ClassificationType,
        val stagesResults: ImmutableList<StageResult>,
    )

    private val _stageIndex = MutableSharedFlow<Int>(replay = 1)
    private val _resultsMode = MutableSharedFlow<ResultsMode>(replay = 1)
    private val _classificationType = MutableSharedFlow<ClassificationType>(replay = 1)

    internal data class RaceWithTeamsAndRiders(
        val race: Race,
        val teams: List<Team>,
        val riders: List<Rider>,
    )

    private val raceFlow = dataRepository.cyclingData.map { (races, teams, riders) ->
        val race = withContext(defaultDispatcher) {
            races.find { it.id == raceId }!!
        }
        emitInitialRaceState(race, stageId)
        RaceWithTeamsAndRiders(
            race = race,
            teams = teams,
            riders = riders,
        )
    }.take(1)

    internal val uiState: StateFlow<UiState?> =
        combine(
            raceFlow,
            _stageIndex,
            _resultsMode,
            _classificationType,
        ) { raceWithTeamsAndRiders, stageIndex, resultsMode, classificationType ->
            withContext(defaultDispatcher) {
                val stagesResults = raceResults(
                    race = raceWithTeamsAndRiders.race,
                    resultsMode = resultsMode,
                    classificationType = classificationType,
                    riders = raceWithTeamsAndRiders.riders,
                    teams = raceWithTeamsAndRiders.teams,
                )
                UiState(
                    race = raceWithTeamsAndRiders.race,
                    currentStageIndex = stageIndex,
                    resultsMode = resultsMode,
                    classificationType = classificationType,
                    stagesResults = stagesResults,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    private fun emitInitialRaceState(race: Race, stageId: String?) {
        val stageIndex: Int
        val resultsMode: ResultsMode
        // If stageId is not provided:
        //   Is past race -> set last stage as current stage + set GC view as current view
        //   Today is happening any of the stages -> set today's stage as current stage + set Stage view as current view
        //   Today is rest day -> set yesterday's stage as current stage + set GC view as current view
        //   Else -> set first stage as current stage + set Stage view as current stage
        // Otherwise
        //   Set given stage as current stage + set Stage view as current view
        if (stageId != null) {
            stageIndex = race.stages.indexOfFirst { it.id == stageId }
            resultsMode = ResultsMode.Stage
        } else {
            when {
                race.isPast() -> {
                    stageIndex = race.stages.size - 1
                    resultsMode = ResultsMode.General
                }

                race.todayStage() != null -> {
                    stageIndex = race.todayStage()!!.second
                    resultsMode = ResultsMode.Stage
                }

                race.isActive() -> {
                    stageIndex = race.indexOfLastStageWithResults()
                    resultsMode = ResultsMode.General
                }

                else -> {
                    stageIndex = 0
                    resultsMode = ResultsMode.Stage
                }
            }
        }
        _stageIndex.tryEmit(stageIndex)
        _resultsMode.tryEmit(resultsMode)
        _classificationType.tryEmit(ClassificationType.Time)
    }

    internal fun onStageSelected(stageIndex: Int) {
        _stageIndex.tryEmit(stageIndex)
    }

    internal fun onResultsModeChanged(resultsMode: ResultsMode) {
        _resultsMode.tryEmit(resultsMode)
    }

    internal fun onClassificationTypeChanged(classificationType: ClassificationType) {
        _classificationType.tryEmit(classificationType)
    }
}
