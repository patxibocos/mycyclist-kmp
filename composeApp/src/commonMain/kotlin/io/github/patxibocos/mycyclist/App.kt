package io.github.patxibocos.mycyclist

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
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

        val navController by rememberUpdatedState(rememberNavController())
        Scaffold { paddingValues ->
            NavigationSuite(
                navController = navController,
                modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues),
            )
        }
    }
}
