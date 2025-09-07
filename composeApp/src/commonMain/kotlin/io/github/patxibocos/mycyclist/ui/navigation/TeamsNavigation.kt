package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.ui.team.details.TeamDetailsScreen
import io.github.patxibocos.mycyclist.ui.team.details.TeamDetailsViewModel
import io.github.patxibocos.mycyclist.ui.team.list.TeamListScreen
import io.github.patxibocos.mycyclist.ui.team.list.TeamListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

internal fun NavGraphBuilder.teamsRoute(
    tabReselected: SharedFlow<NavigationRoutes>,
    coroutineScope: CoroutineScope,
    navController: NavHostController
) {
    composable<NavigationRoutes.Teams>(
        deepLinks = listOf(navDeepLink<NavigationRoutes.Teams>(NavigationRoutes.Teams.deepLink())),
    ) {
        val teams: NavigationRoutes.Teams = it.toRoute()
        val navigator = rememberListDetailPaneScaffoldNavigator(
            initialDestinationHistory = listOfNotNull(
                ThreePaneScaffoldDestinationItem(
                    ListDetailPaneScaffoldRole.List
                ),
                teams.teamId?.let { teamId ->
                    ThreePaneScaffoldDestinationItem(
                        ListDetailPaneScaffoldRole.Detail,
                        teamId,
                    )
                }
            ),
        )
        val worldTeamsLazyListState = rememberLazyListState()
        val proTeamsLazyListState = rememberLazyListState()
        LaunchedEffect(Unit) {
            tabReselected.filterIsInstance<NavigationRoutes.Teams>().collect {
                if (navigator.canNavigateBack()) {
                    navigator.navigateBack()
                } else {
                    coroutineScope.launch {
                        worldTeamsLazyListState.animateScrollToItem(0)
                        proTeamsLazyListState.animateScrollToItem(0)
                    }
                }
            }
        }
        BackHandler(navigator.canNavigateBack()) {
            coroutineScope.launch {
                navigator.navigateBack()
            }
        }
        Scaffold(
            navigator = navigator,
            coroutineScope = coroutineScope,
            navController = navController,
            worldTeamsLazyListState = worldTeamsLazyListState,
            proTeamsLazyListState = proTeamsLazyListState,
        )
    }
}

@Composable
private fun Scaffold(
    navigator: ThreePaneScaffoldNavigator<String>,
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    worldTeamsLazyListState: LazyListState,
    proTeamsLazyListState: LazyListState,
) {
    var lastValidTeamId by remember { mutableStateOf<String?>(null) }
    ListDetailPaneScaffold(
        modifier = Modifier.fillMaxSize(),
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                TeamList(
                    worldTeamsLazyListState = worldTeamsLazyListState,
                    proTeamsLazyListState = proTeamsLazyListState,
                    onTeamClick = { team ->
                        coroutineScope.launch {
                            navigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                team.id
                            )
                        }
                    },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val currentTeamId = navigator.currentDestination?.contentKey
                LaunchedEffect(currentTeamId) {
                    if (currentTeamId != null) {
                        lastValidTeamId = currentTeamId
                    }
                }
                val teamId = currentTeamId ?: lastValidTeamId
                if (teamId == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("No team selected")
                    }
                } else {
                    TeamDetails(
                        navigator = navigator,
                        teamId = teamId,
                        onBackPressed = {
                            coroutineScope.launch {
                                navigator.navigateBack()
                            }
                        },
                        onRiderSelected = { rider ->
                            navController.navigate(NavigationRoutes.Riders(rider.id))
                        },
                    )
                }
            }
        }
    )
}

@Composable
private fun TeamDetails(
    navigator: ThreePaneScaffoldNavigator<String>,
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
            backEnabled = navigator.canNavigateBack(),
            onBackPressed = onBackPressed,
            onRiderSelected = onRiderSelected,
        )
    }
}

@Composable
private fun TeamList(
    worldTeamsLazyListState: LazyListState,
    proTeamsLazyListState: LazyListState,
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
