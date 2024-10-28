package compose.project.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TestViewModel(dataRepository: DataRepository = compose.project.demo.dataRepository) :
    ViewModel() {

    data class UiState(val text: String = "")

    val uiState: StateFlow<UiState> =
        dataRepository.teams.map { teams ->
            UiState(text = teams.first().id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState()
        )

}