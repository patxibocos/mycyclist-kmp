package io.github.patxibocos.mycyclist.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.patxibocos.mycyclist.ui.navigation.NavigationRoutes
import io.github.patxibocos.mycyclist.ui.navigation.TopLevelRoute

@Composable
internal fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val routes = remember {
        listOf(
            TopLevelRoute(
                route = NavigationRoutes.RaceList,
                unselectedIcon = Icons.Outlined.Flag,
                selectedIcon = Icons.Filled.Flag,
                title = "Races"
            ),
            TopLevelRoute(
                route = NavigationRoutes.RiderList,
                unselectedIcon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person,
                title = "Riders"
            ),
            TopLevelRoute(
                route = NavigationRoutes.TeamList,
                unselectedIcon = Icons.Outlined.Group,
                selectedIcon = Icons.Filled.Group,
                title = "Teams"
            ),
        )
    }
    NavigationBar {
        routes.forEach { topLevelRoute ->
            val isRouteSelected = currentDestination?.hasRoute(topLevelRoute.route::class) == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isRouteSelected) topLevelRoute.selectedIcon else topLevelRoute.unselectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(topLevelRoute.title) },
                selected = isRouteSelected,
                onClick = {
                    if (isRouteSelected) {
                        return@NavigationBarItem
                    }
                    navController.navigate(topLevelRoute.route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestDisplayName)
                    }
                },
            )
        }
    }
}
