package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

internal data class TopLevelRoute(
    val route: NavigationRoutes,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val title: String,
)
