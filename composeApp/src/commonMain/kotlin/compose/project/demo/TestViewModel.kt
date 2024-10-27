package compose.project.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.data.protobuf.CyclingData
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class TestViewModel : ViewModel() {

    data class UiState(val counter: Int, val text: String = "")

    private val _uiState = MutableStateFlow(UiState(counter = 0))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalSerializationApi::class, ExperimentalEncodingApi::class)
    fun clicked() {
        val value = _uiState.value
        _uiState.tryEmit(value.copy(counter = value.counter + 1))
        viewModelScope.launch {
            Firebase.remoteConfig.fetchAndActivate()
            val configValue = Firebase.remoteConfig.getValue("cycling_data").asString()
            val unzipped = unGZip(Base64.decode(configValue))
            val data = ProtoBuf.decodeFromByteArray<CyclingData>(unzipped)
            _uiState.tryEmit(value.copy(text = data.teams.first().id))
        }
    }

}