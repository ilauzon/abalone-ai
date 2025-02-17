package com.bcit.abalone

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

@Composable
fun AbaloneGame(viewModel: AbaloneViewModel) {
    val board = viewModel.boardState.value
    val selectedCells = remember { mutableStateListOf<Cell>() }

    Row(modifier = Modifier.fillMaxSize()) {
        // Left panel for table including marbles out, moves, and time.
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color.LightGray)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text("table including marbles out, moves, and time", fontSize = 20.sp)
        }

        // Center panel with game board and buttons
        Column(
            modifier = Modifier
                .weight(2f)
                .background(Color.White)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Draw game board
            Column {
                board.forEachIndexed { _, row ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(start = (9 - row.size) * 18.dp)
                    ) {
                        row.forEach { cell ->
                            val isSelected = selectedCells.contains(cell)
                            Box(
                                modifier = Modifier
                                    .size(35.dp)
                                    .clickable {
                                        if (cell.piece == Piece.Empty){
                                            viewModel.moveMarbles(selectedCells, cell)
                                        }else {
                                            viewModel.selectMarbles(selectedCells, cell)
                                        }
                                    }
                                    .background(
                                        when (cell.piece) {
                                            Piece.Blue -> Color.Blue
                                            Piece.Red -> Color.Red
                                            else -> Color.White
                                        },
                                        shape = CircleShape
                                    )
                                    .border(2.dp, if (isSelected) Color.Green else Color.Gray, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${cell.letter}${cell.number}", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            // buttons
            Spacer(modifier = Modifier.height(10.dp))
            Row {

                Button(onClick = { viewModel.boardState.value = createBoard() }, modifier = Modifier.padding(1.dp)) {
                    Text("Start / Reset")
                }
                Button(onClick = { /* Reset logic here */ }, modifier = Modifier.padding(1.dp)) {
                    Text("Stop / Pause")
                }
                Button(onClick = { /* Reset logic here */ }, modifier = Modifier.padding(1.dp)) {
                    Text("Last move")
                }
            }
        }

        // Right panel for moves info.
        Box(
            modifier = Modifier
                .weight(1.5f)
                .background(Color.LightGray)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text("moves info", fontSize = 20.sp)
        }
    }
}
