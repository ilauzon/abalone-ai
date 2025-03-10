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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

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
    val isPaused = viewModel.isPaused

    val selectedLayout = viewModel.selectedLayout

    val blueTimePerTurn = viewModel.p1TimeLimit
    val redTimePerTurn = viewModel.p2TimeLimit
    var timeJob = viewModel.timerJob

    val moveLimit = viewModel.moveLimit
    val durationPerMove = viewModel.moveDuration.value


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
            Text("P1 Score")
            Box(

            ) {
                Text("$bluePiecesTaken", fontSize = 90.sp, color = Color.Blue)
            }
            Spacer(modifier = Modifier.height(100.dp))
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
                        TableCell("P1")
                        TableCell("$bluePiecesTaken")
                        TableCell("$blueMoveNumber/${moveLimit.toInt()}")
                        TableCell(formatTime(blueTimeRemaining))
                        if (currentPlayer == Piece.Black) {
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
                        TableCell("P2")
                        TableCell("$redPiecesTaken")
                        TableCell("$redMoveNumber/${moveLimit.toInt()}")
                        TableCell(formatTime(redTimeRemaining))
                        if (currentPlayer == Piece.White) {
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
            Spacer(modifier = Modifier.height(100.dp))
            Text("P2 Score")
            Box(

            ) {
                Text("$redPiecesTaken", fontSize = 90.sp, color = Color.Red)
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
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                if (currentPlayer == Piece.Black) {
                    Text("P1", fontSize = 50.sp, fontWeight = FontWeight.Bold)
                    playerTimer(viewModel.p1TimeLimit, isPaused, viewModel)
                } else {
                    Text("P2", fontSize = 50.sp, fontWeight = FontWeight.Bold)
                    playerTimer(viewModel.p2TimeLimit, isPaused, viewModel)
                }
                Spacer(modifier = Modifier.height(40.dp))
                // Below is the board
                board.forEachIndexed { _, row ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(start = (9 - row.size) * 4.dp)
                    ) {
                        row.forEach { cell ->
                            val isSelected = selectedCells.contains(cell)
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
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
                                            Piece.Black -> Color.Blue
                                            Piece.White -> Color.Red
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
            Spacer(modifier = Modifier.height(20.dp))
            Row (modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)){

                Button(onClick = { viewModel.resetGame() }, modifier = Modifier.padding(1.dp)) {
                    Text("Start / Reset")
                }
                Button(onClick = { viewModel.pauseOrResumeGame() }, modifier = Modifier.padding(1.dp)) {
                    Text( if(isPaused.value) "Resume" else "Pause / Resume")
                }
                Button(onClick = {viewModel.undoLastMove() }, modifier = Modifier.padding(1.dp)) {
                    Text("Undo")
                }
            }
        }

        // Right panel for moves table.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1.5f)
                .background(Color.LightGray)
                .fillMaxHeight()
        ) {
            Text("Previous Moves", modifier = Modifier.padding(15.dp).weight(0.5f).padding(top = 20.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier
                .padding(5.dp)
                .background(Color.White)

                .fillMaxWidth()
                .weight(3f)
                ) {
                    Column{
                        Box {
                            LazyColumn {
                                items(viewModel.moveHistory.size) {
                                    pathCard(viewModel.moveHistory[it])
                                }
                            }
                        }
                    }
                }


            Column(modifier = Modifier.fillMaxWidth().weight(0.5f)
            ){
                val p1Time = viewModel.moveHistory
                    .filter { it.previousPlayer == Piece.Black }
                    .sumOf { it.moveDuration }

                val p2Time = viewModel.moveHistory
                    .filter { it.previousPlayer == Piece.White }
                    .sumOf { it.moveDuration }
                Text("P1 Total time spent   ${p1Time / 1000}s", modifier = Modifier.padding(top = 10.dp),fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("P2 Total time spent   ${p2Time / 1000}s", modifier = Modifier.padding(top = 10.dp),fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
fun countDownFlow(startTime: Float, isPausedFlow: StateFlow<Boolean>) = flow {
    var time = startTime
    while(time >= 0) {
        if (!isPausedFlow.value) {
            emit(time)
            delay(1000L)
            time--
        } else {
            delay(1000L)
        }
    }
}

@Composable
fun playerTimer(timePerTurn: Float, isPausedFlow: StateFlow<Boolean>, viewModel: AbaloneViewModel){
    var timeLeft by remember { mutableStateOf(timePerTurn) }

    LaunchedEffect(timePerTurn, isPausedFlow, viewModel.currentPlayer.value) {
        viewModel.timerJob?.cancel()
        viewModel.timerJob = viewModel.viewModelScope.launch {
            countDownFlow(timePerTurn, isPausedFlow).collectLatest { newTime ->
                timeLeft = newTime
            }
        }
    }

    Text(
        text = "Time left: $timeLeft s",
        fontSize = 24.sp,
    )
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val centiseconds = (milliseconds / 10) % 100

    return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds)
}

@Composable
fun pathCard(moveRecord: AbaloneViewModel.MoveRecord) {
    Card(
        border = BorderStroke(width = 1.dp, color = if( moveRecord.previousPlayer == Piece.White ) Color.Red else Color.Blue),
        modifier = Modifier.fillMaxWidth().padding(all=10.dp)
    ){
        Row(horizontalArrangement = Arrangement.SpaceEvenly){
            Text(
                text = if (moveRecord.previousPlayer == Piece.White)"Red" else "Blue"
            )
            Text(text = moveRecord.movePath)
            Text(text = "${moveRecord.moveDuration / 1000}s")
        }
    }
}
