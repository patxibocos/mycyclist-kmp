package io.github.patxibocos.mycyclist.ui.race.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import io.github.patxibocos.mycyclist.ui.preview.aRace
import io.github.patxibocos.mycyclist.ui.util.humanDatesDiff
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.todayIn
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

internal fun LazyListScope.seasonInProgress(
    pastRaces: List<RaceListViewModel.PastRace>,
    todayStages: List<RaceListViewModel.TodayStage>,
    futureRaces: List<Race>,
    onRaceSelected: (Race) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
) {
    todayStages(todayStages, onStageSelected, onRaceSelected)
    futureRaces(futureRaces, onRaceSelected)
    pastRaces(pastRaces, onRaceSelected)
}

private fun LazyListScope.pastRaces(
    pastRaces: List<RaceListViewModel.PastRace>,
    onRaceSelected: (Race) -> Unit
) {
    if (pastRaces.isEmpty()) {
        return
    }
    stickyHeader {
        Text(
            text = "PAST",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
        )
    }
    items(
        items = pastRaces,
        key = { it.race.id },
        itemContent = { pastRace ->
            PastRace(pastRace, onRaceSelected)
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
            text = "UPCOMING",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
        )
    }
    items(
        items = futureRaces,
        key = Race::id,
        itemContent = { race ->
            UpcomingRace(race, onRaceSelected)
        },
    )
}

@Composable
private fun UpcomingRace(
    race: Race,
    onRaceSelected: (Race) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.clickable { onRaceSelected(race) }.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = EmojiUtil.getCountryEmoji(race.country),
                    style = MaterialTheme.typography.titleLarge,
                )
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(
                        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.titleMedium.fontSize),
                        text = race.name,
                        style = MaterialTheme.typography.titleMedium.copy(color = LocalContentColor.current),
                        maxLines = 1,
                    )
                    val dateFormat = remember {
                        LocalDate.Format {
                            monthName(MonthNames.ENGLISH_ABBREVIATED)
                            char(' ')
                            day()
                        }
                    }
                    val raceDateString = if (race.isSingleDay()) {
                        dateFormat.format(race.startDate())
                    } else {
                        "${dateFormat.format(race.startDate())} — ${dateFormat.format(race.endDate())}"
                    }
                    Text(
                        text = raceDateString,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                Text(
                    text = humanDatesDiff(today, race.startDate()),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(15.dp)
                    ).padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun PastRace(
    pastRace: RaceListViewModel.PastRace,
    onRaceSelected: (Race) -> Unit,
) {
    val (race, winner) = pastRace
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.clickable { onRaceSelected(race) }.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = EmojiUtil.getCountryEmoji(race.country),
                    style = MaterialTheme.typography.titleLarge,
                )
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicText(
                        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.titleMedium.fontSize),
                        text = race.name,
                        style = MaterialTheme.typography.titleMedium.copy(color = LocalContentColor.current),
                        maxLines = 1,
                    )
                    val dateFormat = remember {
                        LocalDate.Format {
                            monthName(MonthNames.ENGLISH_ABBREVIATED)
                            char(' ')
                            day()
                        }
                    }
                    val raceDateString = if (race.isSingleDay()) {
                        dateFormat.format(race.startDate())
                    } else {
                        "${dateFormat.format(race.startDate())} — ${dateFormat.format(race.endDate())}"
                    }
                    Text(
                        text = raceDateString,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Box(
                    modifier = Modifier.size(50.dp)
                ) {
                    AsyncImage(
                        model = winner.photo,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                        modifier = Modifier
                            .shadow(5.dp, CircleShape)
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .offset(x = 2.dp, y = 2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                shape = CircleShape
                            )
                            .padding(3.dp)
                    )
                }
            }
        }
    }
}

private fun LazyListScope.todayStages(
    todayStages: List<RaceListViewModel.TodayStage>,
    onStageSelected: (Race, Stage) -> Unit,
    onRaceSelected: (Race) -> Unit
) {
    if (todayStages.isEmpty()) {
        return
    }
    stickyHeader {
        Text(
            text = "TODAY",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
        )
    }
    items(todayStages, key = { it.race.id }) { todayStage ->
        when (todayStage) {
            is RaceListViewModel.TodayStage.MultiStageRace -> TodayRaceStage(
                todayStage.race,
                todayStage.stage,
                onStageSelected,
            )

            is RaceListViewModel.TodayStage.SingleDayRace -> TodayRaceStage(
                todayStage.race,
                todayStage.stage,
                onStageSelected,
            )

            is RaceListViewModel.TodayStage.RestDay -> TodayRestDayStage(
                todayStage.race,
                onRaceSelected
            )
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
        onStageSelected = { _, _ -> },
    )
}

@Composable
private fun TodayRaceStage(
    race: Race,
    stage: Stage,
    onStageSelected: (Race, Stage) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.clickable { onStageSelected(race, stage) }.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = EmojiUtil.getCountryEmoji(race.country),
                    style = MaterialTheme.typography.titleLarge,
                )
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(
                        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.titleMedium.fontSize),
                        text = race.name,
                        style = MaterialTheme.typography.titleMedium.copy(color = LocalContentColor.current),
                        maxLines = 1,
                    )
                    val dateFormat = remember {
                        LocalDate.Format {
                            monthName(MonthNames.ENGLISH_ABBREVIATED)
                            char(' ')
                            day()
                        }
                    }
                    val raceDateString = if (race.isSingleDay()) {
                        dateFormat.format(race.startDate())
                    } else {
                        "${dateFormat.format(race.startDate())} — ${dateFormat.format(race.endDate())}"
                    }
                    Text(
                        text = raceDateString,
                        style = MaterialTheme.typography.labelLarge
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.DirectionsBike,
                    modifier = Modifier.size(20.5.dp),
                    contentDescription = null,
                )
                Text(text = "${stage.distance} km", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stage.departure.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                    )
                    Text(
                        text = stage.arrival.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
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
