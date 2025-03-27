package io.github.patxibocos.mycyclist.domain

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class ListRiderResults(
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
) {

    private companion object {
        private const val RESULTS_TO_DISPLAY = 3
    }

    internal suspend operator fun invoke(
        riderId: String,
        participations: List<Participation>,
    ): ImmutableList<RiderResult> =
        withContext(defaultDispatcher) {
            participations.map { it.race }
                .flatMap { race ->
                    val raceResult =
                        race.result()?.take(RESULTS_TO_DISPLAY)
                            ?.find { it.participantId == riderId }
                            ?.let { RiderResult.RaceResult(race, it.position) }
                    if (race.isSingleDay()) {
                        return@flatMap listOfNotNull(raceResult)
                    }
                    val stageResults = race.stages.mapNotNull { stage ->
                        stage.stageResults.time.take(RESULTS_TO_DISPLAY)
                            .find { it.participantId == riderId }
                            ?.let {
                                RiderResult.StageResult(
                                    race = race,
                                    stage = stage,
                                    stageNumber = race.stages.indexOf(stage) + 1,
                                    position = it.position,
                                )
                            }
                    }
                    return@flatMap stageResults + listOfNotNull(raceResult)
                }.toImmutableList()
        }
}
