package compose.project.demo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import compose.project.demo.CodePointUtil.Companion.buildStringFromCodePoints
import compose.project.demo.domain.Race
import compose.project.demo.domain.startDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context).crossfade(true).logger(DebugLogger()).build()

@Composable
@Preview
fun App() {
    MaterialTheme {
        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }

        val navController by rememberUpdatedState(rememberNavController())
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        data class TopLevelRoute<T : Any>(val route: T, val icon: ImageVector, val title: String)

        val routes = listOf(
            TopLevelRoute(NavigationRoutes.RacesList, Icons.Outlined.Email, "Races"),
            TopLevelRoute(NavigationRoutes.RidersList, Icons.Outlined.Face, "Riders"),
            TopLevelRoute(NavigationRoutes.TeamsList, Icons.Outlined.Person, "Teams"),
        )

        Scaffold(
            bottomBar = {
                NavigationBar {
                    routes.forEach { topLevelRoute ->
                        NavigationBarItem(
                            icon = { Icon(topLevelRoute.icon, null) },
                            label = { Text(topLevelRoute.title) },
                            selected = currentDestination?.hasRoute(topLevelRoute.route::class) == true,
                            onClick = {
                                navController.navigate(topLevelRoute.route)
                            },
                        )
                    }
                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = NavigationRoutes.RacesList,
                modifier = Modifier.padding(it)
            ) {
                composable<NavigationRoutes.RacesList>(deepLinks = listOf(
                    navDeepLink {
                        uriPattern = NavigationRoutes.RacesList.deepLinkRoute()
                    }
                )) {
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
                composable<NavigationRoutes.RidersList>(deepLinks = listOf(
                    navDeepLink {
                        uriPattern = NavigationRoutes.RidersList.deepLinkRoute()
                    }
                )) {
                    Text("Riders")
                }
                composable<NavigationRoutes.TeamsList>(deepLinks = listOf(
                    navDeepLink {
                        uriPattern = NavigationRoutes.TeamsList.deepLinkRoute()
                    }
                )) {
                    Text("Teams")
                }
                composable<NavigationRoutes.RaceDetails>(deepLinks = listOf(
                    navDeepLink {
                        uriPattern = NavigationRoutes.RaceDetails.deepLinkRoute()
                    }
                )) { backStackEntry ->
                    val raceDetails: NavigationRoutes.RaceDetails = backStackEntry.toRoute()
                    RaceDetailsScreen(raceDetails.raceId, raceDetails.stageId)
                }
            }
        }
    }
}

sealed interface NavigationRoutes {

    val deepLink: String

    fun deepLinkRoute() = "mycyclist://$deepLink"

    @Serializable
    data object RacesList : NavigationRoutes {
        override val deepLink = "races"
    }

    @Serializable
    data object RidersList : NavigationRoutes {
        override val deepLink = "riders"
    }

    @Serializable
    data object TeamsList : NavigationRoutes {
        override val deepLink = "teams"
    }

    @Serializable
    data class RaceDetails(val raceId: String, val stageId: String? = null) {
        companion object : NavigationRoutes {
            override val deepLink = "races/{raceId}?stage={stageId}"
        }
    }

    @Serializable
    data class RiderDetails(val riderId: String) {
        companion object : NavigationRoutes {
            override val deepLink = "riders/{riderId}"
        }
    }

    @Serializable
    data class TeamDetails(val teamId: String) {
        companion object : NavigationRoutes {
            override val deepLink = "teams/{teamId}"
        }
    }

}

@Composable
fun RacesListScreen(
    onRaceClick: (Race) -> Unit,
    viewModel: RacesListViewModel = viewModel { RacesListViewModel() }
) {
    val viewState by viewModel.uiState.collectAsState()
    val racesViewState = viewState
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (racesViewState) {
            RacesListViewModel.UiState.EmptyViewState -> {}
            is RacesListViewModel.UiState.SeasonEndedViewState -> {
                seasonEnded(racesViewState.pastRaces, {})
            }

            is RacesListViewModel.UiState.SeasonInProgressViewState -> {
//                seasonInProgress(
//                    racesViewState.pastRaces,
//                    racesViewState.todayStages,
//                    racesViewState.futureRaces,
//                    onRaceSelected,
//                    onStageSelected,
//                )
            }

            is RacesListViewModel.UiState.SeasonNotStartedViewState -> {
                seasonNotStarted(racesViewState.futureRaces, onRaceClick)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.seasonEnded(pastRaces: List<Race>, onRaceSelected: (Race) -> Unit) {
    item {
        Text(text = "Season has ended")
    }
    stickyHeader {
        Text(text = "Past races")
    }
    items(pastRaces) { pastRace ->
        Text(pastRace.name)
    }
}

@Composable
fun RaceDetailsScreen(raceId: String, stageId: String?) {
    Column {
        Text(raceId)
        Text(stageId.toString())
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.seasonNotStarted(
    futureRaces: List<Race>,
    onRaceSelected: (Race) -> Unit,
) {
    item {
        Text(text = "Season has not started", modifier = Modifier)
    }
    stickyHeader {
        Text(text = "Future races")
    }
    items(futureRaces) { pastRace ->
        RaceRow(race = pastRace, onRaceSelected = onRaceSelected)
    }
}

@Composable
private fun RaceRow(
    race: Race,
    onRaceSelected: (Race) -> Unit,
) {
    val raceStartDate = race.startDate()
    val month = LocalDate.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
    }.format(raceStartDate)
    val day = LocalDate.Format {
        dayOfMonth(padding = Padding.ZERO)
    }.format(raceStartDate)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onRaceSelected(race) },
    ) {
        Text(
            text = "${getCountryEmoji(race.country)} ${race.name}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Row {
            Card(
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(end = 10.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 5.dp)) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Text(
                        text = month,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}

fun getCountryEmoji(countryCode: String): String {
    val codePoints = countryCode.uppercase()
        .map { char -> 127397 + CodePointUtil.codePointAt(char.toString(), 0) }
        .toIntArray()
    return buildStringFromCodePoints(codePoints)
}
