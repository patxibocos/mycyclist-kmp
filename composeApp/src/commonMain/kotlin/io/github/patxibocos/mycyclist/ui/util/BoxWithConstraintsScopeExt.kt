package io.github.patxibocos.mycyclist.ui.util

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> BoxWithConstraintsScope.rememberWithSize(value: T): T {
    return remember(maxWidth, maxHeight) { value }
}
