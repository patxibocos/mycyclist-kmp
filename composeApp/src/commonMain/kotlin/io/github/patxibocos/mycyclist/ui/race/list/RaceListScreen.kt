package io.github.patxibocos.mycyclist.ui.race.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames

@Composable
internal fun RaceListScreen(
    uiState: RaceListViewModel.UiState,
    onRaceClick: (Race) -> Unit,
    onRaceStageClick: (Race, Stage) -> Unit,
    onRefresh: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    PullToRefreshBox(isRefreshing = uiState.refreshing, onRefresh = onRefresh) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            state = listState
        ) {
            when (val content = uiState.content) {
                RaceListViewModel.Content.EmptyViewState -> {
                    item { Text(text = "Empty") }
                }

                is RaceListViewModel.Content.SeasonEndedViewState -> {
                    seasonEnded(
                        pastRaces = content.pastRaces,
                        onRaceSelected = onRaceClick,
                    )
                }

                is RaceListViewModel.Content.SeasonInProgressViewState -> {
                    seasonInProgress(
                        pastRaces = content.pastRaces,
                        todayStages = content.todayStages,
                        futureRaces = content.futureRaces,
                        onRaceSelected = onRaceClick,
                        onStageSelected = onRaceStageClick,
                    )
                }

                is RaceListViewModel.Content.SeasonNotStartedViewState -> {
                    seasonNotStarted(
                        futureRaces = content.futureRaces,
                        onRaceSelected = onRaceClick
                    )
                }
            }
        }
    }
}

private fun LazyListScope.seasonEnded(pastRaces: List<Race>, onRaceSelected: (Race) -> Unit) {
    item {
        Text(text = "Season has ended")
    }
    stickyHeader {
        Text(text = "Past races")
    }
    items(pastRaces, key = Race::id) { pastRace ->
        RaceRow(race = pastRace, onRaceSelected = onRaceSelected)
    }
}

private fun LazyListScope.seasonNotStarted(
    futureRaces: List<Race>,
    onRaceSelected: (Race) -> Unit,
) {
    item {
        Text(text = "Season has not started", modifier = Modifier)
    }
    stickyHeader {
        Text(text = "Future races")
    }
    items(futureRaces, key = Race::id) { pastRace ->
        RaceRow(race = pastRace, onRaceSelected = onRaceSelected)
    }
}

@Composable
internal fun RaceRow(
    race: Race,
    onRaceSelected: (Race) -> Unit,
) {
    val raceStartDate = race.startDate()
    val month = LocalDate.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
    }.format(raceStartDate)
    val day = LocalDate.Format {
        day()
    }.format(raceStartDate)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onRaceSelected(race) },
    ) {
        Text(
            text = "${EmojiUtil.getCountryEmoji(race.country)} ${race.name}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Row {
            Card(
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(end = 10.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 5.dp)) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Text(
                        text = month,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
