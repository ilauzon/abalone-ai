package com.bcit.abalone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfigMenu() {
    var selectedLayout by remember { mutableStateOf("Standard") }
    var selectedMode by remember { mutableStateOf("Vs. Human") }
    var player1Color by remember { mutableStateOf("Black") }
    var p1MoveLimit by remember { mutableStateOf(50f) }
    var p1TimeLimit by remember { mutableStateOf(300f) }
    var p2MoveLimit by remember { mutableStateOf(50f) }
    var p2TimeLimit by remember { mutableStateOf(300f) }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Game Settings", style = MaterialTheme.typography.h3)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Initial Layout:", style = MaterialTheme.typography.h6)
                BoardLayoutSelection(selectedLayout) {selectedLayout = it}
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Game mode:", style = MaterialTheme.typography.h6)
                ModeSelection(selectedMode) {selectedMode = it}
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Text("Player 1 Color:", style = MaterialTheme.typography.h6)
                ColorSelection(player1Color) { player1Color = it }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Move Limit (P1): ${p1MoveLimit.toInt()}", style = MaterialTheme.typography.h6)
                Slider(
                    value = p1MoveLimit,
                    onValueChange = { p1MoveLimit = it },
                    valueRange = 10f..100f)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Time Limit (P1): ${p1TimeLimit.toInt()} sec (${(p1TimeLimit / 60).toInt()} min)", style = MaterialTheme.typography.h6)
                Slider(
                    value = p1TimeLimit,
                    onValueChange = { p1TimeLimit = it },
                    valueRange = 60f..600f)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Move Limit (P2): ${p2MoveLimit.toInt()}", style = MaterialTheme.typography.h6)
                Slider(
                    value = p2MoveLimit,
                    onValueChange = { p2MoveLimit = it },
                    valueRange = 10f..100f)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Time Limit (P2): ${p2TimeLimit.toInt()} sec (${(p2TimeLimit / 60).toInt()} min)", style = MaterialTheme.typography.h6)
                Slider(
                    value = p2TimeLimit,
                    onValueChange = { p2TimeLimit = it },
                    valueRange = 60f..600f)
            }
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(onClick = {println("")}) {
                Text("Apply Settings")
            }
        }
    }
}

@Composable
fun BoardLayoutSelection(selectedLayout: String, onSelect: (String) -> Unit) {
    Column {
        listOf("Standard", "German Daisy", "Belgian Daisy").forEach { layout ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (selectedLayout == layout),
                    onClick = { onSelect(layout) }
                )
                Text(layout, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun ColorSelection(selectedColor: String, onColorSelected: (String) -> Unit) {
    listOf("Black", "White").forEach { color ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = (selectedColor == color), onClick = { onColorSelected(color) })
            Text(color, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun ModeSelection(selectedMode: String, onModeSelected: (String) -> Unit) {
    listOf("Vs. Human", "Vs. Bot").forEach { mode ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = (selectedMode == mode), onClick = { onModeSelected(mode) })
            Text(mode, modifier = Modifier.padding(start = 8.dp))
        }
    }
}