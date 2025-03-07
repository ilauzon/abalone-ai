package com.bcit.abalone

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(viewModel: AbaloneViewModel) {
    MaterialTheme {
        AbaloneGame(viewModel)
    }
}