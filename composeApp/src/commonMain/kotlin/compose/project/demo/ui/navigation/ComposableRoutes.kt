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
import compose.project.demo.ui.race.details.RaceDetailsRoute
import compose.project.demo.ui.race.list.RaceListRoute
import compose.project.demo.ui.rider.details.RiderDetailsRoute
import compose.project.demo.ui.rider.list.RiderListRoute
import compose.project.demo.ui.team.details.TeamDetailsRoute
import compose.project.demo.ui.team.list.TeamListRoute

internal fun NavGraphBuilder.RaceListComposableRoute(onRaceClick: (Race) -> Unit) {
    composable<NavigationRoutes.RaceList>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = NavigationRoutes.RaceList.deepLinkRoute()
            }
        ),
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        }
    ) {
        RaceListRoute(onRaceClick = onRaceClick)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
internal fun NavGraphBuilder.RiderListComposableRoute(
    sharedTransitionScope: SharedTransitionScope,
    onRiderClick: (Rider) -> Unit
) {
    composable<NavigationRoutes.RiderList>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = NavigationRoutes.RiderList.deepLinkRoute()
            }
        ),
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        }
    ) {
        RiderListRoute(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable,
            onRiderClick = onRiderClick
        )
    }
}

internal fun NavGraphBuilder.TeamListComposableRoute(onTeamClick: (Team) -> Unit) {
    composable<NavigationRoutes.TeamList>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = NavigationRoutes.TeamList.deepLinkRoute()
            }
        ),
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        }
    ) {
        TeamListRoute(onTeamClick = onTeamClick)
    }
}

internal fun NavGraphBuilder.raceDetailsComposableRoute(onBackPressed: () -> Unit) {
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
internal fun NavGraphBuilder.riderDetailsComposableRoute(
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

internal fun NavGraphBuilder.teamDetailsComposableRoute(
    onRiderSelected: (Rider) -> Unit,
    onBackPressed: () -> Unit,
) {
    composable<NavigationRoutes.TeamDetails>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = NavigationRoutes.TeamDetails.deepLinkRoute()
            }
        ),
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
