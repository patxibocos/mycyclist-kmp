package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import mycyclist.composeapp.generated.resources.Res
import mycyclist.composeapp.generated.resources.navigation_item_races
import mycyclist.composeapp.generated.resources.navigation_item_riders
import mycyclist.composeapp.generated.resources.navigation_item_teams
import org.jetbrains.compose.resources.StringResource

@Serializable
internal sealed interface Route

@Serializable
internal sealed interface TopLevelRoute : Route {
    val unselectedIcon: ImageVector
    val selectedIcon: ImageVector
    val titleResId: StringResource
}

@Serializable
internal data object RaceList : TopLevelRoute {
    override val unselectedIcon = Icons.Outlined.Flag
    override val selectedIcon = Icons.Filled.Flag
    override val titleResId = Res.string.navigation_item_races
}

@Serializable
internal data object RiderList : TopLevelRoute {
    override val unselectedIcon = Icons.Outlined.Person
    override val selectedIcon = Icons.Filled.Person
    override val titleResId = Res.string.navigation_item_riders
}

@Serializable
internal data object TeamList : TopLevelRoute {
    override val unselectedIcon = Icons.Outlined.Group
    override val selectedIcon = Icons.Filled.Group
    override val titleResId = Res.string.navigation_item_teams
}

@Serializable
internal data class RaceDetails(val raceId: String, val stageId: String? = null) : Route

@Serializable
internal data class RiderDetails(val riderId: String) : Route

@Serializable
internal data class TeamDetails(val teamId: String) : Route

internal val topLevelRoutes: List<TopLevelRoute> = listOf(RaceList, RiderList, TeamList)
