package compose.project.demo.ui.riders_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Rider
import compose.project.demo.domain.firebaseDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RidersListViewModel(dataRepository: DataRepository = firebaseDataRepository) :
    ViewModel() {

    data class UiState(val riders: List<Rider>)

    val uiState =
        dataRepository.riders.map { value ->
            UiState(value)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

}