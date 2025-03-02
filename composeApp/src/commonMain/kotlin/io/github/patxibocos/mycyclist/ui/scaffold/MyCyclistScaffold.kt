package io.github.patxibocos.mycyclist.ui.scaffold

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun MyCyclistScaffold() {
    val navController by rememberUpdatedState(rememberNavController())

    Scaffold { paddingValues ->
        SharedTransitionLayout(
            modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues)
        ) {
            NavigationSuite(navController)
        }
    }
}

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
private fun NavigationSuite(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationSuiteScaffold(modifier = Modifier.fillMaxSize(), navigationSuiteItems = {
        item(
            selected = currentDestination?.hasRoute(NavigationRoutes.Races::class) == true,
            onClick = {
                navController.navigate(NavigationRoutes.Races(raceId = null))
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Flag,
                    contentDescription = null,
                )
            }
        )
        item(
            selected = currentDestination?.hasRoute(NavigationRoutes.Riders::class) == true,
            onClick = {
                navController.navigate(NavigationRoutes.Riders(riderId = null))
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                )
            }
        )
        item(
            selected = currentDestination?.hasRoute(NavigationRoutes.Teams::class) == true,
            onClick = {
                navController.navigate(NavigationRoutes.Teams(teamId = null))
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                )
            }
        )
    }) {
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.Races(raceId = null),
        ) {
            composable<NavigationRoutes.Races> {
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

                val coroutineScope = rememberCoroutineScope()
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
                        AnimatedPane {
                            val viewModel = viewModel { RaceListViewModel() }
                            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                                ?: return@AnimatedPane
                            Surface(modifier = Modifier.fillMaxSize()) {
                                RaceListScreen(
                                    uiState = uiState,
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
            composable<NavigationRoutes.Riders> {
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

                val coroutineScope = rememberCoroutineScope()
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
                        AnimatedPane {
                            val viewModel = viewModel { RiderListViewModel() }
                            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                                ?: return@AnimatedPane
                            val topBarState by viewModel.topBarState.collectAsStateWithLifecycle()
                            Surface(modifier = Modifier.fillMaxSize()) {
                                RiderListScreen(
                                    animatedVisibilityScope = this@composable,
                                    onRiderClick = { rider ->
                                        coroutineScope.launch {
                                            navigator.navigateTo(
                                                ListDetailPaneScaffoldRole.Detail,
                                                rider.id
                                            )
                                        }
                                    },
                                    uiState = uiState,
                                    topBarState = topBarState,
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
            composable<NavigationRoutes.Teams> {
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

                val coroutineScope = rememberCoroutineScope()
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
                        AnimatedPane {
                            val viewModel = viewModel { TeamListViewModel() }
                            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                                ?: return@AnimatedPane
                            Surface(modifier = Modifier.fillMaxSize()) {
                                TeamListScreen(
                                    uiState = uiState,
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
