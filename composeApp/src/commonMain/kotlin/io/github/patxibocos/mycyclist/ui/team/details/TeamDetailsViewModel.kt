package io.github.patxibocos.mycyclist.ui.team.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class TeamDetailsViewModel(
    private val teamId: String,
    dataRepository: DataRepository = firebaseDataRepository,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
) :
    ViewModel() {

    internal data class UiState(val team: Team, val riders: ImmutableList<Rider>)

    internal val uiState: StateFlow<UiState?> =
        combine(dataRepository.teams, dataRepository.riders) { teams, riders ->
            withContext(defaultDispatcher) {
                val team = teams.find { it.id == teamId }!!
                val teamRiders = riders.filter { team.riderIds.contains(it.id) }.toImmutableList()
                UiState(team, teamRiders)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}
