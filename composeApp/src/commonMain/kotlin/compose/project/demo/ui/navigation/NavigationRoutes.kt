package compose.project.demo.ui.navigation

import kotlinx.serialization.Serializable

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