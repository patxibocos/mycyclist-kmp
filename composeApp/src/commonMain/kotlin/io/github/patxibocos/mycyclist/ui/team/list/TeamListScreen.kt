package io.github.patxibocos.mycyclist.ui.team.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
internal fun TeamListScreen(
    uiState: TeamListViewModel.UiState,
    onTeamClick: (Team) -> Unit,
    onRefresh: () -> Unit,
    worldTeamsLazyListState: LazyListState = rememberLazyListState(),
    proTeamsLazyListState: LazyListState = rememberLazyListState(),
) {
    PullToRefreshBox(isRefreshing = uiState.refreshing, onRefresh = onRefresh) {
        val pagerState = rememberPagerState(pageCount = { 2 })
        val coroutineScope = rememberCoroutineScope()

        Column {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage == 0) {
                                worldTeamsLazyListState.scrollToItem(0)
                            } else {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    },
                    text = { Text("World Teams") },
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage == 1) {
                                proTeamsLazyListState.scrollToItem(0)
                            } else {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    },
                    text = { Text("Pro Teams") },
                )
            }
            HorizontalPager(
                state = pagerState,
            ) { page ->
                if (page == 0) {
                    TeamList(
                        teams = uiState.worldTeams,
                        onTeamSelected = onTeamClick,
                        lazyListState = worldTeamsLazyListState,
                    )
                } else {
                    TeamList(
                        teams = uiState.proTeams,
                        onTeamSelected = onTeamClick,
                        lazyListState = proTeamsLazyListState,
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamList(
    teams: ImmutableList<Team>,
    onTeamSelected: (Team) -> Unit,
    lazyListState: LazyListState,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        state = lazyListState,
    ) {
        item { }
        items(teams, key = Team::id) { team ->
            TeamRow(team, onTeamSelected)
        }
    }
}

@Composable
private fun TeamRow(
    team: Team,
    onTeamSelected: (Team) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.clickable { onTeamSelected(team) }.padding(10.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AsyncImage(
                model = team.jersey,
                modifier = Modifier
                    .shadow(5.dp, MaterialTheme.shapes.medium)
                    .size(75.dp)
                    .clip(MaterialTheme.shapes.medium),
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                BasicText(
                    text = team.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.titleMedium.fontSize),
                )
                team.abbreviation?.let {
                    Text(team.abbreviation, style = MaterialTheme.typography.labelLarge)
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Icon(Icons.Default.PedalBike, null)
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(team.bike, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Text(text = EmojiUtil.getCountryEmoji(team.country))
        }
    }
}
