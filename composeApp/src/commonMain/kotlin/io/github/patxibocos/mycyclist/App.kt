package io.github.patxibocos.mycyclist

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import io.github.patxibocos.mycyclist.domain.repository.cyclingDataRepository
import io.github.patxibocos.mycyclist.domain.repository.messagingRepository
import io.github.patxibocos.mycyclist.ui.navigation.NavigationSuite
import io.github.patxibocos.mycyclist.ui.theme.AppTheme

@Composable
internal fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean,
) {
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
    ) {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context).crossfade(true).build()
        }
        cyclingDataRepository.initialize()
        messagingRepository.initialize()

        NavigationSuite()
    }
}
