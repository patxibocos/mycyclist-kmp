package io.github.patxibocos.mycyclist

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MyCyclist",
    ) {
        App(darkTheme = isSystemInDarkTheme(), dynamicColor = false)
    }
}
