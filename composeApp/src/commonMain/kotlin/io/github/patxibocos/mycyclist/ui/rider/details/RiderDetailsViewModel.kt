package io.github.patxibocos.mycyclist.ui.rider.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.coroutines.CoroutineContext

internal class RiderDetailsViewModel(
    dataRepository: DataRepository = firebaseDataRepository,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
    riderId: String,
) : ViewModel() {

    private companion object {
        private const val RESULTS_TO_DISPLAY = 3
    }

    data class UiState(
        val rider: Rider,
        val team: Team?,
        val currentParticipation: Participation?,
        val pastParticipations: List<Participation>,
        val futureParticipations: ImmutableList<Participation>,
        val results: ImmutableList<Result>,
    )

    internal val uiState: StateFlow<UiState?> =
        dataRepository.cyclingData.map { (races, teams, riders) ->
            withContext(defaultDispatcher) {
                val rider = riders.find { it.id == riderId }!!
                val team = teams.find { it.riderIds.contains(riderId) }
                val (pastParticipations, currentParticipation, futureParticipations) = riderParticipations(
                    riderId,
                    races,
                )
                val results = riderResults(
                    riderId,
                    pastParticipations + listOfNotNull(currentParticipation),
                )
                UiState(
                    rider = rider,
                    team = team,
                    currentParticipation = currentParticipation,
                    pastParticipations = pastParticipations,
                    futureParticipations = futureParticipations,
                    results = results,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    private fun riderParticipations(
        riderId: String,
        races: List<Race>,
    ): Triple<ImmutableList<Participation>, Participation?, ImmutableList<Participation>> {
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
        return Triple(pastParticipations, currentParticipation, futureParticipations)
    }

    private fun riderResults(
        riderId: String,
        participations: List<Participation>,
    ): ImmutableList<Result> {
        return participations.map { it.race }
            .flatMap { race ->
                val raceResult =
                    race.result()?.take(RESULTS_TO_DISPLAY)?.find { it.participantId == riderId }
                        ?.let { Result.RaceResult(race, it.position) }
                if (race.isSingleDay()) {
                    return@flatMap listOfNotNull(raceResult)
                }
                val stageResults = race.stages.mapNotNull { stage ->
                    stage.stageResults.time.take(RESULTS_TO_DISPLAY)
                        .find { it.participantId == riderId }
                        ?.let {
                            Result.StageResult(
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

    data class Participation(val race: Race, val number: Int)

    sealed class Result(open val race: Race, open val position: Int) {

        data class RaceResult(override val race: Race, override val position: Int) :
            Result(race, position)

        data class StageResult(
            override val race: Race,
            val stage: Stage,
            val stageNumber: Int,
            override val position: Int,
        ) : Result(race, position)
    }
}
