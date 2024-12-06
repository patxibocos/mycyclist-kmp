package compose.project.demo.ui.scaffold

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import compose.project.demo.ui.navigation.NavigationRoutes
import compose.project.demo.ui.race_details.RaceDetailsRoute
import compose.project.demo.ui.races_list.RacesListRoute
import compose.project.demo.ui.rider_details.RiderDetailsRoute
import compose.project.demo.ui.riders_list.RidersListRoute
import compose.project.demo.ui.team_details.TeamDetailsRoute
import compose.project.demo.ui.teams_list.TeamsListRoute

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MyCyclistScaffold() {
    val navController by rememberUpdatedState(rememberNavController())

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) {
        SharedTransitionLayout {
            NavHost(
                navController = navController,
                startDestination = NavigationRoutes.RacesList,
                modifier = Modifier.padding(it)
            ) {
                composable<NavigationRoutes.RacesList>(
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = NavigationRoutes.RacesList.deepLinkRoute()
                        }
                    ),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) {
                    RacesListRoute(
                        onRaceClick = { race ->
                            navController.navigate(
                                NavigationRoutes.RaceDetails(
                                    race.id,
                                    null
                                )
                            )
                        }
                    )
                }
                composable<NavigationRoutes.RidersList>(
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = NavigationRoutes.RidersList.deepLinkRoute()
                        }
                    ),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) {
                    RidersListRoute(
                        animatedVisibilityScope = this@composable,
                        onRiderClick = { rider ->
                            navController.navigate(
                                NavigationRoutes.RiderDetails(
                                    rider.id
                                )
                            )
                        }
                    )
                }
                composable<NavigationRoutes.TeamsList>(
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = NavigationRoutes.TeamsList.deepLinkRoute()
                        }
                    ),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) {
                    TeamsListRoute(
                        onTeamClick = { team ->
                            navController.navigate(
                                NavigationRoutes.TeamDetails(team.id)
                            )
                        }
                    )
                }
                composable<NavigationRoutes.RaceDetails>(
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = NavigationRoutes.RaceDetails.deepLinkRoute()
                        }
                    ),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) { backStackEntry ->
                    val raceDetails: NavigationRoutes.RaceDetails = backStackEntry.toRoute()
                    RaceDetailsRoute(
                        raceId = raceDetails.raceId,
                        stageId = raceDetails.stageId,
                        onBackPressed = { navController.navigateUp() },
                        onRiderSelected = {},
                        onTeamSelected = {},
                        onParticipationsClicked = {},
                    )
                }
                composable<NavigationRoutes.RiderDetails>(
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = NavigationRoutes.RiderDetails.deepLinkRoute()
                        }
                    ),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) { backStackEntry ->
                    val riderDetails: NavigationRoutes.RiderDetails = backStackEntry.toRoute()
                    RiderDetailsRoute(
                        riderId = riderDetails.riderId,
                        animatedVisibilityScope = this@composable,
                        onBackPressed = { navController.navigateUp() },
                        onRaceSelected = { race ->
                            navController.navigate(
                                NavigationRoutes.RaceDetails(
                                    race.id
                                )
                            )
                        },
                        onTeamSelected = { team ->
                            navController.navigate(
                                NavigationRoutes.TeamDetails(
                                    team.id
                                )
                            )
                        },
                        onStageSelected = { race, stage ->
                            navController.navigate(
                                NavigationRoutes.RaceDetails(
                                    race.id, stage.id,
                                )
                            )
                        },
                    )
                }
                composable<NavigationRoutes.TeamDetails>(
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = NavigationRoutes.TeamDetails.deepLinkRoute()
                        }
                    ),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) { backStackEntry ->
                    val teamDetails: NavigationRoutes.TeamDetails = backStackEntry.toRoute()
                    TeamDetailsRoute(
                        teamId = teamDetails.teamId,
                        onBackPressed = { navController.navigateUp() },
                        onRiderSelected = { rider ->
                            navController.navigate(
                                NavigationRoutes.RiderDetails(
                                    rider.id
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}