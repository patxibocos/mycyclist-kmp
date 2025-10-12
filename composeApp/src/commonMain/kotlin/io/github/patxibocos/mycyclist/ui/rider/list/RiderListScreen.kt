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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListViewModel.Sorting

@Composable
internal fun RiderListScreen(
    uiState: RiderListViewModel.UiState,
    topBarState: RiderListViewModel.TopBarState,
    listState: LazyListState,
    scrollBehavior: TopAppBarScrollBehavior,
    onRiderClick: (Rider) -> Unit,
    onRiderSearched: (String) -> Unit,
    onToggled: () -> Unit,
    onSortingSelected: (Sorting) -> Unit,
    onRefresh: () -> Unit,
) {
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
        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        state = listState,
    ) {
        when (uiState.riders) {
            is RiderListViewModel.UiState.Riders.ByLastName -> {
                uiState.riders.riders.forEach { (letter, riders) ->
                    stickyHeader {
                        Text(
                            text = "$letter",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 5.dp)
                        )
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
                        Text(
                            text = "$country ${EmojiUtil.getCountryEmoji(country)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 5.dp)
                        )
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.clickable { onRiderSelected(rider) }.padding(10.dp)
                .height(IntrinsicSize.Min)
        ) {
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
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = EmojiUtil.getCountryEmoji(rider.country),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    BasicText(
                        text = "${rider.lastName.uppercase()} ${rider.firstName}",
                        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.titleMedium.fontSize),
                        style = MaterialTheme.typography.titleMedium.copy(color = LocalContentColor.current),
                        maxLines = 1,
                    )
                }
                val age = remember(rider) { rider.age() }
                if (rider.height != null || rider.weight != null || age != null) {
                    RiderPersonalInfo(
                        age = age,
                        height = rider.height,
                        weight = rider.weight,
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            rider.uciRankingPosition?.let { position ->
                UciRankingBadge(position = position)
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
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.inverseOnSurface,
        fontWeight = FontWeight.Bold,
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = RoundedCornerShape(15.dp)
        ).padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun RiderPersonalInfo(age: Int?, height: Int?, weight: Int?) {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (age != null) {
            RiderAge(age = age)
        }
        if (height != null) {
            RiderHeight(height = height)
        }
        if (weight != null) {
            RiderWeight(weight = weight)
        }
    }
}

@Composable
private fun RiderAge(age: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.Bottom) {
        Icon(
            Icons.Default.Cake,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
        BasicText(
            text = "${age}y",
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                ),
                color = LocalContentColor.current,
            ),
            color = { onSurfaceVariantColor },
            autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.labelLarge.fontSize),
        )
    }
}

@Composable
private fun RiderHeight(height: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.Bottom) {
        Icon(
            Icons.Default.Straighten,
            contentDescription = null,
            modifier = Modifier.size(20.dp).rotate(degrees = 90f)
        )
        val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
        BasicText(
            text = "${height}cm",
            style = MaterialTheme.typography.labelLarge.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                ),
                color = LocalContentColor.current
            ),
            color = { onSurfaceVariantColor },
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.labelLarge.fontSize),
        )
    }
}

@Composable
private fun RiderWeight(weight: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.Bottom) {
        Icon(
            Icons.Default.Scale,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
        BasicText(
            text = "${weight}kg",
            style = MaterialTheme.typography.labelLarge.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                ),
                color = LocalContentColor.current
            ),
            color = { onSurfaceVariantColor },
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.labelLarge.fontSize),
        )
    }
}
