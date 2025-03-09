package io.github.patxibocos.mycyclist.ui.scaffold

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.patxibocos.mycyclist.ui.navigation.NavigationRoutes

internal data class TopLevelRoute(
    val route: NavigationRoutes,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val title: String,
)