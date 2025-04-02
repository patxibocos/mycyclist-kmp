package io.github.patxibocos.mycyclist.domain.usecase

import io.github.patxibocos.mycyclist.domain.entity.Place
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.domain.entity.StageType
import io.github.patxibocos.mycyclist.domain.entity.Team
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal typealias StageResult = Pair<Stage, StageResults>

internal sealed interface StageResults {
    data class TeamsTimeResult(val teams: List<TeamTimeResult>) : StageResults
    data class RidersTimeResult(val riders: List<RiderTimeResult>) : StageResults
    data class RidersPointResult(val riders: List<RiderPointsResult>) : StageResults
    data class RidersPointsPerPlaceResult(val perPlaceResult: Map<Place, List<RiderPointsResult>>) :
        StageResults
}

internal data class RiderTimeResult(val rider: Rider, val position: Int, val time: Long)

internal data class TeamTimeResult(val team: Team, val position: Int, val time: Long)

internal data class RiderPointsResult(
    val rider: Rider,
    val position: Int,
    val points: Int,
)

internal enum class ClassificationType {
    Time,
    Points,
    Kom,
    Youth,
    Teams,
}

internal enum class ResultsMode {
    Stage,
    General,
}

internal class RaceResults(
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
) {

    internal suspend operator fun invoke(
        race: Race,
        resultsMode: ResultsMode,
        classificationType: ClassificationType,
        riders: List<Rider>,
        teams: List<Team>,
    ): ImmutableList<StageResult> = withContext(defaultDispatcher) {
        race.stages.map { stage ->
            stage to when (classificationType) {
                ClassificationType.Time -> timeResults(resultsMode, stage, teams, riders)
                ClassificationType.Points -> pointsResults(resultsMode, stage, riders)
                ClassificationType.Kom -> komResults(resultsMode, stage, riders)
                ClassificationType.Youth -> youthResults(resultsMode, stage, riders)
                ClassificationType.Teams -> teamsResults(resultsMode, stage, teams)
            }
        }.toImmutableList()
    }

    private fun timeResults(
        resultsMode: ResultsMode,
        stage: Stage,
        teams: List<Team>,
        riders: List<Rider>
    ): StageResults = when (resultsMode) {
        ResultsMode.Stage -> when (stage.stageType) {
            StageType.TEAM_TIME_TRIAL -> StageResults.TeamsTimeResult(
                stage.stageResults.time.map { participantResult ->
                    TeamTimeResult(
                        teams.find { it.id == participantResult.participantId }!!,
                        position = participantResult.position,
                        participantResult.time,
                    )
                },
            )

            else -> StageResults.RidersTimeResult(
                stage.stageResults.time.map { participantResult ->
                    RiderTimeResult(
                        riders.find { it.id == participantResult.participantId }!!,
                        position = participantResult.position,
                        participantResult.time,
                    )
                },
            )
        }

        ResultsMode.General -> StageResults.RidersTimeResult(
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
    ): StageResults = when (resultsMode) {
        ResultsMode.Stage -> StageResults.RidersPointsPerPlaceResult(
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

        ResultsMode.General -> StageResults.RidersPointResult(
            stage.generalResults.points.map { participantResult ->
                RiderPointsResult(
                    riders.find { it.id == participantResult.participant }!!,
                    position = participantResult.position,
                    participantResult.points,
                )
            },
        )
    }

    private fun teamsResults(
        resultsMode: ResultsMode,
        stage: Stage,
        teams: List<Team>
    ): StageResults.TeamsTimeResult = when (resultsMode) {
        ResultsMode.Stage -> StageResults.TeamsTimeResult(
            stage.stageResults.teams.map { participantResult ->
                TeamTimeResult(
                    teams.find { it.id == participantResult.participantId }!!,
                    position = participantResult.position,
                    participantResult.time,
                )
            },
        )

        ResultsMode.General -> StageResults.TeamsTimeResult(
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
    ): StageResults.RidersTimeResult = when (resultsMode) {
        ResultsMode.Stage -> StageResults.RidersTimeResult(
            stage.stageResults.youth.map { participantResult ->
                RiderTimeResult(
                    riders.find { it.id == participantResult.participantId }!!,
                    position = participantResult.position,
                    participantResult.time,
                )
            },
        )

        ResultsMode.General -> StageResults.RidersTimeResult(
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
    ): StageResults = when (resultsMode) {
        ResultsMode.Stage -> StageResults.RidersPointsPerPlaceResult(
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

        ResultsMode.General -> StageResults.RidersPointResult(
            stage.generalResults.kom.map { participantResult ->
                RiderPointsResult(
                    riders.find { it.id == participantResult.participant }!!,
                    position = participantResult.position,
                    participantResult.points,
                )
            },
        )
    }
}
