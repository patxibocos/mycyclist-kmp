package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.domain.Team
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

internal fun NavGraphBuilder.raceListComposableRoute(
    onRaceClick: (Race) -> Unit,
    onRaceStageClick: (Race, Stage) -> Unit,
) {
    composable<NavigationRoutes.RaceList>(
        deepLinks = listOf(
            navDeepLink<NavigationRoutes.RaceList>(basePath = NavigationRoutes.RaceList.deepLinkRoute())
        ),
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        }
    ) {
        val viewModel = viewModel { RaceListViewModel() }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return@composable
        RaceListScreen(
            uiState = uiState,
            onRaceClick = onRaceClick,
            onRaceStageClick = onRaceStageClick,
            onRefresh = viewModel::refresh,
        )
    }
}

internal fun NavGraphBuilder.riderListComposableRoute(
    onRiderClick: (Rider) -> Unit
) {
    composable<NavigationRoutes.RiderList>(
        deepLinks = listOf(
            navDeepLink<NavigationRoutes.RiderList>(basePath = NavigationRoutes.RiderList.deepLinkRoute())
        ),
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        }
    ) {
        val viewModel = viewModel { RiderListViewModel() }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return@composable
        val topBarState by viewModel.topBarState.collectAsStateWithLifecycle()
        RiderListScreen(
            animatedVisibilityScope = this@composable,
            onRiderClick = onRiderClick,
            uiState = uiState,
            topBarState = topBarState,
            onRiderSearched = viewModel::onSearched,
            onToggled = viewModel::onToggled,
            onSortingSelected = viewModel::onSorted,
            onRefresh = viewModel::refresh,
        )
    }
}

internal fun NavGraphBuilder.teamListComposableRoute(onTeamClick: (Team) -> Unit) {
    composable<NavigationRoutes.TeamList>(
        deepLinks = listOf(
            navDeepLink<NavigationRoutes.TeamList>(basePath = NavigationRoutes.TeamList.deepLinkRoute())
        ),
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        }
    ) {
        val viewModel = viewModel { TeamListViewModel() }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return@composable
        TeamListScreen(
            uiState = uiState,
            onTeamClick = onTeamClick,
            onRefresh = viewModel::refresh,
        )
    }
}

internal fun NavGraphBuilder.raceDetailsComposableRoute(
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onBackPressed: () -> Unit,
) {
    composable<NavigationRoutes.RaceDetails>(
        deepLinks = listOf(
            navDeepLink<NavigationRoutes.RaceDetails>(NavigationRoutes.RaceDetails.deepLinkRoute())
        ),
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        }
    ) { backStackEntry ->
        val raceDetails: NavigationRoutes.RaceDetails = backStackEntry.toRoute()
        val viewModel = viewModel {
            RaceDetailsViewModel(raceId = raceDetails.raceId, stageId = raceDetails.stageId)
        }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return@composable
        RaceDetailsScreen(
            uiState = uiState,
            backEnabled = true,
            onBackPressed = onBackPressed,
            onRiderSelected = onRiderSelected,
            onTeamSelected = onTeamSelected,
            onResultsModeChanged = viewModel::onResultsModeChanged,
            onClassificationTypeChanged = viewModel::onClassificationTypeChanged,
            onStageSelected = viewModel::onStageSelected,
            onParticipationsClicked = {},
        )
    }
}

internal fun NavGraphBuilder.riderDetailsComposableRoute(
    onRaceSelected: (Race) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
    onBackPressed: () -> Unit,
) {
    composable<NavigationRoutes.RiderDetails>(
        deepLinks = listOf(
            navDeepLink<NavigationRoutes.RiderDetails>(basePath = NavigationRoutes.RiderDetails.deepLinkRoute())
        ),
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        }
    ) { backStackEntry ->
        val riderDetails: NavigationRoutes.RiderDetails = backStackEntry.toRoute()
        val viewModel: RiderDetailsViewModel =
            viewModel { RiderDetailsViewModel(riderId = riderDetails.riderId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return@composable
        RiderDetailsScreen(
            uiState = uiState,
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
            navDeepLink<NavigationRoutes.TeamDetails>(basePath = NavigationRoutes.TeamDetails.deepLinkRoute())
        ),
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) { backStackEntry ->
        val teamDetails: NavigationRoutes.TeamDetails = backStackEntry.toRoute()
        val viewModel = viewModel { TeamDetailsViewModel(teamId = teamDetails.teamId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return@composable
        TeamDetailsScreen(
            uiState = uiState,
            onBackPressed = onBackPressed,
            onRiderSelected = onRiderSelected,
        )
    }
}
