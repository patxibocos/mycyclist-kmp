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
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.domain.usecase.StageResults
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
    stageResults: StageResults,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
    ) {
        when (stageResults) {
            is StageResults.RidersPointResult -> RidersPointResult(
                stageResults,
                onRiderSelected
            )

            is StageResults.RidersPointsPerPlaceResult -> RidersPointsPerPlaceResult(
                stageResults,
                onRiderSelected,
            )

            is StageResults.RidersTimeResult -> RidersTimeResult(
                stageResults,
                onRiderSelected
            )

            is StageResults.TeamsTimeResult -> TeamsTimeResult(
                stageResults,
                onTeamSelected
            )
        }
    }
}

@Composable
private fun RidersPointResult(
    stageResults: StageResults.RidersPointResult,
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
    stageResults: StageResults.RidersPointsPerPlaceResult,
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
    stageResults: StageResults.RidersTimeResult,
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
    stageResults: StageResults.TeamsTimeResult,
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
