package compose.project.demo.ui.riders_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import compose.project.demo.domain.Rider
import compose.project.demo.ui.emoji.getCountryEmoji

@Composable
fun RidersListScreen(
    onRiderClick: (Rider) -> Unit,
    viewModel: RidersListViewModel = viewModel { RidersListViewModel() },
) {
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    val state = viewState ?: return
    RidersList(state.riders, onRiderClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RidersList(
    riders: List<Rider>,
    onRiderSelected: (Rider) -> Unit,
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(riders, key = Rider::id) { rider ->
            RiderRow(rider, onRiderSelected)
        }
    }
}

@Composable
private fun RiderRow(
    rider: Rider,
    onRiderSelected: (Rider) -> Unit,
) {
    Column(modifier = Modifier.clickable { onRiderSelected(rider) }) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = rider.photo,
                modifier = Modifier
                    .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    .padding(2.dp)
                    .size(75.dp)
                    .clip(CircleShape),
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = "${rider.lastName.uppercase()} ${rider.firstName}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Country(countryCode = rider.country, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
    }
}

@Composable
private fun Country(countryCode: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "${getCountryEmoji(countryCode)} $countryCode",
        style = MaterialTheme.typography.bodyLarge,
    )
}
