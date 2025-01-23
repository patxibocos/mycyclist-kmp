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
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.domain.Team
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
            onRaceClick = { race -> navController.navigateToRaceDetails(race) },
            onRaceStageClick = { race, stage ->
                navController.navigateToRaceDetails(
                    race = race,
                    stage = stage,
                )
            }
        )
        riderListComposableRoute(
            sharedTransitionScope = this@Navigation,
            onRiderClick = { rider -> navController.navigateToRiderDetails(rider) }
        )
        teamListComposableRoute(onTeamClick = { team -> navController.navigateToTeamDetails(team) })
        raceDetailsComposableRoute(
            onBackPressed = { navController.navigateUp() },
            onRiderSelected = { rider -> navController.navigateToRiderDetails(rider) },
            onTeamSelected = { team -> navController.navigateToTeamDetails(team) },
        )
        riderDetailsComposableRoute(
            sharedTransitionScope = this@Navigation,
            onRaceSelected = { race -> navController.navigateToRaceDetails(race) },
            onTeamSelected = { team -> navController.navigateToTeamDetails(team) },
            onStageSelected = { race, stage ->
                navController.navigateToRaceDetails(
                    race = race,
                    stage = stage,
                )
            },
            onBackPressed = { navController.navigateUp() },
        )
        teamDetailsComposableRoute(
            onRiderSelected = { rider -> navController.navigateToRiderDetails(rider) },
            onBackPressed = { navController.navigateUp() },
        )
    }
}

private fun NavHostController.navigateToRaceDetails(race: Race, stage: Stage? = null) =
    navigate(NavigationRoutes.RaceDetails(raceId = race.id, stageId = stage?.id))

private fun NavHostController.navigateToRiderDetails(rider: Rider) =
    navigate(NavigationRoutes.RiderDetails(riderId = rider.id))

private fun NavHostController.navigateToTeamDetails(team: Team) =
    navigate(NavigationRoutes.TeamDetails(teamId = team.id))
