package io.github.patxibocos.mycyclist.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal sealed interface NavigationRoutes {

    val path: String

    fun deepLinkRoute(): String = "mycyclist://$path"

    @Serializable
    data object RaceList : NavigationRoutes {
        override val path = "races"
    }

    @Serializable
    data object RiderList : NavigationRoutes {
        override val path = "riders"
    }

    @Serializable
    data object TeamList : NavigationRoutes {
        override val path = "teams"
    }

    @Serializable
    data class RaceDetails(val raceId: String, @SerialName("stage") val stageId: String? = null) {
        companion object : NavigationRoutes {
            override val path = "races"
        }
    }

    @Serializable
    data class RiderDetails(val riderId: String) {
        companion object : NavigationRoutes {
            override val path = "riders"
        }
    }

    @Serializable
    data class TeamDetails(@SerialName("team") val teamId: String) {
        companion object : NavigationRoutes {
            override val path = "teams"
        }
    }
}
