package compose.project.demo.ui.team_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.project.demo.domain.Rider
import compose.project.demo.ui.team_details.TeamDetailsViewModel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsRoute(
    teamId: String,
    onBackPressed: () -> Unit,
    onRiderSelected: (Rider) -> Unit,
    viewModel: TeamDetailsViewModel = viewModel { TeamDetailsViewModel() }
) {
    val viewState by remember(teamId) {
        viewModel.uiState(teamId = teamId)
    }.collectAsStateWithLifecycle()
    val state = viewState ?: return
    TeamDetailsScreen(
        state = state,
        onBackPressed = onBackPressed,
        onRiderSelected = onRiderSelected,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsScreen(
    state: UiState,
    onBackPressed: () -> Unit,
    onRiderSelected: (Rider) -> Unit,
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = state.team.name,
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
        Text(text = state.team.name)
        state.riders.forEach {
            RiderRow(rider = it, onRiderSelected = onRiderSelected)
        }
    }
}

@Composable
private fun RiderRow(rider: Rider, onRiderSelected: (Rider) -> Unit) {
    Text(
        text = rider.lastName,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRiderSelected(rider) },
    )
}
