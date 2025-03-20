package io.github.patxibocos.mycyclist.ui.race.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.domain.Team
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun StageInfo(stage: Stage) {
    Text(text = stage.startDateTime.toString())
    if (stage.departure?.isNotEmpty() == true && stage.arrival?.isNotEmpty() == true) {
        Text(text = "${stage.departure} - ${stage.arrival}")
    }
    if (stage.distance > 0) {
        Text(text = "${stage.distance} km")
    }
    if (stage.profileType != null) {
        Text(text = stage.profileType.toString())
    }
}

@Composable
internal fun StageResults(
    stageResults: RaceDetailsViewModel.StageResults,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
    ) {
        when (stageResults) {
            is RaceDetailsViewModel.StageResults.RidersPointResult -> RidersPointResult(
                stageResults,
                onRiderSelected
            )

            is RaceDetailsViewModel.StageResults.RidersPointsPerPlaceResult -> RidersPointsPerPlaceResult(
                stageResults,
                onRiderSelected,
            )

            is RaceDetailsViewModel.StageResults.RidersTimeResult -> RidersTimeResult(
                stageResults,
                onRiderSelected
            )

            is RaceDetailsViewModel.StageResults.TeamsTimeResult -> TeamsTimeResult(
                stageResults,
                onTeamSelected
            )
        }
    }
}

@Composable
private fun RidersPointResult(
    stageResults: RaceDetailsViewModel.StageResults.RidersPointResult,
    onRiderSelected: (Rider) -> Unit,
) {
    stageResults.riders.forEach { (rider, position, points) ->
        Text(
            text = "$position. ${rider.fullName()} - $points",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRiderSelected(rider) },
        )
    }
}

@Composable
private fun RidersPointsPerPlaceResult(
    stageResults: RaceDetailsViewModel.StageResults.RidersPointsPerPlaceResult,
    onRiderSelected: (Rider) -> Unit,
) {
    stageResults.perPlaceResult.forEach { (place, riders) ->
        Text(text = "${place.name} - ${place.distance}")
        riders.forEach { (rider, position, points) ->
            Text(
                text = "$position. ${rider.fullName()} - $points",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRiderSelected(rider) },
            )
        }
        HorizontalDivider(thickness = 8.dp)
    }
}

@Composable
private fun RidersTimeResult(
    stageResults: RaceDetailsViewModel.StageResults.RidersTimeResult,
    onRiderSelected: (Rider) -> Unit,
) {
    stageResults.riders.forEachIndexed { i, (rider, position, time) ->
        val duration = if (i == 0) {
            time.seconds.toString()
        } else {
            "+${(time - stageResults.riders.first().time).seconds}"
        }
        Text(
            text = "$position. ${rider.fullName()} - $duration",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRiderSelected(rider) },
        )
    }
}

@Composable
private fun TeamsTimeResult(
    stageResults: RaceDetailsViewModel.StageResults.TeamsTimeResult,
    onTeamSelected: (Team) -> Unit,
) {
    stageResults.teams.forEachIndexed { i, (team, position, time) ->
        val duration = if (i == 0) {
            time.seconds.toString()
        } else {
            "+${(time - stageResults.teams.first().time).seconds}"
        }
        Text(
            text = "$position. ${team.name} - $duration",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTeamSelected(team) },
        )
    }
}
