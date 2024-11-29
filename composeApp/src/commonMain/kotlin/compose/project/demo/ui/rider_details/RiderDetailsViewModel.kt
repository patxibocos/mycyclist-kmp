package compose.project.demo.ui.rider_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Rider
import compose.project.demo.domain.firebaseDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RiderDetailsViewModel(private val dataRepository: DataRepository = firebaseDataRepository) :
    ViewModel() {

    data class UiState(val rider: Rider)

    fun uiState(raceId: String): StateFlow<UiState?> {
        return dataRepository.riders.map { riders ->
            val rider = riders.find { it.id == raceId }!!
            UiState(rider)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
    }

}