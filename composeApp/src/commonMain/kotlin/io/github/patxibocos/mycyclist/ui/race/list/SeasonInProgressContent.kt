package io.github.patxibocos.mycyclist.ui.race.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Stage
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

internal fun LazyListScope.seasonInProgress(
    pastRaces: List<Race>,
    todayStages: List<RaceListViewModel.TodayStage>,
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
    todayStages(todayStages, onStageSelected, onRaceSelected)
    futureRaces(futureRaces, onRaceSelected)
    pastRaces(pastRaces, onRaceSelected)
}

private fun LazyListScope.pastRaces(
    pastRaces: List<Race>,
    onRaceSelected: (Race) -> Unit
) {
    if (pastRaces.isEmpty()) {
        return
    }
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

private fun LazyListScope.futureRaces(
    futureRaces: List<Race>,
    onRaceSelected: (Race) -> Unit
) {
    if (futureRaces.isEmpty()) {
        return
    }
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

private fun LazyListScope.todayStages(
    todayStages: List<RaceListViewModel.TodayStage>,
    onStageSelected: (Race, Stage) -> Unit,
    onRaceSelected: (Race) -> Unit
) {
    if (todayStages.isEmpty()) {
        item {
            Text("No races today, see next races below")
        }
    } else {
        items(todayStages, key = { it.race.id }) { todayStage ->
            when (todayStage) {
                is RaceListViewModel.TodayStage.MultiStageRace -> TodayMultiStageRaceStage(
                    todayStage.race,
                    todayStage.stage,
                    todayStage.stageNumber,
                    todayStage.results,
                    onStageSelected,
                )

                is RaceListViewModel.TodayStage.SingleDayRace -> TodaySingleDayRaceStage(
                    todayStage.race,
                    todayStage.stage,
                    todayStage.results,
                    onRaceSelected,
                )

                is RaceListViewModel.TodayStage.RestDay -> TodayRestDayStage(
                    todayStage.race,
                    onRaceSelected
                )
            }
        }
    }
}

@Composable
private fun TodayMultiStageRaceStage(
    race: Race,
    stage: Stage,
    stageNumber: Int,
    results: RaceListViewModel.TodayResults,
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
    results: RaceListViewModel.TodayResults,
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
private fun Results(results: RaceListViewModel.TodayResults) {
    when (results) {
        is RaceListViewModel.TodayResults.Riders -> {
            results.riders.forEachIndexed { index, rider ->
                Text("${index + 1}. ${rider.rider.fullName()}")
            }
        }

        is RaceListViewModel.TodayResults.Teams -> {
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
