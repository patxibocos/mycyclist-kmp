package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.ui.team.details.TeamDetailsScreen
import io.github.patxibocos.mycyclist.ui.team.details.TeamDetailsViewModel
import io.github.patxibocos.mycyclist.ui.team.list.TeamListScreen
import io.github.patxibocos.mycyclist.ui.team.list.TeamListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
internal fun NavGraphBuilder.teamsRoute(
    tabReselected: MutableSharedFlow<NavigationRoutes>,
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
        val worldTeamsLazyGridState = rememberLazyGridState()
        val proTeamsLazyGridState = rememberLazyGridState()
        LaunchedEffect(Unit) {
            tabReselected.filterIsInstance<NavigationRoutes.Teams>().collect {
                if (navigator.canNavigateBack()) {
                    navigator.navigateBack()
                } else {
                    coroutineScope.launch {
                        worldTeamsLazyGridState.animateScrollToItem(0)
                        proTeamsLazyGridState.animateScrollToItem(0)
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
            worldTeamsLazyGridState = worldTeamsLazyGridState,
            proTeamsLazyGridState = proTeamsLazyGridState,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun Scaffold(
    navigator: ThreePaneScaffoldNavigator<String>,
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    worldTeamsLazyGridState: LazyGridState,
    proTeamsLazyGridState: LazyGridState,
) {
    ListDetailPaneScaffold(
        modifier = Modifier.fillMaxSize(),
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                TeamList(
                    worldTeamsLazyGridState = worldTeamsLazyGridState,
                    proTeamsLazyGridState = proTeamsLazyGridState,
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
                TeamDetails(
                    navigator = navigator,
                    teamId = navigator.currentDestination?.contentKey ?: return@AnimatedPane,
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
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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
    worldTeamsLazyGridState: LazyGridState,
    proTeamsLazyGridState: LazyGridState,
    viewModel: TeamListViewModel = viewModel { TeamListViewModel() },
    onTeamClick: (Team) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        ?: return
    Surface(modifier = Modifier.fillMaxSize()) {
        TeamListScreen(
            uiState = uiState,
            worldTeamsLazyGridState = worldTeamsLazyGridState,
            proTeamsLazyGridState = proTeamsLazyGridState,
            onTeamClick = onTeamClick,
            onRefresh = viewModel::refresh,
        )
    }
}
