package compose.project.demo.ui.race_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Race
import compose.project.demo.domain.firebaseDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn

class RaceDetailsViewModel(private val dataRepository: DataRepository = firebaseDataRepository) :
    ViewModel() {

    sealed class UiState {
        data class RaceDetails(val race: Race) : UiState()
    }

    fun uiState(raceId: String, stageId: String?): StateFlow<UiState?> =
        dataRepository.races
            .mapNotNull { races ->
                races.find { it.id == raceId }
            }
            .map { race ->
                UiState.RaceDetails(race)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null,
            )

}