package io.github.patxibocos.mycyclist.ui.scaffold

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
internal fun MyCyclistScaffold() {
    val navController by rememberUpdatedState(rememberNavController())

    Scaffold { paddingValues ->
        NavigationSuite(
            navController = navController,
            modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues),
        )
    }
}
