package io.github.patxibocos.mycyclist.ui.race.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.patxibocos.mycyclist.LocalBackButtonVisibility
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.domain.usecase.ClassificationType
import io.github.patxibocos.mycyclist.domain.usecase.ResultsMode
import io.github.patxibocos.mycyclist.domain.usecase.StageResult
import io.github.patxibocos.mycyclist.domain.usecase.StageResults
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
internal fun RaceDetailsScreen(
    uiState: RaceDetailsViewModel.UiState,
    onBackPressed: () -> Unit,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onResultsModeChanged: (ResultsMode) -> Unit,
    onClassificationTypeChanged: (ClassificationType) -> Unit,
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
                if (LocalBackButtonVisibility.current) {
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
                stage = stage,
                stageResults = uiState.stagesResults.first().second,
                onRiderSelected = onRiderSelected,
                onTeamSelected = onTeamSelected,
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
    stageResults: StageResults,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    StageInfo(stage)
    StageResults(stageResults, onRiderSelected, onTeamSelected)
}

@Composable
private fun StagesList(
    stages: ImmutableList<Stage>,
    stagesResults: ImmutableList<StageResult>,
    currentStageIndex: Int,
    resultsMode: ResultsMode,
    classificationType: ClassificationType,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onResultsModeChanged: (ResultsMode) -> Unit,
    onClassificationTypeChanged: (ClassificationType) -> Unit,
    onStageSelected: (Int) -> Unit,
) {
    val pagerState = remember(stages) {
        PagerState(
            currentPage = currentStageIndex,
            pageCount = { stages.size }
        )
    }
    LaunchedEffect(currentStageIndex) {
        if (pagerState.currentPage != currentStageIndex) {
            pagerState.scrollToPage(currentStageIndex)
        }
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
            stageResults = stagesResults[page].second,
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
    stageResults: StageResults,
    resultsMode: ResultsMode,
    classificationType: ClassificationType,
    onResultsModeChanged: (ResultsMode) -> Unit,
    onClassificationTypeChanged: (ClassificationType) -> Unit,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        StageInfo(stage)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ElevatedFilterChip(
                selected = resultsMode == ResultsMode.Stage,
                onClick = { onResultsModeChanged(ResultsMode.Stage) },
                label = {
                    Text(text = ResultsMode.Stage.toString())
                },
            )
            ElevatedFilterChip(
                selected = resultsMode == ResultsMode.General,
                onClick = { onResultsModeChanged(ResultsMode.General) },
                label = {
                    Text(text = ResultsMode.General.toString())
                },
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ClassificationType.entries.forEach {
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
