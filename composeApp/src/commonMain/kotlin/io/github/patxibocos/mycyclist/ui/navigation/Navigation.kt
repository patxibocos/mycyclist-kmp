package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import io.github.patxibocos.mycyclist.ListDetailScene
import io.github.patxibocos.mycyclist.rememberListDetailSceneStrategy
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NavigationSuite() {
    val backStack: MutableList<Route> =
        rememberSerializable(serializer = SnapshotStateListSerializer()) {
            mutableStateListOf(RaceList)
        }

    val listDetailStrategy = rememberListDetailSceneStrategy<Route>()

    Scaffold(
        bottomBar = { BottomNavigation(topLevelRoutes, backStack) },
    ) { paddingValues ->
        NavDisplay(
            modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategy = listDetailStrategy,
            entryProvider = entryProvider {
                entry<RaceList>(metadata = ListDetailScene.listPane()) {
                    RaceList(onRaceClick = { race ->
                        backStack.add(RaceDetails(race.id))
                    }, onRaceStageClick = { race, stage ->
                        backStack.add(RaceDetails(race.id, stage.id))
                    })
                }
                entry<RiderList>(metadata = ListDetailScene.listPane()) {
                    RiderList(onRiderClick = { rider ->
                        backStack.add(RiderDetails(rider.id))
                    })
                }
                entry<TeamList>(metadata = ListDetailScene.listPane()) {
                    TeamList(onTeamClick = { team ->
                        backStack.add(TeamDetails(team.id))
                    })
                }
                entry<RaceDetails>(metadata = ListDetailScene.detailPane()) {
                    RaceDetails(
                        raceAndStageId = it.raceId to it.stageId,
                        onBackPressed = {
                            backStack.removeLastOrNull()
                        },
                        onRiderSelected = { rider ->
                            backStack.add(RiderDetails(rider.id))
                        },
                        onTeamSelected = { team ->
                            backStack.add(TeamDetails(team.id))
                        }
                    )
                }
                entry<RiderDetails>(metadata = ListDetailScene.detailPane()) {
                    RiderDetails(
                        riderId = it.riderId,
                        onBackPressed = {
                            backStack.removeLastOrNull()
                        },
                        onRaceSelected = { race ->
                            backStack.add(RaceDetails(race.id))
                        },
                        onTeamSelected = { team ->
                            backStack.add(TeamDetails(team.id))
                        },
                        onStageSelected = { race, stage ->
                            backStack.add(RaceDetails(race.id, stage.id))
                        }
                    )
                }
                entry<TeamDetails>(metadata = ListDetailScene.detailPane()) {
                    TeamDetails(
                        teamId = it.teamId,
                        onBackPressed = {
                            backStack.removeLastOrNull()
                        },
                        onRiderSelected = { rider ->
                            backStack.add(RiderDetails(rider.id))
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun BottomNavigation(
    topLevelRoutes: List<TopLevelRoute>,
    backStack: MutableList<Route>
) {
    val selectedType =
        remember { derivedStateOf { backStack.lastOrNull { it in topLevelRoutes } as? TopLevelRoute } }.value
    NavigationBar {
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (topLevelRoute == selectedType) {
                            topLevelRoute.selectedIcon
                        } else {
                            topLevelRoute.unselectedIcon
                        },
                        contentDescription = null
                    )
                },
                selected = topLevelRoute == selectedType,
                onClick = {
                    val routeIndex = backStack.indexOfFirst { it == topLevelRoute }
                    when {
                        routeIndex == -1 -> {
                            backStack.add(topLevelRoute)
                        }

                        selectedType == topLevelRoute -> {
                            val lastIndex = backStack.indexOfLast { it == topLevelRoute }
                            if (lastIndex < backStack.lastIndex) {
                                backStack.subList(lastIndex + 1, backStack.size).clear()
                            }
                        }

                        else -> {
                            val nextTopLevelIndex = backStack
                                .drop(routeIndex + 1)
                                .indexOfFirst { it in topLevelRoutes }

                            val endIndex = if (nextTopLevelIndex == -1) {
                                backStack.size
                            } else {
                                routeIndex + 1 + nextTopLevelIndex
                            }

                            val section = backStack.subList(routeIndex, endIndex).toList()
                            backStack.removeAll(section)
                            backStack.addAll(section)
                        }
                    }
                },
                label = {
                    Text(text = stringResource(topLevelRoute.titleResId))
                }
            )
        }
    }
}
