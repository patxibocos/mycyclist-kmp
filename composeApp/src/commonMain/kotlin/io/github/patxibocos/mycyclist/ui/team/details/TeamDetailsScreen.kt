package io.github.patxibocos.mycyclist.ui.team.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patxibocos.mycyclist.domain.entity.Rider
import io.github.patxibocos.mycyclist.ui.emoji.EmojiUtil
import io.github.patxibocos.mycyclist.ui.util.rememberWithSize

@Composable
internal fun TeamDetailsScreen(
    uiState: TeamDetailsViewModel.UiState,
    backEnabled: Boolean,
    onBackPressed: () -> Unit,
    onRiderSelected: (Rider) -> Unit,
) {
    BoxWithConstraints {
        val backEnabled = rememberWithSize(backEnabled)
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
                    if (backEnabled) {
                        IconButton(onClick = onBackPressed) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    }
                }
            )
            HorizontalDivider()
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    .fillMaxSize().verticalScroll(rememberScrollState())
            ) {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(10.dp).height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AsyncImage(
                            model = uiState.team.jersey,
                            modifier = Modifier
                                .shadow(5.dp, MaterialTheme.shapes.medium)
                                .size(75.dp)
                                .clip(MaterialTheme.shapes.medium),
                            alignment = Alignment.TopCenter,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                        )
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = EmojiUtil.getCountryEmoji(uiState.team.country),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                BasicText(
                                    text = uiState.team.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = LocalContentColor.current
                                    ),
                                    maxLines = 1,
                                    autoSize = TextAutoSize.StepBased(
                                        maxFontSize = MaterialTheme.typography.titleMedium.fontSize
                                    ),
                                )
                            }
                            uiState.team.abbreviation?.let {
                                Text(
                                    uiState.team.abbreviation,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            Row(verticalAlignment = Alignment.Bottom) {
                                Icon(Icons.Default.PedalBike, null)
                                Spacer(modifier = Modifier.size(5.dp))
                                Text(
                                    uiState.team.bike,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(5.dp))
                uiState.team.website?.let { website ->
                    val uriHandler = LocalUriHandler.current
                    Surface(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Link,
                                contentDescription = null,
                            )
                            Text(
                                text = website,
                                color = Color(0xFF1976D2),
                                modifier = Modifier.clickable {
                                    uriHandler.openUri(website)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "RIDERS",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 10.dp),
                )
                Surface(
                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium),
                ) {
                    FlowRow(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        uiState.riders.forEach { rider ->
                            Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                                RiderItem(rider = rider, onRiderSelected = onRiderSelected)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RiderItem(rider: Rider, onRiderSelected: (Rider) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onRiderSelected(rider) }.width(75.dp)
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
        Text(
            text = rider.fullName(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
