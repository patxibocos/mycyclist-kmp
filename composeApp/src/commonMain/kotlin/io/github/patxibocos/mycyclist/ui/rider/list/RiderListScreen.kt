package io.github.patxibocos.mycyclist.ui.rider.list

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListViewModel.Sorting

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun RiderListScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    uiState: RiderListViewModel.UiState,
    topBarState: RiderListViewModel.TopBarState,
    onRiderClick: (Rider) -> Unit,
    onRiderSearched: (String) -> Unit,
    onToggled: () -> Unit,
    onSortingSelected: (Sorting) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    Column {
        TopBar(
            topBarState = topBarState,
            focusManager = focusManager,
            onSortingSelected = onSortingSelected,
            onSearched = onRiderSearched,
            onToggled = onToggled,
            onClicked = {
                lazyListState.scrollToItem(0)
            },
        )
        sharedTransitionScope.RiderList(
            lazyListState,
            uiState,
            animatedVisibilityScope,
            onRiderClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.RiderList(
    lazyListState: LazyListState,
    uiState: RiderListViewModel.UiState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRiderClick: (Rider) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        state = lazyListState,
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
                            animatedVisibilityScope = animatedVisibilityScope,
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
                            animatedVisibilityScope = animatedVisibilityScope,
                            onRiderSelected = onRiderClick,
                        )
                    }
                }
            }

            is RiderListViewModel.UiState.Riders.ByUciRanking -> {
                items(uiState.riders.riders, key = Rider::id) { rider ->
                    RiderRow(
                        rider = rider,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onRiderSelected = onRiderClick,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.RiderRow(
    rider: Rider,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRiderSelected: (Rider) -> Unit,
) {
    Column(modifier = Modifier.clickable { onRiderSelected(rider) }) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = rider.photo,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = rider.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
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
        text = "${EmojiUtil.getCountryEmoji(countryCode)} $countryCode",
        style = MaterialTheme.typography.bodyLarge,
    )
}
