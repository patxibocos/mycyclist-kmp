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
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.ui.race.details.RaceDetailsScreen
import io.github.patxibocos.mycyclist.ui.race.details.RaceDetailsViewModel
import io.github.patxibocos.mycyclist.ui.race.list.RaceListScreen
import io.github.patxibocos.mycyclist.ui.race.list.RaceListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
internal fun NavGraphBuilder.racesRoute(
    tabReselected: SharedFlow<NavigationRoutes>,
    coroutineScope: CoroutineScope,
    navController: NavHostController
) {
    composable<NavigationRoutes.Races>(
        deepLinks = listOf(navDeepLink<NavigationRoutes.Races>(NavigationRoutes.Races.deepLink())),
    ) {
        val races: NavigationRoutes.Races = it.toRoute()
        val navigator = rememberListDetailPaneScaffoldNavigator(
            initialDestinationHistory = listOfNotNull(
                ThreePaneScaffoldDestinationItem(
                    ListDetailPaneScaffoldRole.List
                ),
                races.raceId?.let { raceId ->
                    ThreePaneScaffoldDestinationItem(
                        ListDetailPaneScaffoldRole.Detail,
                        raceId to races.stageId,
                    )
                }
            ),
        )
        val listState = rememberLazyListState()
        LaunchedEffect(Unit) {
            tabReselected.filterIsInstance<NavigationRoutes.Races>().collect {
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
    navigator: ThreePaneScaffoldNavigator<Pair<String, String?>>,
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
                RaceList(
                    listState = listState,
                    onRaceClick = { race ->
                        coroutineScope.launch {
                            navigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                race.id to null,
                            )
                        }
                    },
                    onRaceStageClick = { race, stage ->
                        coroutineScope.launch {
                            navigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                race.id to stage.id,
                            )
                        }
                    },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val raceId = navigator.currentDestination?.contentKey
                if (raceId == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("No race selected")
                    }
                } else {
                    RaceDetails(
                        navigator = navigator,
                        navController = navController,
                        raceAndStageId = raceId,
                        onBackPressed = {
                            coroutineScope.launch {
                                navigator.navigateBack()
                            }
                        },
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun RaceDetails(
    navigator: ThreePaneScaffoldNavigator<Pair<String, String?>>,
    navController: NavHostController,
    raceAndStageId: Pair<String, String?>,
    viewModel: RaceDetailsViewModel = viewModel(key = raceAndStageId.first) {
        RaceDetailsViewModel(
            raceId = raceAndStageId.first,
            stageId = raceAndStageId.second,
        )
    },
    onBackPressed: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return
    Surface(modifier = Modifier.fillMaxSize()) {
        RaceDetailsScreen(
            uiState = uiState,
            backEnabled = navigator.canNavigateBack(),
            onBackPressed = onBackPressed,
            onRiderSelected = { rider ->
                navController.navigate(NavigationRoutes.Riders(rider.id))
            },
            onTeamSelected = { team ->
                navController.navigate(NavigationRoutes.Teams(team.id))
            },
            onResultsModeChanged = viewModel::onResultsModeChanged,
            onClassificationTypeChanged = viewModel::onClassificationTypeChanged,
            onStageSelected = viewModel::onStageSelected,
            onParticipationsClicked = {},
        )
    }
}

@Composable
private fun RaceList(
    listState: LazyListState,
    onRaceClick: (Race) -> Unit,
    onRaceStageClick: (Race, Stage) -> Unit,
    viewModel: RaceListViewModel = viewModel { RaceListViewModel() },
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return
    Surface(modifier = Modifier.fillMaxSize()) {
        RaceListScreen(
            uiState = uiState,
            listState = listState,
            onRaceClick = onRaceClick,
            onRaceStageClick = onRaceStageClick,
            onRefresh = viewModel::refresh,
        )
    }
}
