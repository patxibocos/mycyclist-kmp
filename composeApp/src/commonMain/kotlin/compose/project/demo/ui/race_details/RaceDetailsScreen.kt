package compose.project.demo.ui.race_details

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceDetailsScreen(
    raceId: String,
    stageId: String?,
    viewModel: RaceDetailsViewModel = viewModel { RaceDetailsViewModel() },
    onBackPressed: () -> Unit,
) {
    Column {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
            }
        )
        val viewState by viewModel.uiState(raceId, stageId).collectAsState()
        val state = viewState
        if (state == null) {
            return@Column
        }
        when (state) {
            is RaceDetailsViewModel.UiState.RaceDetails -> Text(state.race.name)
        }
    }
}