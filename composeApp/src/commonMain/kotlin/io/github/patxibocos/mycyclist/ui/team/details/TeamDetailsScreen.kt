package io.github.patxibocos.mycyclist.ui.team.details

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.patxibocos.mycyclist.domain.Rider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TeamDetailsScreen(
    uiState: TeamDetailsViewModel.UiState,
    onBackPressed: () -> Unit,
    onRiderSelected: (Rider) -> Unit,
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = uiState.team.name,
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
        Text(text = uiState.team.name)
        uiState.riders.forEach {
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
