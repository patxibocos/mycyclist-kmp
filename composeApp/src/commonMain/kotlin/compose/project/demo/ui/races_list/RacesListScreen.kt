package compose.project.demo.ui.races_list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.project.demo.domain.Race
import compose.project.demo.domain.Stage
import compose.project.demo.domain.startDate
import compose.project.demo.getCountryEmoji
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

@Composable
fun RacesListScreen(
    onRaceClick: (Race) -> Unit,
    viewModel: RacesListViewModel = viewModel { RacesListViewModel() }
) {
    val viewState by viewModel.uiState.collectAsState()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val state = viewState
        if (state == null) {
            return@LazyColumn
        }
        when (state) {
            RacesListViewModel.UiState.EmptyViewState -> {
                item { Text(text = "Empty") }
            }

            is RacesListViewModel.UiState.SeasonEndedViewState -> {
                seasonEnded(state.pastRaces, {})
            }

            is RacesListViewModel.UiState.SeasonInProgressViewState -> {
                seasonInProgress(
                    state.pastRaces,
                    state.todayStages,
                    state.futureRaces,
                    {},
                    { _, _ -> },
                )
            }

            is RacesListViewModel.UiState.SeasonNotStartedViewState -> {
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
    items(pastRaces) { pastRace ->
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
    items(futureRaces) { pastRace ->
        RaceRow(race = pastRace, onRaceSelected = onRaceSelected)
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.seasonInProgress(
    pastRaces: List<Race>,
    todayStages: List<RacesListViewModel.TodayStage>,
    futureRaces: List<Race>,
    onRaceSelected: (Race) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
) {
    item {
        Text(
            text = "Today",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp)
                .fillMaxWidth(),
        )
    }
    if (todayStages.isEmpty()) {
        item {
            Text("No races today, see next races below")
        }
    }
    items(todayStages) { todayStage ->
        when (todayStage) {
            is RacesListViewModel.TodayStage.MultiStageRace -> TodayMultiStageRaceStage(
                todayStage.race,
                todayStage.stage,
                todayStage.stageNumber,
                todayStage.results,
                onStageSelected,
            )

            is RacesListViewModel.TodayStage.SingleDayRace -> TodaySingleDayRaceStage(
                todayStage.race,
                todayStage.stage,
                todayStage.results,
                onRaceSelected,
            )

            is RacesListViewModel.TodayStage.RestDay -> TodayRestDayStage(
                todayStage.race,
                onRaceSelected
            )
        }
    }
    if (futureRaces.isNotEmpty()) {
        stickyHeader {
            Text(
                text = "Future races",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp)
                    .fillMaxWidth(),
            )
        }
        items(
            items = futureRaces,
            key = Race::id,
            itemContent = { race ->
                RaceRow(race, onRaceSelected)
            },
        )
    }
    if (pastRaces.isNotEmpty()) {
        stickyHeader {
            Text(
                text = "Past races",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp)
                    .fillMaxWidth(),
            )
        }
        items(
            items = pastRaces,
            key = Race::id,
            itemContent = { race ->
                RaceRow(race, onRaceSelected)
            },
        )
    }
}

@Composable
private fun TodayMultiStageRaceStage(
    race: Race,
    stage: Stage,
    stageNumber: Int,
    results: RacesListViewModel.TodayResults,
    onStageSelected: (Race, Stage) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onStageSelected(race, stage)
            },
    ) {
        Text("${race.name} - Stage $stageNumber")
        Text("🏳 ${stage.departure} - ${stage.arrival} 🏁")
        Text(formatTime(stage.startDateTime))
        Results(results)
    }
}

@Composable
private fun TodaySingleDayRaceStage(
    race: Race,
    stage: Stage,
    results: RacesListViewModel.TodayResults,
    onRaceSelected: (Race) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRaceSelected(race) },
    ) {
        Text(text = race.name)
        Text("🏳 ${stage.departure} - ${stage.arrival} 🏁")
        Text(formatTime(stage.startDateTime))
        Results(results)
    }
}

private fun formatTime(instant: Instant): String {
    return LocalDateTime.Format {
        hour()
        char(':')
        minute()
    }.format(instant.toLocalDateTime(TimeZone.currentSystemDefault()))
}

@Composable
private fun Results(results: RacesListViewModel.TodayResults) {
    when (results) {
        is RacesListViewModel.TodayResults.Riders -> {
            results.riders.forEachIndexed { index, rider ->
                Text("${index + 1}. ${rider.rider.fullName()}")
            }
        }

        is RacesListViewModel.TodayResults.Teams -> {
            results.teams.forEachIndexed { index, team ->
                Text("${index + 1}. ${team.team.name}")
            }
        }
    }
}

@Composable
private fun TodayRestDayStage(race: Race, onRaceSelected: (Race) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRaceSelected(race) },
    ) {
        Text("Rest day - ${race.name}")
    }
}

@Composable
private fun RaceRow(
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
            text = "${getCountryEmoji(race.country)} ${race.name}",
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