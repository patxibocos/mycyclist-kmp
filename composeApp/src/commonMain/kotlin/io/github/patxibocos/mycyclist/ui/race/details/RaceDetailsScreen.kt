package io.github.patxibocos.mycyclist.ui.race.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.domain.entity.Team
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RaceDetailsScreen(
    uiState: RaceDetailsViewModel.UiState,
    backEnabled: Boolean,
    onBackPressed: () -> Unit,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onResultsModeChanged: (RaceDetailsViewModel.ResultsMode) -> Unit,
    onClassificationTypeChanged: (RaceDetailsViewModel.ClassificationType) -> Unit,
    onStageSelected: (Int) -> Unit,
    onParticipationsClicked: (Race) -> Unit,
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = uiState.race.name,
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
        Button(onClick = { onParticipationsClicked(uiState.race) }) {
            Text(text = "Participants")
        }
        if (uiState.race.stages.size == 1) {
            val stage = uiState.race.stages.first()
            SingleStage(
                stage,
                uiState.stagesResults.results.first().second,
                onRiderSelected,
                onTeamSelected,
            )
        } else {
            StagesList(
                stages = uiState.race.stages.toImmutableList(),
                stagesResults = uiState.stagesResults,
                currentStageIndex = uiState.currentStageIndex,
                resultsMode = uiState.resultsMode,
                classificationType = uiState.classificationType,
                onRiderSelected = onRiderSelected,
                onTeamSelected = onTeamSelected,
                onResultsModeChanged = onResultsModeChanged,
                onClassificationTypeChanged = onClassificationTypeChanged,
                onStageSelected = onStageSelected,
            )
        }
    }
}

@Composable
private fun SingleStage(
    stage: Stage,
    stageResults: RaceDetailsViewModel.StageResults,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    StageInfo(stage)
    StageResults(stageResults, onRiderSelected, onTeamSelected)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StagesList(
    stages: ImmutableList<Stage>,
    stagesResults: RaceDetailsViewModel.StagesResults,
    currentStageIndex: Int,
    resultsMode: RaceDetailsViewModel.ResultsMode,
    classificationType: RaceDetailsViewModel.ClassificationType,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onResultsModeChanged: (RaceDetailsViewModel.ResultsMode) -> Unit,
    onClassificationTypeChanged: (RaceDetailsViewModel.ClassificationType) -> Unit,
    onStageSelected: (Int) -> Unit,
) {
    val pagerState = key(stages) {
        rememberPagerState(
            initialPage = currentStageIndex,
            pageCount = { stages.size }
        )
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onStageSelected(page)
        }
    }
    val coroutineScope = rememberCoroutineScope()
    PrimaryScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        stages.forEachIndexed { index, _ ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
            ) {
                Text(text = "Stage ${index + 1}")
            }
        }
    }
    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        state = pagerState,
        verticalAlignment = Alignment.Top,
    ) { page ->
        val stage = stages[page]
        Stage(
            stage = stage,
            stageResults = stagesResults.results[page].second,
            resultsMode = resultsMode,
            classificationType = classificationType,
            onResultsModeChanged = onResultsModeChanged,
            onClassificationTypeChanged = onClassificationTypeChanged,
            onRiderSelected = onRiderSelected,
            onTeamSelected = onTeamSelected,
        )
    }
}

@Composable
private fun Stage(
    stage: Stage,
    stageResults: RaceDetailsViewModel.StageResults,
    resultsMode: RaceDetailsViewModel.ResultsMode,
    classificationType: RaceDetailsViewModel.ClassificationType,
    onResultsModeChanged: (RaceDetailsViewModel.ResultsMode) -> Unit,
    onClassificationTypeChanged: (RaceDetailsViewModel.ClassificationType) -> Unit,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        StageInfo(stage)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ElevatedFilterChip(
                selected = resultsMode == RaceDetailsViewModel.ResultsMode.Stage,
                onClick = { onResultsModeChanged(RaceDetailsViewModel.ResultsMode.Stage) },
                label = {
                    Text(text = RaceDetailsViewModel.ResultsMode.Stage.toString())
                },
            )
            ElevatedFilterChip(
                selected = resultsMode == RaceDetailsViewModel.ResultsMode.General,
                onClick = { onResultsModeChanged(RaceDetailsViewModel.ResultsMode.General) },
                label = {
                    Text(text = RaceDetailsViewModel.ResultsMode.General.toString())
                },
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            RaceDetailsViewModel.ClassificationType.entries.forEach {
                ElevatedFilterChip(
                    selected = classificationType == it,
                    onClick = { onClassificationTypeChanged(it) },
                    label = {
                        Text(text = it.toString())
                    },
                )
            }
        }
        StageResults(stageResults, onRiderSelected, onTeamSelected)
    }
}
