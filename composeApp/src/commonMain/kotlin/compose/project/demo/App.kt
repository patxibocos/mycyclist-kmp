package compose.project.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import composedemo.composeapp.generated.resources.Res
import composedemo.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context).crossfade(true).logger(DebugLogger()).build()

@Composable
@Preview
fun App(
    viewModel: TestViewModel = viewModel { TestViewModel() },
) {
    MaterialTheme {
        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.Email, null) },
                        label = { Text("Races") },
                        selected = true,
                        onClick = {},
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.Face, null) },
                        label = { Text("Riders") },
                        selected = false,
                        onClick = {},
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.Person, null) },
                        label = { Text("Teams") },
                        selected = false,
                        onClick = {},
                    )
                }
            }
        ) {
            val uiState = viewModel.uiState.collectAsState()
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(uiState.value.text)
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                AsyncImage(
                    model = "https://freepngimg.com/thumb/emoji/3-2-love-hearts-eyes-emoji-png.png",
                    contentDescription = null,
                )
            }
        }
    }
}
