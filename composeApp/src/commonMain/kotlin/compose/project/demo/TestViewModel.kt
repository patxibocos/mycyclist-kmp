package compose.project.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestViewModel : ViewModel() {

    data class UiState(val counter: Int, val text: String = "")

    private val _uiState = MutableStateFlow(UiState(counter = 0))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun clicked() {
        val value = _uiState.value
        _uiState.tryEmit(value.copy(counter = value.counter + 1))
        viewModelScope.launch {
            Firebase.remoteConfig.fetchAndActivate()
            val configValue = Firebase.remoteConfig.getValue("cycling_data").asString()
            _uiState.tryEmit(value.copy(text = configValue))
        }
    }

}