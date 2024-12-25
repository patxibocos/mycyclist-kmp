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
    results: RaceDetailsViewModel.Results,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
    ) {
        when (results) {
            is RaceDetailsViewModel.Results.RidersPointResult -> RidersPointResult(results, onRiderSelected)
            is RaceDetailsViewModel.Results.RidersPointsPerPlaceResult -> RidersPointsPerPlaceResult(
                results,
                onRiderSelected,
            )

            is RaceDetailsViewModel.Results.RidersTimeResult -> RidersTimeResult(results, onRiderSelected)
            is RaceDetailsViewModel.Results.TeamsTimeResult -> TeamsTimeResult(results, onTeamSelected)
        }
    }
}

@Composable
private fun RidersPointResult(
    results: RaceDetailsViewModel.Results.RidersPointResult,
    onRiderSelected: (Rider) -> Unit,
) {
    results.riders.forEachIndexed { i, (rider, points) ->
        Text(
            text = "${i + 1}. ${rider.fullName()} - $points",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRiderSelected(rider) },
        )
    }
}

@Composable
private fun RidersPointsPerPlaceResult(
    results: RaceDetailsViewModel.Results.RidersPointsPerPlaceResult,
    onRiderSelected: (Rider) -> Unit,
) {
    results.perPlaceResult.forEach { (place, riders) ->
        Text(text = "${place.name} - ${place.distance}")
        riders.forEachIndexed { i, (rider, points) ->
            Text(
                text = "${i + 1}. ${rider.fullName()} - $points",
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
    results: RaceDetailsViewModel.Results.RidersTimeResult,
    onRiderSelected: (Rider) -> Unit,
) {
    results.riders.forEachIndexed { i, (rider, time) ->
        val duration = if (i == 0) {
            time.seconds.toString()
        } else {
            "+${(time - results.riders.first().time).seconds}"
        }
        Text(
            text = "${i + 1}. ${rider.fullName()} - $duration",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRiderSelected(rider) },
        )
    }
}

@Composable
private fun TeamsTimeResult(
    results: RaceDetailsViewModel.Results.TeamsTimeResult,
    onTeamSelected: (Team) -> Unit,
) {
    results.teams.forEachIndexed { i, (team, time) ->
        val duration = if (i == 0) {
            time.seconds.toString()
        } else {
            "+${(time - results.teams.first().time).seconds}"
        }
        Text(
            text = "${i + 1}. ${team.name} - $duration",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTeamSelected(team) },
        )
    }
}
