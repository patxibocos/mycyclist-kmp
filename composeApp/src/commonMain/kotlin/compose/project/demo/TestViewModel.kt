package compose.project.demo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestViewModel : ViewModel() {

    data class UiState(val counter: Int)

    private val _uiState = MutableStateFlow(UiState(counter = 0))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun clicked() {
        val value = _uiState.value
        _uiState.tryEmit(value.copy(counter = value.counter + 1))
    }

}