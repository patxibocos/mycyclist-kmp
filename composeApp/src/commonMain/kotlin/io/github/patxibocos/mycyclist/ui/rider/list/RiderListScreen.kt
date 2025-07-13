package io.github.patxibocos.mycyclist.ui.rider.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListViewModel.Sorting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RiderListScreen(
    uiState: RiderListViewModel.UiState,
    topBarState: RiderListViewModel.TopBarState,
    onRiderClick: (Rider) -> Unit,
    onRiderSearched: (String) -> Unit,
    onToggled: () -> Unit,
    onSortingSelected: (Sorting) -> Unit,
    onRefresh: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    PullToRefreshBox(
        isRefreshing = uiState.refreshing,
        onRefresh = onRefresh,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        val focusManager = LocalFocusManager.current
        Column {
            TopBar(
                topBarState = topBarState,
                focusManager = focusManager,
                scrollBehavior = scrollBehavior,
                onSortingSelected = onSortingSelected,
                onSearched = onRiderSearched,
                onToggled = onToggled,
                onClicked = {
                    listState.scrollToItem(0)
                },
            )
            RiderList(
                listState = listState,
                uiState = uiState,
                onRiderClick = onRiderClick
            )
        }
    }
}

@Composable
private fun RiderList(
    listState: LazyListState,
    uiState: RiderListViewModel.UiState,
    onRiderClick: (Rider) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        state = listState,
    ) {
        when (uiState.riders) {
            is RiderListViewModel.UiState.Riders.ByLastName -> {
                uiState.riders.riders.forEach { (letter, riders) ->
                    stickyHeader {
                        Text(text = letter.toString())
                    }
                    items(riders, key = Rider::id) { rider ->
                        RiderRow(
                            rider = rider,
                            onRiderSelected = onRiderClick,
                        )
                    }
                }
            }

            is RiderListViewModel.UiState.Riders.ByCountry -> {
                uiState.riders.riders.forEach { (country, riders) ->
                    stickyHeader {
                        Text(text = country)
                    }
                    items(riders, key = Rider::id) { rider ->
                        RiderRow(
                            rider = rider,
                            onRiderSelected = onRiderClick,
                        )
                    }
                }
            }

            is RiderListViewModel.UiState.Riders.ByUciRanking -> {
                items(uiState.riders.riders, key = Rider::id) { rider ->
                    RiderRow(
                        rider = rider,
                        onRiderSelected = onRiderClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun RiderRow(
    rider: Rider,
    onRiderSelected: (Rider) -> Unit,
) {
    Card(modifier = Modifier.clickable { onRiderSelected(rider) }) {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp).height(IntrinsicSize.Min)) {
            AsyncImage(
                model = rider.photo,
                modifier = Modifier
                    .shadow(5.dp, CircleShape)
                    .size(75.dp)
                    .clip(CircleShape),
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${rider.lastName.uppercase()} ${rider.firstName}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    rider.uciRankingPosition?.let { position ->
                        UciRankingBadge(position = position)
                    }
                }
                RiderDemographics(
                    country = rider.country,
                    birthPlace = rider.birthPlace,
                )
                val age = remember(rider) { rider.age() }
                if (rider.height != null || rider.weight != null || age != null) {
                    RiderPersonalInfo(
                        age = age,
                        height = rider.height,
                        weight = rider.weight,
                    )
                }
            }
        }
    }
}

@Composable
private fun UciRankingBadge(
    position: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "#$position",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.inverseOnSurface,
        fontWeight = FontWeight.Bold,
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = RoundedCornerShape(15.dp)
        ).padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun RiderDemographics(
    country: String,
    birthPlace: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = EmojiUtil.getCountryEmoji(country),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            modifier = Modifier.clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(5.dp))
                .padding(horizontal = 5.dp),
            text = country,
            style = MaterialTheme.typography.bodyMedium,
        )
        birthPlace?.let {
            Text(
                text = birthPlace,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RiderPersonalInfo(
    age: Int?,
    height: Int?,
    weight: Int?,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        age?.let {
            Icon(
                Icons.Default.Cake,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = "$it years",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        height?.let { height ->
            Spacer(modifier = Modifier.size(10.dp))
            Icon(
                Icons.Default.Straighten,
                contentDescription = null,
                modifier = Modifier.size(15.dp).rotate(degrees = 90f)
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "$height cm")
        }
        weight?.let { weight ->
            Spacer(modifier = Modifier.size(10.dp))
            Icon(
                Icons.Default.Scale,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "$weight kg")
        }
    }
}
