package compose.project.demo.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import compose.project.demo.domain.Race
import compose.project.demo.domain.Rider
import compose.project.demo.domain.Stage
import compose.project.demo.domain.Team
import compose.project.demo.ui.race_details.RaceDetailsRoute
import compose.project.demo.ui.races_list.RacesListRoute
import compose.project.demo.ui.rider_details.RiderDetailsRoute
import compose.project.demo.ui.riders_list.RidersListRoute
import compose.project.demo.ui.team_details.TeamDetailsRoute
import compose.project.demo.ui.teams_list.TeamsListRoute

fun NavGraphBuilder.racesListComposableRoute(onRaceClick: (Race) -> Unit) {
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
        RacesListRoute(onRaceClick = onRaceClick)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.ridersListComposableRoute(
    sharedTransitionScope: SharedTransitionScope,
    onRiderClick: (Rider) -> Unit
) {
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
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable,
            onRiderClick = onRiderClick
        )
    }
}

fun NavGraphBuilder.teamsListComposableRoute(onTeamClick: (Team) -> Unit) {
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
        TeamsListRoute(onTeamClick = onTeamClick)
    }
}

fun NavGraphBuilder.raceDetailsComposableRoute(onBackPressed: () -> Unit) {
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
            onBackPressed = onBackPressed,
            onRiderSelected = {},
            onTeamSelected = {},
            onParticipationsClicked = {},
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.riderDetailsComposableRoute(
    sharedTransitionScope: SharedTransitionScope,
    onRaceSelected: (Race) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
    onBackPressed: () -> Unit,
) {
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
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable,
            onBackPressed = onBackPressed,
            onRaceSelected = onRaceSelected,
            onTeamSelected = onTeamSelected,
            onStageSelected = onStageSelected,
        )
    }
}

fun NavGraphBuilder.teamDetailsComposableRoute(
    onRiderSelected: (Rider) -> Unit,
    onBackPressed: () -> Unit,
) {
    composable<NavigationRoutes.TeamDetails>(
        deepLinks = listOf(navDeepLink {
            uriPattern = NavigationRoutes.TeamDetails.deepLinkRoute()
        }),
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) { backStackEntry ->
        val teamDetails: NavigationRoutes.TeamDetails = backStackEntry.toRoute()
        TeamDetailsRoute(
            teamId = teamDetails.teamId,
            onBackPressed = onBackPressed,
            onRiderSelected = onRiderSelected,
        )
    }
}