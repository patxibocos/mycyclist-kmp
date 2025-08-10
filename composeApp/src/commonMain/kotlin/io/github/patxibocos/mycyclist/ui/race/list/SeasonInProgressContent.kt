package io.github.patxibocos.mycyclist.ui.race.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import io.github.patxibocos.mycyclist.ui.preview.aRace
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

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
                is RaceListViewModel.TodayStage.MultiStageRace -> TodayRaceStage(
                    todayStage.race,
                    todayStage.stage,
                    todayStage.results,
                    onStageSelected,
                )

                is RaceListViewModel.TodayStage.SingleDayRace -> TodayRaceStage(
                    todayStage.race,
                    todayStage.stage,
                    todayStage.results,
                    onStageSelected,
                )

                is RaceListViewModel.TodayStage.RestDay -> TodayRestDayStage(
                    todayStage.race,
                    onRaceSelected
                )
            }
        }
    }
}

@Preview
@Composable
fun TodayMultiStageRaceStagePreview() {
    val race = aRace()
    val stage = race.stages.first()
    TodayRaceStage(
        race = race,
        stage = stage,
        results = RaceListViewModel.TodayResults.Riders(persistentListOf()),
        onStageSelected = { _, _ -> },
    )
}

@Composable
private fun TodayRaceStage(
    race: Race,
    stage: Stage,
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
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = EmojiUtil.getCountryEmoji(race.country),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = race.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val dateFormat = remember {
                        LocalDate.Format {
                            monthName(MonthNames.ENGLISH_ABBREVIATED)
                            char(' ')
                            dayOfMonth()
                        }
                    }
                    val raceDateString = if (race.isSingleDay()) {
                        dateFormat.format(race.startDate())
                    } else {
                        "${dateFormat.format(race.startDate())} — ${dateFormat.format(race.endDate())}"
                    }
                    Text(
                        text = raceDateString,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(15.dp)
                    ).padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "${stage.distance} km", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "${stage.departure} — ${stage.arrival}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            stage.profileType.let { profileType ->
            }
        }
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
