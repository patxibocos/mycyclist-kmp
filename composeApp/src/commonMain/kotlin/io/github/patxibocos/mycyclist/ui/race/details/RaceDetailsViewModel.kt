package io.github.patxibocos.mycyclist.ui.race.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Place
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.domain.StageType
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class RaceDetailsViewModel(
    private val raceId: String,
    private val stageId: String?,
    dataRepository: DataRepository = firebaseDataRepository,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
) :
    ViewModel() {

    internal data class UiState(
        val race: Race,
        val currentStageIndex: Int,
        val resultsMode: ResultsMode,
        val classificationType: ClassificationType,
        val stagesResults: ImmutableMap<Stage, Results>,
    )

    private val _stageIndex = MutableSharedFlow<Int>(replay = 1)
    private val _resultsMode = MutableSharedFlow<ResultsMode>(replay = 1)
    private val _classificationType = MutableSharedFlow<ClassificationType>(replay = 1)

    private val _raceState = combine(
        _stageIndex,
        _resultsMode,
        _classificationType,
    ) { stageIndex, resultsMode, classificationType ->
        Triple(stageIndex, resultsMode, classificationType)
    }

    internal enum class ResultsMode {
        Stage,
        General,
    }

    internal enum class ClassificationType {
        Time,
        Points,
        Kom,
        Youth,
        Teams,
    }

    internal sealed interface Results {
        data class TeamsTimeResult(val teams: List<TeamTimeResult>) : Results
        data class RidersTimeResult(val riders: List<RiderTimeResult>) : Results
        data class RidersPointResult(val riders: List<RiderPointsResult>) : Results
        data class RidersPointsPerPlaceResult(val perPlaceResult: Map<Place, List<RiderPointsResult>>) :
            Results
    }

    internal data class RiderTimeResult(val rider: Rider, val position: Int, val time: Long)

    internal data class TeamTimeResult(val team: Team, val position: Int, val time: Long)

    internal data class RiderPointsResult(
        val rider: Rider,
        val position: Int,
        val points: Int,
    )

    internal data class RaceWithTeamsAndRiders(
        val race: Race,
        val teams: List<Team>,
        val riders: List<Rider>,
    )

    internal val uiState: StateFlow<UiState?> =
        combine(
            dataRepository.races,
            dataRepository.teams,
            dataRepository.riders
        ) { races, teams, riders ->
            val race = withContext(defaultDispatcher) {
                races.find { it.id == raceId }!!
            }
            RaceWithTeamsAndRiders(
                race = race,
                teams = teams,
                riders = riders,
            )
        }.distinctUntilChanged()
            .onEach { raceWithTeamsAndRiders ->
                emitInitialRaceState(raceWithTeamsAndRiders.race, stageId)
            }
            .combine(_raceState) { raceWithTeamsAndRiders, (stageIndex, resultsMode, classificationType) ->
                withContext(defaultDispatcher) {
                    val stagesResults = raceWithTeamsAndRiders.race.stages.associateWith { stage ->
                        stageResults(
                            stage = stage,
                            resultsMode = resultsMode,
                            classificationType = classificationType,
                            riders = raceWithTeamsAndRiders.riders,
                            teams = raceWithTeamsAndRiders.teams,
                        )
                    }.toImmutableMap()
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

    private suspend fun emitInitialRaceState(race: Race, stageId: String?) {
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
        _stageIndex.emit(stageIndex)
        _resultsMode.emit(resultsMode)
        _classificationType.emit(ClassificationType.Time)
    }

    private fun stageResults(
        stage: Stage,
        resultsMode: ResultsMode,
        classificationType: ClassificationType,
        riders: List<Rider>,
        teams: List<Team>,
    ): Results =
        when (classificationType) {
            ClassificationType.Time -> timeResults(resultsMode, stage, teams, riders)
            ClassificationType.Points -> pointsResults(resultsMode, stage, riders)
            ClassificationType.Kom -> komResults(resultsMode, stage, riders)
            ClassificationType.Youth -> youthResults(resultsMode, stage, riders)
            ClassificationType.Teams -> teamsResults(resultsMode, stage, teams)
        }

    private fun teamsResults(
        resultsMode: ResultsMode,
        stage: Stage,
        teams: List<Team>
    ): Results.TeamsTimeResult = when (resultsMode) {
        ResultsMode.Stage -> Results.TeamsTimeResult(
            stage.stageResults.teams.map { participantResult ->
                TeamTimeResult(
                    teams.find { it.id == participantResult.participantId }!!,
                    position = participantResult.position,
                    participantResult.time,
                )
            },
        )

        ResultsMode.General -> Results.TeamsTimeResult(
            stage.generalResults.teams.map { participantResult ->
                TeamTimeResult(
                    teams.find { it.id == participantResult.participantId }!!,
                    position = participantResult.position,
                    participantResult.time,
                )
            },
        )
    }

    private fun youthResults(
        resultsMode: ResultsMode,
        stage: Stage,
        riders: List<Rider>
    ): Results.RidersTimeResult = when (resultsMode) {
        ResultsMode.Stage -> Results.RidersTimeResult(
            stage.stageResults.youth.map { participantResult ->
                RiderTimeResult(
                    riders.find { it.id == participantResult.participantId }!!,
                    position = participantResult.position,
                    participantResult.time,
                )
            },
        )

        ResultsMode.General -> Results.RidersTimeResult(
            stage.generalResults.youth.map { participantResult ->
                RiderTimeResult(
                    riders.find { it.id == participantResult.participantId }!!,
                    position = participantResult.position,
                    participantResult.time,
                )
            },
        )
    }

    private fun komResults(
        resultsMode: ResultsMode,
        stage: Stage,
        riders: List<Rider>
    ): Results = when (resultsMode) {
        ResultsMode.Stage -> Results.RidersPointsPerPlaceResult(
            stage.stageResults.kom.associate {
                it.place to it.points.map { riderResult ->
                    RiderPointsResult(
                        riders.find { rider -> rider.id == riderResult.participant }!!,
                        position = riderResult.position,
                        riderResult.points,
                    )
                }
            },
        )

        ResultsMode.General -> Results.RidersPointResult(
            stage.generalResults.kom.map { participantResult ->
                RiderPointsResult(
                    riders.find { it.id == participantResult.participant }!!,
                    position = participantResult.position,
                    participantResult.points,
                )
            },
        )
    }

    private fun timeResults(
        resultsMode: ResultsMode,
        stage: Stage,
        teams: List<Team>,
        riders: List<Rider>
    ): Results = when (resultsMode) {
        ResultsMode.Stage -> when (stage.stageType) {
            StageType.TEAM_TIME_TRIAL -> Results.TeamsTimeResult(
                stage.stageResults.time.map { participantResult ->
                    TeamTimeResult(
                        teams.find { it.id == participantResult.participantId }!!,
                        position = participantResult.position,
                        participantResult.time,
                    )
                },
            )

            else -> Results.RidersTimeResult(
                stage.stageResults.time.map { participantResult ->
                    RiderTimeResult(
                        riders.find { it.id == participantResult.participantId }!!,
                        position = participantResult.position,
                        participantResult.time,
                    )
                },
            )
        }

        ResultsMode.General -> Results.RidersTimeResult(
            stage.generalResults.time.map { participantResult ->
                RiderTimeResult(
                    riders.find { it.id == participantResult.participantId }!!,
                    position = participantResult.position,
                    participantResult.time,
                )
            },
        )
    }

    private fun pointsResults(
        resultsMode: ResultsMode,
        stage: Stage,
        riders: List<Rider>
    ): Results = when (resultsMode) {
        ResultsMode.Stage -> Results.RidersPointsPerPlaceResult(
            stage.stageResults.points.associate {
                it.place to it.points.map { riderResult ->
                    RiderPointsResult(
                        riders.find { rider -> rider.id == riderResult.participant }!!,
                        position = riderResult.position,
                        riderResult.points,
                    )
                }
            },
        )

        ResultsMode.General -> Results.RidersPointResult(
            stage.generalResults.points.map { participantResult ->
                RiderPointsResult(
                    riders.find { it.id == participantResult.participant }!!,
                    position = participantResult.position,
                    participantResult.points,
                )
            },
        )
    }

    internal fun onStageSelected(stageIndex: Int) {
        viewModelScope.launch {
            _stageIndex.emit(stageIndex)
        }
    }

    internal fun onResultsModeChanged(resultsMode: ResultsMode) {
        viewModelScope.launch {
            _resultsMode.emit(resultsMode)
        }
    }

    internal fun onClassificationTypeChanged(classificationType: ClassificationType) {
        viewModelScope.launch {
            _classificationType.emit(classificationType)
        }
    }
}
