package compose.project.demo.ui.scaffold

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import compose.project.demo.ui.navigation.NavigationRoutes
import compose.project.demo.ui.navigation.raceDetailsComposableRoute
import compose.project.demo.ui.navigation.racesListComposableRoute
import compose.project.demo.ui.navigation.riderDetailsComposableRoute
import compose.project.demo.ui.navigation.ridersListComposableRoute
import compose.project.demo.ui.navigation.teamDetailsComposableRoute
import compose.project.demo.ui.navigation.teamsListComposableRoute

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
                racesListComposableRoute(onRaceClick = { race ->
                    navController.navigate(
                        NavigationRoutes.RaceDetails(race.id, null)
                    )
                })
                ridersListComposableRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onRiderClick = { rider ->
                        navController.navigate(
                            NavigationRoutes.RiderDetails(
                                rider.id
                            )
                        )
                    })
                teamsListComposableRoute(onTeamClick = { team ->
                    navController.navigate(
                        NavigationRoutes.TeamDetails(team.id)
                    )
                })
                raceDetailsComposableRoute(onBackPressed = { navController.navigateUp() })
                riderDetailsComposableRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
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
                                race.id,
                                stage.id,
                            )
                        )
                    },
                    onBackPressed = { navController.navigateUp() },
                )
                teamDetailsComposableRoute(
                    onRiderSelected = { rider ->
                        navController.navigate(
                            NavigationRoutes.RiderDetails(
                                rider.id
                            )
                        )
                    },
                    onBackPressed = { navController.navigateUp() },
                )
            }
        }
    }
}