package compose.project.demo.ui.rider.details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import compose.project.demo.domain.Race
import compose.project.demo.domain.Stage
import compose.project.demo.domain.Team

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
internal fun RiderDetailsRoute(
    riderId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPressed: () -> Unit,
    onRaceSelected: (Race) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
    viewModel: RiderDetailsViewModel = viewModel { RiderDetailsViewModel() },
) {
    val viewState by remember(riderId) {
        viewModel.uiState(riderId = riderId)
    }.collectAsStateWithLifecycle()
    val state = viewState ?: return
    sharedTransitionScope.RiderDetailsScreen(
        state = state,
        animatedVisibilityScope = animatedVisibilityScope,
        onBackPressed = onBackPressed,
        onRaceSelected = onRaceSelected,
        onTeamSelected = onTeamSelected,
        onStageSelected = onStageSelected,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SharedTransitionScope.RiderDetailsScreen(
    state: RiderDetailsViewModel.UiState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPressed: () -> Unit,
    onRaceSelected: (Race) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = state.rider.fullName(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp),
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
            }
        )
        AsyncImage(
            model = state.rider.photo,
            modifier = Modifier
                .sharedElement(
                    state = rememberSharedContentState(key = state.rider.id),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                .padding(2.dp)
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape),
            alignment = Alignment.TopCenter,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        if (state.rider.uciRankingPosition != null) {
            Text(text = "UCI Ranking: ${state.rider.uciRankingPosition}")
        }
        Text(
            text = state.team.name,
            modifier = Modifier.clickable {
                onTeamSelected(state.team)
            },
        )
        state.currentParticipation?.let { currentParticipation ->
            Text(
                text = "Currently running ${currentParticipation.race.name}",
                modifier = Modifier.clickable {
                    onRaceSelected(state.currentParticipation.race)
                },
            )
        }
        state.results.forEach { lastResult ->
            when (lastResult) {
                is RiderDetailsViewModel.Result.RaceResult -> Text(
                    text = "${lastResult.position} on ${lastResult.race.name}",
                    modifier = Modifier.clickable {
                        onRaceSelected(lastResult.race)
                    },
                )

                is RiderDetailsViewModel.Result.StageResult -> Text(
                    text = "${lastResult.position} on stage ${lastResult.stageNumber} of ${lastResult.race.name}",
                    modifier = Modifier.clickable {
                        onStageSelected(lastResult.race, lastResult.stage)
                    },
                )
            }
        }
    }
}
