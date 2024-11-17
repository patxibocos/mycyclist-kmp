package compose.project.demo.ui.scaffold

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import compose.project.demo.ui.race_details.RaceDetailsScreen
import compose.project.demo.ui.races_list.RacesListScreen

@Composable
fun MyCyclistScaffold() {
    val navController by rememberUpdatedState(rememberNavController())

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) {
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
                RacesListScreen(
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
                Text("Riders")
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
                Text("Teams")
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
                RaceDetailsScreen(
                    raceId = raceDetails.raceId,
                    stageId = raceDetails.stageId,
                    onBackPressed = { navController.navigateUp() },
                )
            }
        }
    }
}