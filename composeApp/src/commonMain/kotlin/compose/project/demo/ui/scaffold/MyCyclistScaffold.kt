package compose.project.demo.ui.scaffold

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import compose.project.demo.ui.navigation.NavigationRoutes
import compose.project.demo.ui.navigation.RaceListComposableRoute
import compose.project.demo.ui.navigation.RiderListComposableRoute
import compose.project.demo.ui.navigation.TeamListComposableRoute
import compose.project.demo.ui.navigation.raceDetailsComposableRoute
import compose.project.demo.ui.navigation.riderDetailsComposableRoute
import compose.project.demo.ui.navigation.teamDetailsComposableRoute

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun MyCyclistScaffold() {
    val navController by rememberUpdatedState(rememberNavController())

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) {
        SharedTransitionLayout {
            Navigation(navController, it)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.Navigation(
    navController: NavHostController,
    values: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.RaceList,
        modifier = Modifier.padding(values)
    ) {
        RaceListComposableRoute(onRaceClick = { race ->
            navController.navigate(
                NavigationRoutes.RaceDetails(race.id, null)
            )
        })
        RiderListComposableRoute(
            sharedTransitionScope = this@Navigation,
            onRiderClick = { rider ->
                navController.navigate(
                    NavigationRoutes.RiderDetails(
                        rider.id
                    )
                )
            }
        )
        TeamListComposableRoute(onTeamClick = { team ->
            navController.navigate(
                NavigationRoutes.TeamDetails(team.id)
            )
        })
        raceDetailsComposableRoute(onBackPressed = { navController.navigateUp() })
        riderDetailsComposableRoute(
            sharedTransitionScope = this@Navigation,
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
