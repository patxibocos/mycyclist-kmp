package io.github.patxibocos.mycyclist.ui.race.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding

@Composable
internal fun RaceListRoute(
    onRaceClick: (Race) -> Unit,
    viewModel: RaceListViewModel = viewModel { RaceListViewModel() }
) {
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    val state = viewState ?: return
    RaceListScreen(
        state = state,
        onRaceClick = onRaceClick,
    )
}

@Composable
private fun RaceListScreen(
    state: RaceListViewModel.UiState,
    onRaceClick: (Race) -> Unit,
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (state) {
            RaceListViewModel.UiState.EmptyViewState -> {
                item { Text(text = "Empty") }
            }

            is RaceListViewModel.UiState.SeasonEndedViewState -> {
                seasonEnded(state.pastRaces, onRaceClick)
            }

            is RaceListViewModel.UiState.SeasonInProgressViewState -> {
                seasonInProgress(
                    state.pastRaces,
                    state.todayStages,
                    state.futureRaces,
                    {},
                    { _, _ -> },
                )
            }

            is RaceListViewModel.UiState.SeasonNotStartedViewState -> {
                seasonNotStarted(state.futureRaces, onRaceClick)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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

@OptIn(ExperimentalFoundationApi::class)
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
        dayOfMonth(padding = Padding.ZERO)
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
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}
