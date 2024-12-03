package compose.project.demo.ui.rider_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Race
import compose.project.demo.domain.Rider
import compose.project.demo.domain.Stage
import compose.project.demo.domain.Team
import compose.project.demo.domain.endDate
import compose.project.demo.domain.firebaseDataRepository
import compose.project.demo.domain.isSingleDay
import compose.project.demo.domain.result
import compose.project.demo.domain.startDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class RiderDetailsViewModel(private val dataRepository: DataRepository = firebaseDataRepository) :
    ViewModel() {

    data class UiState(
        val rider: Rider,
        val team: Team,
        val currentParticipation: Participation?,
        val pastParticipations: List<Participation>,
        val futureParticipations: List<Participation>,
        val results: List<Result>,
    )

    fun uiState(riderId: String): StateFlow<UiState?> =
        combine(
            dataRepository.teams,
            dataRepository.riders,
            dataRepository.races,
        ) { teams, riders, races ->
            val rider = riders.find { it.id == riderId }!!
            val team = teams.find { it.riderIds.contains(riderId) }!!
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
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    private fun riderParticipations(
        riderId: String,
        races: List<Race>,
    ): Triple<List<Participation>, Participation?, List<Participation>> {
        val participations = races.mapNotNull { race ->
            race.teamParticipations.flatMap { it.riderParticipations } // Flattening this because team IDs may change on PCS
                .find { it.riderId == riderId }
                ?.let { Participation(race, it.number) }
        }
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val currentParticipation =
            participations.find { it.race.startDate() <= today && it.race.endDate() >= today }
        val pastParticipations = participations.filter { it.race.endDate() < today }
        val futureParticipations = participations.filter { it.race.startDate() > today }
        return Triple(pastParticipations, currentParticipation, futureParticipations)
    }

    private fun riderResults(
        riderId: String,
        participations: List<Participation>,
    ): List<Result> {
        return participations.map { it.race }
            .flatMap { race ->
                val raceResult = race.result()?.take(3)?.find { it.participantId == riderId }
                    ?.let { Result.RaceResult(race, it.position) }
                if (race.isSingleDay()) {
                    return@flatMap listOfNotNull(raceResult)
                }
                val stageResults = race.stages.mapNotNull { stage ->
                    stage.stageResults.time.take(3).find { it.participantId == riderId }
                        ?.let {
                            Result.StageResult(
                                race,
                                stage,
                                race.stages.indexOf(stage) + 1,
                                it.position,
                            )
                        }
                }
                return@flatMap stageResults + listOfNotNull(raceResult)
            }
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