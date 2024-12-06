package compose.project.demo.ui.teams_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Team
import compose.project.demo.domain.firebaseDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TeamsListViewModel(dataRepository: DataRepository = firebaseDataRepository) : ViewModel() {

    data class UiState(val teams: List<Team>)

    val uiState: StateFlow<UiState?> =
        dataRepository.teams.map { teams ->
            UiState(teams)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}