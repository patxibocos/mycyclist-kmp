package io.github.patxibocos.mycyclist.ui.team.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class TeamListViewModel(dataRepository: DataRepository = firebaseDataRepository) :
    ViewModel() {

    internal data class UiState(val teams: ImmutableList<Team>)

    internal val uiState: StateFlow<UiState?> =
        dataRepository.teams.map { teams ->
            UiState(teams.toImmutableList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}
