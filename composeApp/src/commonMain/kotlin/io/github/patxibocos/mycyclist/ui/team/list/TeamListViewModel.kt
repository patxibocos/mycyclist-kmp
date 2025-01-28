package io.github.patxibocos.mycyclist.ui.team.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.domain.TeamStatus
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class TeamListViewModel(
    dataRepository: DataRepository = firebaseDataRepository,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default
) : ViewModel() {

    internal data class UiState(
        val worldTeams: ImmutableList<Team>,
        val proTeams: ImmutableList<Team>,
    )

    internal val uiState: StateFlow<UiState?> =
        dataRepository.teams.map { teams ->
            withContext(defaultDispatcher) {
                val worldTeams = teams.filter { it.status == TeamStatus.WORLD_TEAM }
                val proTeams = teams.filter { it.status == TeamStatus.PRO_TEAM }
                UiState(worldTeams.toImmutableList(), proTeams.toImmutableList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}
