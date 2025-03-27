package io.github.patxibocos.mycyclist.domain

import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.coroutines.CoroutineContext

internal class ListRiderParticipations(
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
) {

    internal suspend operator fun invoke(
        riderId: String,
        races: List<Race>,
    ): RiderParticipations =
        withContext(defaultDispatcher) {
            val participations = races.mapNotNull { race ->
                race.teamParticipations
                    .flatMap { it.riderParticipations } // Flattening this because team IDs may change on PCS
                    .find { it.riderId == riderId }
                    ?.let { Participation(race, it.number) }
            }
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val currentParticipation =
                participations.find { it.race.startDate() <= today && it.race.endDate() >= today }
            val pastParticipations =
                participations.filter { it.race.endDate() < today }.toImmutableList()
            val futureParticipations =
                participations.filter { it.race.startDate() > today }.toImmutableList()
            RiderParticipations(
                pastParticipations = pastParticipations,
                currentParticipation = currentParticipation,
                futureParticipations = futureParticipations,
            )
        }
}
