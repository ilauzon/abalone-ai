/**
 * Contains the AbaloneGame and TableCell Composables.
 */

package com.bcit.abalone

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

/**
 * The view for the game board.
 */
@Composable
fun AbaloneGame(viewModel: AbaloneViewModel) {
    val board = viewModel.boardState.value
    val currentPlayer = viewModel.currentPlayer.value
    val selectedCells = remember { mutableStateListOf<Cell>() }
    val blueMoveNumber = viewModel.blueMoveNumber.value
    val redMoveNumber = viewModel.redMoveNumber.value
    val bluePiecesTaken = viewModel.bluePiecesTaken.value
    val redPiecesTaken = viewModel.redPiecesTaken.value
    val blueTimeRemaining = viewModel.blueTimeRemaining.value
    val redTimeRemaining = viewModel.redTimeRemaining.value

    Row(modifier = Modifier.fillMaxSize()) {
        // Left panel for table including marbles out, moves, and time.
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1.5f)
                .background(Color.LightGray)
                .fillMaxHeight()
        ) {
            Row{
                Box(modifier = Modifier
                    .background(Color.White)
                    .width(125.dp)
                ) {
                    Column{
                        TableCell("")
                        TableCell("Pieces Taken")
                        TableCell("Move Number")
                        TableCell("Remaining Time")
                        TableCell("Whose Turn")
                    }
                }
                Box(modifier = Modifier
                    .background(Color.White)
                    .width(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column{
                        TableCell("BLUE")
                        TableCell("$bluePiecesTaken")
                        TableCell("$blueMoveNumber/30")
                        TableCell(formatTime(blueTimeRemaining))
                        if (currentPlayer == Piece.Blue) {
                            Box(
                                modifier = Modifier.background(Color.Blue).fillMaxWidth()
                                    .height(25.dp)
                            )
                        } else {
                            TableCell("")
                        }
                    }
                }
                Box(modifier = Modifier
                    .background(Color.White)
                    .width(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(verticalArrangement = Arrangement.SpaceEvenly){
                        TableCell("RED")
                        TableCell("$redPiecesTaken")
                        TableCell("$redMoveNumber/30")
                        TableCell(formatTime(redTimeRemaining))
                        if (currentPlayer == Piece.Red) {
                            Box(
                                modifier = Modifier.background(Color.Red).fillMaxWidth()
                                    .height(25.dp)
                            )
                        } else {
                            TableCell("")
                        }
                    }
                }
            }
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
                Text("Time Remaining: 30:00:00",
                    modifier = Modifier.padding(20.dp),
                    fontSize = 25.sp)
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
                                        if (cell.piece != currentPlayer ){
                                            viewModel.moveMarbles(selectedCells, cell)
                                            selectedCells.clear()
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

                Button(onClick = { viewModel.boardState.value=createBoard() }, modifier = Modifier.padding(1.dp)) {
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

        // Right panel for moves table.
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1.5f)
                .background(Color.LightGray)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .border(BorderStroke(0.25.dp, Color.Black))
                    .background(Color.White)
                    .width(340.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Previous Moves", modifier = Modifier.padding(1.5.dp, 0.dp))
            }
            Row{
                Box(modifier = Modifier
                    .background(Color.White)
                    .width(60.dp)
                ) {
                    Column{
                        TableCell("Player")
                        TableCell("")
                        TableCell("")
                        TableCell("")
                        TableCell("Agent")
                        TableCell("Agent")
                    }
                }
                Box(modifier = Modifier
                    .background(Color.White)
                    .width(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column{
                        TableCell("Path")
                        TableCell("")
                        TableCell("")
                        TableCell("")
                        TableCell("[F2]->[F3]")
                        TableCell("[A1, A2]->[B1, B2]")
                    }
                }
                Box(modifier = Modifier
                    .background(Color.White)
                    .width(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column{
                        TableCell("Duration")
                        TableCell("")
                        TableCell("")
                        TableCell("")
                        TableCell("0.737s")
                        TableCell("0.532s")
                    }
                }
            }
            Row(modifier = Modifier.width(340.dp)){
                Box(
                    modifier = Modifier
                        .border(BorderStroke(0.25.dp, Color.Black))
                        .background(Color.White)
                        .width(260.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text("Total", modifier = Modifier.padding(1.5.dp, 0.dp))
                }
                TableCell("29m 34.99s")
            }
        }
    }
}

/**
 * The view for a single cell in a plain table.
 */
@Composable
fun TableCell(text:String){
    Box(
        modifier = Modifier
            .background(Color.White)
            .border(BorderStroke(0.25.dp, Color.Black))
            .fillMaxWidth()
    ) {
        Text(text, modifier = Modifier.padding(1.5.dp, 0.dp), fontSize = 14.sp)
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val centiseconds = (milliseconds / 10) % 100

    return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds)
}
