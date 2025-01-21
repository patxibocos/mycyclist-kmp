package io.github.patxibocos.mycyclist.ui.team.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class TeamDetailsViewModel(
    private val teamId: String,
    dataRepository: DataRepository = firebaseDataRepository
) :
    ViewModel() {

    internal data class UiState(val team: Team, val riders: ImmutableList<Rider>)

    internal val uiState: StateFlow<UiState?> =
        combine(dataRepository.teams, dataRepository.riders) { teams, riders ->
            val team = teams.find { it.id == teamId }!!
            val teamRiders = riders.filter { team.riderIds.contains(it.id) }
            UiState(team, teamRiders.toImmutableList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}
