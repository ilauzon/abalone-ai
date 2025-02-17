package com.bcit.abalone

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

/**
 * Launches a new desktop application.
 */
fun main() = application {
    // The `application` function takes a lambda where the UI is initialized.
    Window(
        onCloseRequest = ::exitApplication,
        title = "abalone",
        state = rememberWindowState(
            width = 1280.dp,
            height = 720.dp,
        )
    ) {
        App()
    }
}