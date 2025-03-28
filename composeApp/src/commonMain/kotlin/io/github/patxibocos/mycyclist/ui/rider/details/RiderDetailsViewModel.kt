package io.github.patxibocos.mycyclist.ui.rider.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.domain.repository.DataRepository
import io.github.patxibocos.mycyclist.domain.repository.firebaseDataRepository
import io.github.patxibocos.mycyclist.domain.usecase.ListRiderParticipations
import io.github.patxibocos.mycyclist.domain.usecase.ListRiderResults
import io.github.patxibocos.mycyclist.domain.usecase.Participation
import io.github.patxibocos.mycyclist.domain.usecase.RiderResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class RiderDetailsViewModel(
    dataRepository: DataRepository = firebaseDataRepository,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
    listRiderParticipations: ListRiderParticipations = ListRiderParticipations(),
    listRiderResults: ListRiderResults = ListRiderResults(),
    riderId: String,
) : ViewModel() {

    data class UiState(
        val rider: Rider,
        val team: Team?,
        val currentParticipation: Participation?,
        val pastParticipations: List<Participation>,
        val futureParticipations: ImmutableList<Participation>,
        val results: ImmutableList<RiderResult>,
    )

    internal val uiState: StateFlow<UiState?> =
        dataRepository.cyclingData.map { (races, teams, riders) ->
            withContext(defaultDispatcher) {
                val rider = riders.find { it.id == riderId }!!
                val team = teams.find { it.riderIds.contains(riderId) }
                val participations = listRiderParticipations(riderId, races)
                val results = listRiderResults(
                    riderId = riderId,
                    participations = participations.pastParticipations + listOfNotNull(
                        participations.currentParticipation
                    ),
                )
                UiState(
                    rider = rider,
                    team = team,
                    currentParticipation = participations.currentParticipation,
                    pastParticipations = participations.pastParticipations,
                    futureParticipations = participations.futureParticipations,
                    results = results,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}
