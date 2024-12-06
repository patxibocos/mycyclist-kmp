package compose.project.demo.ui.team_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Rider
import compose.project.demo.domain.Team
import compose.project.demo.domain.firebaseDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TeamDetailsViewModel(private val dataRepository: DataRepository = firebaseDataRepository) :
    ViewModel() {

    data class UiState(val team: Team, val riders: List<Rider>)

    fun uiState(teamId: String): StateFlow<UiState?> =
        combine(dataRepository.teams, dataRepository.riders) { teams, riders ->
            val team = teams.find { it.id == teamId }!!
            val teamRiders = riders.filter { team.riderIds.contains(it.id) }
            UiState(team, teamRiders)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}