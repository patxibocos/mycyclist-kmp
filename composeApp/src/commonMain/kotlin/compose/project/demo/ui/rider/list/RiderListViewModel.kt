package compose.project.demo.ui.rider.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Rider
import compose.project.demo.domain.firebaseDataRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class RiderListViewModel(dataRepository: DataRepository = firebaseDataRepository) :
    ViewModel() {

    data class UiState(val riders: ImmutableList<Rider>)

    internal val uiState =
        dataRepository.riders.map { riders ->
            UiState(riders.toImmutableList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}
