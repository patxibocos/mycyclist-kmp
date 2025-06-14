package io.github.patxibocos.mycyclist.ui.team.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.domain.entity.TeamStatus
import io.github.patxibocos.mycyclist.domain.repository.CyclingDataRepository
import io.github.patxibocos.mycyclist.domain.repository.cyclingDataRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

internal class TeamListViewModel(
    private val dataRepository: CyclingDataRepository = cyclingDataRepository,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default
) : ViewModel() {

    private val _refreshing = MutableStateFlow(false)

    internal data class UiState(
        val worldTeams: ImmutableList<Team>,
        val proTeams: ImmutableList<Team>,
        val refreshing: Boolean,
    )

    internal val uiState: StateFlow<UiState?> =
        combine(dataRepository.cyclingData, _refreshing) { (races, teams, riders), refreshing ->
            withContext(defaultDispatcher) {
                val worldTeams = teams.filter { it.status == TeamStatus.WORLD_TEAM }
                val proTeams = teams.filter { it.status == TeamStatus.PRO_TEAM }
                UiState(
                    worldTeams = worldTeams.toImmutableList(),
                    proTeams = proTeams.toImmutableList(),
                    refreshing = refreshing,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    internal fun refresh() {
        viewModelScope.launch {
            _refreshing.value = true
            val refreshTime = measureTime {
                dataRepository.refresh()
            }
            // Show loading at least for a second
            delay(1.seconds.minus(refreshTime))
            _refreshing.value = false
        }
    }
}
