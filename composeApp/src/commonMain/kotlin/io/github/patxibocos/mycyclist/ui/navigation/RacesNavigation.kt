package io.github.patxibocos.mycyclist.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.domain.entity.Team
import io.github.patxibocos.mycyclist.ui.race.details.RaceDetailsScreen
import io.github.patxibocos.mycyclist.ui.race.details.RaceDetailsViewModel
import io.github.patxibocos.mycyclist.ui.race.list.RaceListScreen
import io.github.patxibocos.mycyclist.ui.race.list.RaceListViewModel

@Composable
internal fun RaceDetails(
    raceAndStageId: Pair<String, String?>,
    onBackPressed: () -> Unit,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    viewModel: RaceDetailsViewModel = viewModel(key = raceAndStageId.first) {
        RaceDetailsViewModel(
            raceId = raceAndStageId.first,
            stageId = raceAndStageId.second,
        )
    },
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return
    Surface(modifier = Modifier.fillMaxSize()) {
        RaceDetailsScreen(
            uiState = uiState,
            onBackPressed = onBackPressed,
            onRiderSelected = onRiderSelected,
            onTeamSelected = onTeamSelected,
            onResultsModeChanged = viewModel::onResultsModeChanged,
            onClassificationTypeChanged = viewModel::onClassificationTypeChanged,
            onStageSelected = viewModel::onStageSelected,
            onParticipationsClicked = {},
        )
    }
}

@Composable
internal fun RaceList(
    listState: LazyListState = rememberLazyListState(),
    onRaceClick: (Race) -> Unit,
    onRaceStageClick: (Race, Stage) -> Unit,
    viewModel: RaceListViewModel = viewModel { RaceListViewModel() },
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value ?: return
    Surface(modifier = Modifier.fillMaxSize()) {
        RaceListScreen(
            uiState = uiState,
            listState = listState,
            onRaceClick = onRaceClick,
            onRaceStageClick = onRaceStageClick,
            onRefresh = viewModel::refresh,
        )
    }
}
