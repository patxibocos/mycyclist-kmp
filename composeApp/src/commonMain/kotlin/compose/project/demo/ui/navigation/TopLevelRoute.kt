package compose.project.demo.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRoute<T>(val route: T, val icon: ImageVector, val title: String)