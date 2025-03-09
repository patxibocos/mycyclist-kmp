package io.github.patxibocos.mycyclist.ui.scaffold

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import io.github.patxibocos.mycyclist.ui.navigation.NavigationRoutes
import io.github.patxibocos.mycyclist.ui.race.details.RaceDetailsScreen
import io.github.patxibocos.mycyclist.ui.race.details.RaceDetailsViewModel
import io.github.patxibocos.mycyclist.ui.race.list.RaceListScreen
import io.github.patxibocos.mycyclist.ui.race.list.RaceListViewModel
import io.github.patxibocos.mycyclist.ui.rider.details.RiderDetailsScreen
import io.github.patxibocos.mycyclist.ui.rider.details.RiderDetailsViewModel
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListScreen
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListViewModel
import io.github.patxibocos.mycyclist.ui.team.details.TeamDetailsScreen
import io.github.patxibocos.mycyclist.ui.team.details.TeamDetailsViewModel
import io.github.patxibocos.mycyclist.ui.team.list.TeamListScreen
import io.github.patxibocos.mycyclist.ui.team.list.TeamListViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

@Composable
internal fun MyCyclistScaffold() {
    val navController by rememberUpdatedState(rememberNavController())

    Scaffold { paddingValues ->
        NavigationSuite(
            navController = navController,
            modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues),
        )
    }
}

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
private fun NavigationSuite(
    navController: NavHostController,
    modifier: Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val routes = remember {
        listOf(
            TopLevelRoute(
                route = NavigationRoutes.Races(),
                unselectedIcon = Icons.Outlined.Flag,
                selectedIcon = Icons.Filled.Flag,
                title = "Races"
            ),
            TopLevelRoute(
                route = NavigationRoutes.Riders(),
                unselectedIcon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person,
                title = "Riders"
            ),
            TopLevelRoute(
                route = NavigationRoutes.Teams(),
                unselectedIcon = Icons.Outlined.Group,
                selectedIcon = Icons.Filled.Group,
                title = "Teams"
            ),
        )
    }

    val tabReselected = remember { MutableSharedFlow<NavigationRoutes>() }

    val coroutineScope = rememberCoroutineScope()
    NavigationSuiteScaffold(modifier = modifier.fillMaxSize(), navigationSuiteItems = {
        routes.forEach { route ->
            val isRouteSelected = currentDestination?.hasRoute(route.route::class) == true
            item(
                selected = isRouteSelected,
                onClick = {
                    if (!isRouteSelected) {
                        navController.navigate(route.route) {
                            launchSingleTop = true
                        }
                    } else {
                        coroutineScope.launch {
                            tabReselected.emit(route.route)
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isRouteSelected) route.selectedIcon else route.unselectedIcon,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(text = route.title)
                }
            )
        }
    }) {
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.Races(),
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
                LaunchedEffect(Unit) {
                    tabReselected.filterIsInstance<NavigationRoutes.Races>().collect {
                        if (navigator.canNavigateBack()) {
                            navigator.navigateBack()
                        }
                    }
                }
                BackHandler(navigator.canNavigateBack()) {
                    coroutineScope.launch {
                        navigator.navigateBack()
                    }
                }
                ListDetailPaneScaffold(
                    modifier = Modifier.fillMaxSize(),
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        val listState = rememberLazyListState()
                        AnimatedPane {
                            val viewModel = viewModel { RaceListViewModel() }
                            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                                ?: return@AnimatedPane
                            Surface(modifier = Modifier.fillMaxSize()) {
                                RaceListScreen(
                                    uiState = uiState,
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
                                    onRefresh = viewModel::refresh,
                                )
                            }
                        }
                    },
                    detailPane = {
                        AnimatedPane {
                            val (raceId, stageId) =
                                navigator.currentDestination?.contentKey ?: return@AnimatedPane
                            val viewModel =
                                viewModel(key = raceId) {
                                    RaceDetailsViewModel(raceId = raceId, stageId = stageId)
                                }
                            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                                ?: return@AnimatedPane
                            Surface(modifier = Modifier.fillMaxSize()) {
                                RaceDetailsScreen(
                                    uiState = uiState,
                                    backEnabled = true,
                                    onBackPressed = {
                                        coroutineScope.launch {
                                            navigator.navigateBack()
                                        }
                                    },
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
                    },
                )
            }
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
                LaunchedEffect(Unit) {
                    tabReselected.filterIsInstance<NavigationRoutes.Riders>().collect {
                        if (navigator.canNavigateBack()) {
                            navigator.navigateBack()
                        }
                    }
                }
                BackHandler(navigator.canNavigateBack()) {
                    coroutineScope.launch {
                        navigator.navigateBack()
                    }
                }
                ListDetailPaneScaffold(
                    modifier = Modifier.fillMaxSize(),
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        val listState = rememberLazyListState()
                        AnimatedPane {
                            val viewModel = viewModel { RiderListViewModel() }
                            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                                ?: return@AnimatedPane
                            val topBarState by viewModel.topBarState.collectAsStateWithLifecycle()
                            Surface(modifier = Modifier.fillMaxSize()) {
                                RiderListScreen(
                                    uiState = uiState,
                                    topBarState = topBarState,
                                    listState = listState,
                                    onRiderClick = { rider ->
                                        coroutineScope.launch {
                                            navigator.navigateTo(
                                                ListDetailPaneScaffoldRole.Detail,
                                                rider.id
                                            )
                                        }
                                    },
                                    onRiderSearched = viewModel::onSearched,
                                    onToggled = viewModel::onToggled,
                                    onSortingSelected = viewModel::onSorted,
                                    onRefresh = viewModel::refresh,
                                )
                            }
                        }
                    },
                    detailPane = {
                        AnimatedPane {
                            val riderId =
                                navigator.currentDestination?.contentKey ?: return@AnimatedPane
                            val viewModel: RiderDetailsViewModel =
                                viewModel(key = riderId) { RiderDetailsViewModel(riderId = riderId) }
                            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                                ?: return@AnimatedPane
                            Surface(modifier = Modifier.fillMaxSize()) {
                                RiderDetailsScreen(
                                    uiState = uiState,
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
                LaunchedEffect(Unit) {
                    tabReselected.filterIsInstance<NavigationRoutes.Teams>().collect {
                        if (navigator.canNavigateBack()) {
                            navigator.navigateBack()
                        }
                    }
                }
                BackHandler(navigator.canNavigateBack()) {
                    coroutineScope.launch {
                        navigator.navigateBack()
                    }
                }
                ListDetailPaneScaffold(
                    modifier = Modifier.fillMaxSize(),
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        val worldTeamsLazyGridState = rememberLazyGridState()
                        val proTeamsLazyGridState = rememberLazyGridState()
                        AnimatedPane {
                            val viewModel = viewModel { TeamListViewModel() }
                            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                                ?: return@AnimatedPane
                            Surface(modifier = Modifier.fillMaxSize()) {
                                TeamListScreen(
                                    uiState = uiState,
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
                                    onRefresh = viewModel::refresh,
                                )
                            }
                        }
                    },
                    detailPane = {
                        AnimatedPane {
                            val teamId =
                                navigator.currentDestination?.contentKey ?: return@AnimatedPane
                            val viewModel =
                                viewModel(key = teamId) { TeamDetailsViewModel(teamId = teamId) }
                            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                                ?: return@AnimatedPane
                            Surface(modifier = Modifier.fillMaxSize()) {
                                TeamDetailsScreen(
                                    uiState = uiState,
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
        }
    }
}
