package compose.project.demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import compose.project.demo.ui.scaffold.MyCyclistScaffold
import compose.project.demo.util.CodePointUtil
import compose.project.demo.util.CodePointUtil.Companion.buildStringFromCodePoints
import org.jetbrains.compose.ui.tooling.preview.Preview

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context).crossfade(true).logger(DebugLogger()).build()

@Composable
@Preview
fun App() {
    MaterialTheme {
        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }
        MyCyclistScaffold()
    }
}

fun getCountryEmoji(countryCode: String): String {
    val codePoints = countryCode.uppercase()
        .map { char -> 127397 + CodePointUtil.codePointAt(char.toString(), 0) }
        .toIntArray()
    return buildStringFromCodePoints(codePoints)
}