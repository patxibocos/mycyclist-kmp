package io.github.patxibocos.mycyclist.ui.rider.details

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.domain.usecase.RiderResult
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RiderDetailsScreen(
    uiState: RiderDetailsViewModel.UiState,
    backEnabled: Boolean,
    onBackPressed: () -> Unit,
    onRaceSelected: (Race) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = uiState.rider.fullName(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp),
                )
            },
            navigationIcon = {
                if (backEnabled) {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                }
            }
        )
        RiderPhoto(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            rider = uiState.rider,
        )
        if (uiState.rider.uciRankingPosition != null) {
            Text(text = "UCI Ranking: ${uiState.rider.uciRankingPosition}")
        }
        if (uiState.team != null) {
            Text(
                text = uiState.team.name,
                modifier = Modifier.clickable {
                    onTeamSelected(uiState.team)
                },
            )
        }
        uiState.currentParticipation?.let { currentParticipation ->
            Text(
                text = "Currently running ${currentParticipation.race.name}",
                modifier = Modifier.clickable {
                    onRaceSelected(uiState.currentParticipation.race)
                },
            )
        }
        RiderResults(
            results = uiState.results,
            onRaceSelected = onRaceSelected,
            onStageSelected = onStageSelected,
        )
    }
}

@Composable
private fun RiderResults(
    results: ImmutableList<RiderResult>,
    onRaceSelected: (Race) -> Unit,
    onStageSelected: (Race, Stage) -> Unit
) {
    results.forEach { lastResult ->
        when (lastResult) {
            is RiderResult.RaceResult -> Text(
                text = "${lastResult.position} on ${lastResult.race.name}",
                modifier = Modifier.clickable {
                    onRaceSelected(lastResult.race)
                },
            )

            is RiderResult.StageResult -> Text(
                text = "${lastResult.position} on stage ${lastResult.stageNumber} of ${lastResult.race.name}",
                modifier = Modifier.clickable {
                    onStageSelected(lastResult.race, lastResult.stage)
                },
            )
        }
    }
}

@Composable
private fun RiderPhoto(
    rider: Rider,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = rider.photo,
        modifier = modifier
            .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            .padding(2.dp)
            .size(150.dp)
            .clip(CircleShape),
        alignment = Alignment.TopCenter,
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
}
