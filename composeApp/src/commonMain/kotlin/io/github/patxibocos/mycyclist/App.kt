package io.github.patxibocos.mycyclist

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import io.github.patxibocos.mycyclist.domain.firebaseMessaging
import io.github.patxibocos.mycyclist.ui.scaffold.MyCyclistScaffold
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
internal fun App() {
    MaterialTheme {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context).crossfade(true).build()
        }
        firebaseDataRepository.initialize()
        firebaseMessaging.initialize()
        MyCyclistScaffold()
    }
}
