package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

internal data class TopLevelRoute<T>(
    val route: T,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val title: String,
)
