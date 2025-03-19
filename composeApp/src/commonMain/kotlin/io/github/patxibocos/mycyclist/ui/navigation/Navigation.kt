package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import mycyclist.composeapp.generated.resources.Res
import mycyclist.composeapp.generated.resources.navigation_item_races
import mycyclist.composeapp.generated.resources.navigation_item_riders
import mycyclist.composeapp.generated.resources.navigation_item_teams
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NavigationSuite(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val routes = remember {
        listOf(
            TopLevelRoute(
                route = NavigationRoutes.Races(),
                unselectedIcon = Icons.Outlined.Flag,
                selectedIcon = Icons.Filled.Flag,
                titleResId = Res.string.navigation_item_races,
            ),
            TopLevelRoute(
                route = NavigationRoutes.Riders(),
                unselectedIcon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person,
                titleResId = Res.string.navigation_item_riders,
            ),
            TopLevelRoute(
                route = NavigationRoutes.Teams(),
                unselectedIcon = Icons.Outlined.Group,
                selectedIcon = Icons.Filled.Group,
                titleResId = Res.string.navigation_item_teams,
            ),
        ).toImmutableList()
    }

    NavigationScaffold(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        routes = routes,
        currentDestination = currentDestination,
    )
}

@Composable
private fun NavigationScaffold(
    navController: NavHostController,
    routes: ImmutableList<TopLevelRoute>,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    val tabReselected =
        remember { MutableSharedFlow<NavigationRoutes>() }

    val coroutineScope = rememberCoroutineScope()
    NavigationSuiteScaffold(modifier = modifier.fillMaxSize(), navigationSuiteItems = {
        routes(routes, currentDestination, navController, coroutineScope, tabReselected)
    }) {
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.Races(),
        ) {
            racesRoute(tabReselected, coroutineScope, navController)
            ridersRoute(tabReselected, coroutineScope, navController)
            teamsRoute(tabReselected, coroutineScope, navController)
        }
    }
}

private fun NavigationSuiteScope.routes(
    routes: List<TopLevelRoute>,
    currentDestination: NavDestination?,
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    tabReselected: MutableSharedFlow<NavigationRoutes>
) {
    routes.forEach { route ->
        val isRouteSelected = currentDestination?.hasRoute(route.route::class) == true
        item(
            selected = isRouteSelected,
            onClick = {
                if (!isRouteSelected) {
                    navController.navigate(route.route) {
                        launchSingleTop = true
                    }
                } else {
                    coroutineScope.launch {
                        tabReselected.emit(route.route)
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = if (isRouteSelected) route.selectedIcon else route.unselectedIcon,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = stringResource(route.titleResId))
            }
        )
    }
}
