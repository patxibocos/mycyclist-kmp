package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.ui.rider.details.RiderDetailsScreen
import io.github.patxibocos.mycyclist.ui.rider.details.RiderDetailsViewModel
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListScreen
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListViewModel

@Composable
internal fun RiderDetails(
    riderId: String,
    onBackPressed: () -> Unit,
    onRaceSelected: (Race) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
    viewModel: RiderDetailsViewModel = viewModel(key = riderId) { RiderDetailsViewModel(riderId = riderId) },
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return
    Surface(modifier = Modifier.fillMaxSize()) {
        RiderDetailsScreen(
            uiState = uiState,
            onBackPressed = onBackPressed,
            onRaceSelected = onRaceSelected,
            onTeamSelected = onTeamSelected,
            onStageSelected = onStageSelected,
        )
    }
}

@Composable
internal fun RiderList(
    listState: LazyListState = rememberLazyListState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onRiderClick: (Rider) -> Unit,
    viewModel: RiderListViewModel = viewModel { RiderListViewModel() },
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        ?: return
    val topBarState by viewModel.topBarState.collectAsStateWithLifecycle()
    Surface(modifier = Modifier.fillMaxSize()) {
        RiderListScreen(
            uiState = uiState,
            topBarState = topBarState,
            listState = listState,
            scrollBehavior = scrollBehavior,
            onRiderClick = onRiderClick,
            onRiderSearched = viewModel::onSearched,
            onToggled = viewModel::onToggled,
            onSortingSelected = viewModel::onSorted,
            onRefresh = viewModel::refresh,
        )
    }
}
