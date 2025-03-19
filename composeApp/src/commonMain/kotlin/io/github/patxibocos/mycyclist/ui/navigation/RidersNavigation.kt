package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.ui.rider.details.RiderDetailsScreen
import io.github.patxibocos.mycyclist.ui.rider.details.RiderDetailsViewModel
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListScreen
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
internal fun NavGraphBuilder.ridersRoute(
    tabReselected: SharedFlow<NavigationRoutes>,
    coroutineScope: CoroutineScope,
    navController: NavHostController
) {
    composable<NavigationRoutes.Riders>(
        deepLinks = listOf(navDeepLink<NavigationRoutes.Riders>(NavigationRoutes.Riders.deepLink())),
    ) {
        val riders: NavigationRoutes.Riders = it.toRoute()
        val navigator = rememberListDetailPaneScaffoldNavigator(
            initialDestinationHistory = listOfNotNull(
                ThreePaneScaffoldDestinationItem(
                    ListDetailPaneScaffoldRole.List
                ),
                riders.riderId?.let { raceId ->
                    ThreePaneScaffoldDestinationItem(
                        ListDetailPaneScaffoldRole.Detail,
                        raceId,
                    )
                }
            ),
        )
        val listState = rememberLazyListState()
        LaunchedEffect(Unit) {
            tabReselected.filterIsInstance<NavigationRoutes.Riders>().collect {
                if (navigator.canNavigateBack()) {
                    navigator.navigateBack()
                } else {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
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
            listState = listState,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun Scaffold(
    navigator: ThreePaneScaffoldNavigator<String>,
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    listState: LazyListState,
) {
    ListDetailPaneScaffold(
        modifier = Modifier.fillMaxSize(),
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                RiderList(
                    listState = listState,
                    onRiderClick = { rider ->
                        coroutineScope.launch {
                            navigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                rider.id
                            )
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val riderId = navigator.currentDestination?.contentKey
                if (riderId == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("No rider selected")
                    }
                } else {
                    RiderDetails(
                        navigator = navigator,
                        riderId = riderId,
                        onBackPressed = {
                            coroutineScope.launch {
                                navigator.navigateBack()
                            }
                        },
                        onRaceSelected = { race ->
                            navController.navigate(NavigationRoutes.Races(race.id))
                        },
                        onTeamSelected = { team ->
                            navController.navigate(NavigationRoutes.Teams(team.id))
                        },
                        onStageSelected = { race, stage ->
                            navController.navigate(
                                NavigationRoutes.Races(
                                    race.id,
                                    stage.id
                                )
                            )
                        },
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun RiderDetails(
    navigator: ThreePaneScaffoldNavigator<String>,
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
            backEnabled = navigator.canNavigateBack(),
            onBackPressed = onBackPressed,
            onRaceSelected = onRaceSelected,
            onTeamSelected = onTeamSelected,
            onStageSelected = onStageSelected,
        )
    }
}

@Composable
private fun RiderList(
    listState: LazyListState,
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
            onRiderClick = onRiderClick,
            onRiderSearched = viewModel::onSearched,
            onToggled = viewModel::onToggled,
            onSortingSelected = viewModel::onSorted,
            onRefresh = viewModel::refresh,
        )
    }
}
