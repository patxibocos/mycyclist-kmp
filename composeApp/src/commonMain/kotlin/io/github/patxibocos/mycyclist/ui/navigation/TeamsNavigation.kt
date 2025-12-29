package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.ui.team.details.TeamDetailsScreen
import io.github.patxibocos.mycyclist.ui.team.details.TeamDetailsViewModel
import io.github.patxibocos.mycyclist.ui.team.list.TeamListScreen
import io.github.patxibocos.mycyclist.ui.team.list.TeamListViewModel

@Composable
internal fun TeamDetails(
    teamId: String,
    onBackPressed: () -> Unit,
    onRiderSelected: (Rider) -> Unit,
    viewModel: TeamDetailsViewModel = viewModel(key = teamId) { TeamDetailsViewModel(teamId = teamId) },
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        ?: return
    Surface(modifier = Modifier.fillMaxSize()) {
        TeamDetailsScreen(
            uiState = uiState,
            onBackPressed = onBackPressed,
            onRiderSelected = onRiderSelected,
        )
    }
}

@Composable
internal fun TeamList(
    worldTeamsLazyListState: LazyListState = rememberLazyListState(),
    proTeamsLazyListState: LazyListState = rememberLazyListState(),
    viewModel: TeamListViewModel = viewModel { TeamListViewModel() },
    onTeamClick: (Team) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        ?: return
    Surface(modifier = Modifier.fillMaxSize()) {
        TeamListScreen(
            uiState = uiState,
            worldTeamsLazyListState = worldTeamsLazyListState,
            proTeamsLazyListState = proTeamsLazyListState,
            onTeamClick = onTeamClick,
            onRefresh = viewModel::refresh,
        )
    }
}
