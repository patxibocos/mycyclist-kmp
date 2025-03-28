package io.github.patxibocos.mycyclist.ui.team.list

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TeamListScreen(
    uiState: TeamListViewModel.UiState,
    onTeamClick: (Team) -> Unit,
    onRefresh: () -> Unit,
    worldTeamsLazyGridState: LazyGridState = rememberLazyGridState(),
    proTeamsLazyGridState: LazyGridState = rememberLazyGridState(),
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
                                worldTeamsLazyGridState.scrollToItem(0)
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
                                proTeamsLazyGridState.scrollToItem(0)
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
                        lazyListState = worldTeamsLazyGridState,
                    )
                } else {
                    TeamList(
                        teams = uiState.proTeams,
                        onTeamSelected = onTeamClick,
                        lazyListState = proTeamsLazyGridState,
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
    lazyListState: LazyGridState,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        state = lazyListState,
    ) {
        items(teams, key = Team::id) { team ->
            TeamCard(team, onTeamSelected)
        }
    }
}

@Composable
private fun TeamCard(
    team: Team,
    onTeamSelected: (Team) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onTeamSelected(team) },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Text(
                text = team.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center,
            ) {
                AsyncImage(
                    model = team.jersey,
                    contentDescription = null,
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                        .size(75.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(team.abbreviation.orEmpty())
                    Text(
                        text = EmojiUtil.getCountryEmoji(team.country),
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
            }
            Text(

                text = "\uD83D\uDEB4 ${team.bike}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
