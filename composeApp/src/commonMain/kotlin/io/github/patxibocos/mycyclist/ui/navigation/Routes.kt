package io.github.patxibocos.mycyclist.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal sealed interface DeepLinkRoute {
    val path: String

    fun deepLink(): String = "mycyclist://$path"
}

internal sealed interface NavigationRoutes {

    @Serializable
    data class Races(
        @SerialName("race") val raceId: String? = null,
        @SerialName("stage") val stageId: String? = null,
    ) : NavigationRoutes {
        companion object : DeepLinkRoute {
            override val path: String = "races"
        }
    }

    @Serializable
    data class Riders(@SerialName("rider") val riderId: String? = null) : NavigationRoutes {
        companion object : DeepLinkRoute {
            override val path: String = "riders"
        }
    }

    @Serializable
    data class Teams(@SerialName("team") val teamId: String? = null) : NavigationRoutes {
        companion object : DeepLinkRoute {
            override val path: String = "teams"
        }
    }
}
