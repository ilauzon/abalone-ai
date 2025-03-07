package com.bcit.abalone

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

/**
 * Launches a new desktop application.
 */
fun main() = application {
    // The `application` function takes a lambda where the UI is initialized.
    val viewModel: AbaloneViewModel = remember {AbaloneViewModel()}

    Window(
        onCloseRequest = ::exitApplication,
        title = "abalone",
        state = rememberWindowState(
            width = 1280.dp,
            height = 720.dp,
        )
    ) {
        App(viewModel)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "config menu",
        state = rememberWindowState(
            width = 720.dp,
            height = 720.dp
        )
    ) {
        ConfigMenu(viewModel)
    }
}