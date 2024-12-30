package io.github.patxibocos.mycyclist.ui.rider.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListViewModel.UiState.Riders
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class RiderListViewModel(
    dataRepository: DataRepository = firebaseDataRepository,
    defaultDispatcher: CoroutineContext = Dispatchers.Default,
) :
    ViewModel() {

    private val _search = MutableStateFlow("")
    private val _searching = MutableStateFlow(false)
    private val _sorting = MutableStateFlow(Sorting.UciRanking)

    data class UiState(val riders: Riders) {

        sealed class Riders {
            data class ByLastName(val riders: ImmutableMap<Char, List<Rider>>) : Riders()
            data class ByCountry(val riders: ImmutableMap<String, List<Rider>>) : Riders()
            data class ByUciRanking(val riders: ImmutableList<Rider>) : Riders()
        }
    }

    data class TopBarState(val search: String, val searching: Boolean, val sorting: Sorting) {
        companion object {
            val Empty = TopBarState(search = "", searching = false, sorting = Sorting.UciRanking)
        }
    }

    enum class Sorting {
        LastName,
        Country,
        UciRanking,
    }

    internal val uiState =
        combine(
            dataRepository.riders,
            _search,
            _sorting,
        ) { riders, query, sorting ->
            val filteredRiders = searchRiders(defaultDispatcher, riders, query)
            val groupedRiders = sortRiders(defaultDispatcher, filteredRiders, sorting)
            UiState(groupedRiders)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    internal val topBarState: StateFlow<TopBarState> =
        combine(_search, _searching, _sorting) { search, searching, sorting ->
            TopBarState(search, searching, sorting)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TopBarState.Empty,
        )

    internal fun onToggled() {
        _searching.value = !_searching.value
        if (!_searching.value) {
            _search.value = ""
        }
    }

    internal fun onSearched(query: String) {
        _search.value = query
    }

    internal fun onSorted(sorting: Sorting) {
        _sorting.value = sorting
    }

    private suspend fun searchRiders(
        defaultDispatcher: CoroutineContext,
        riders: List<Rider>,
        query: String,
    ): List<Rider> = withContext(defaultDispatcher) {
        if (query.isBlank()) {
            return@withContext riders
        }
        val querySplits = query.trim().split(" ").map { it.trim() }
        riders.filter { rider ->
            // For each of the split, it should be contained either on first or last name
            querySplits.all { q ->
                rider.firstName.contains(
                    q,
                    ignoreCase = true,
                ) || rider.lastName.contains(q, ignoreCase = true)
            }
        }
    }

    private suspend fun sortRiders(
        defaultDispatcher: CoroutineContext,
        filteredRiders: List<Rider>,
        sorting: Sorting,
    ): Riders = withContext(defaultDispatcher) {
        when (sorting) {
            Sorting.LastName -> Riders.ByLastName(
                filteredRiders.sortedWith(
                    compareBy<Rider> { it.lastName }
                        .thenBy { it.firstName }
                ).groupBy {
                    it.lastName.first().uppercaseChar()
                }.toImmutableMap(),
            )

            Sorting.Country -> Riders.ByCountry(
                filteredRiders.sortedWith(
                    compareBy<Rider> { it.country }
                        .thenBy { it.lastName }
                        .thenBy { it.firstName }
                ).groupBy { it.country }.toImmutableMap()
            )

            Sorting.UciRanking -> Riders.ByUciRanking(
                filteredRiders
                    .sortedBy { it.uciRankingPosition ?: Int.MAX_VALUE }
                    .toImmutableList()
            )
        }
    }
}
