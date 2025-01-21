package io.github.patxibocos.mycyclist.ui.scaffold

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
import io.github.patxibocos.mycyclist.ui.navigation.NavigationRoutes
import io.github.patxibocos.mycyclist.ui.navigation.raceDetailsComposableRoute
import io.github.patxibocos.mycyclist.ui.navigation.raceListComposableRoute
import io.github.patxibocos.mycyclist.ui.navigation.riderDetailsComposableRoute
import io.github.patxibocos.mycyclist.ui.navigation.riderListComposableRoute
import io.github.patxibocos.mycyclist.ui.navigation.teamDetailsComposableRoute
import io.github.patxibocos.mycyclist.ui.navigation.teamListComposableRoute

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
        raceListComposableRoute(
            onRaceClick = { race ->
                navController.navigate(
                    NavigationRoutes.RaceDetails(race.id)
                )
            },
            onRaceStageClick = { race, stage ->
                navController.navigate(
                    NavigationRoutes.RaceDetails(race.id, stage.id)
                )
            }
        )
        riderListComposableRoute(
            sharedTransitionScope = this@Navigation,
            onRiderClick = { rider -> navController.navigateToRiderDetails(rider.id) }
        )
        teamListComposableRoute(onTeamClick = { team ->
            navController.navigate(
                NavigationRoutes.TeamDetails(team.id)
            )
        })
        raceDetailsComposableRoute(onBackPressed = { navController.navigateUp() })
        riderDetailsComposableRoute(
            sharedTransitionScope = this@Navigation,
            onRaceSelected = { race -> navController.navigateToRaceDetails(raceId = race.id) },
            onTeamSelected = { team ->
                navController.navigate(
                    NavigationRoutes.TeamDetails(
                        team.id
                    )
                )
            },
            onStageSelected = { race, stage ->
                navController.navigateToRaceDetails(
                    raceId = race.id,
                    stageId = stage.id
                )
            },
            onBackPressed = { navController.navigateUp() },
        )
        teamDetailsComposableRoute(
            onRiderSelected = { rider -> navController.navigateToRiderDetails(rider.id) },
            onBackPressed = { navController.navigateUp() },
        )
    }
}

fun NavHostController.navigateToRaceDetails(raceId: String, stageId: String? = null) =
    navigate(NavigationRoutes.RaceDetails(raceId = raceId, stageId = stageId))

fun NavHostController.navigateToRiderDetails(riderId: String) =
    navigate(NavigationRoutes.RiderDetails(riderId = riderId))
