package io.github.patxibocos.mycyclist.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
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
            TopLevelRoute(NavigationRoutes.RaceList, Icons.Outlined.Email, "Races"),
            TopLevelRoute(NavigationRoutes.RiderList, Icons.Outlined.Face, "Riders"),
            TopLevelRoute(NavigationRoutes.TeamList, Icons.Outlined.Person, "Teams"),
        )
    }
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
