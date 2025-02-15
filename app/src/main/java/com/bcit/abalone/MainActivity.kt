package com.bcit.abalone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AbaloneGame()
            }
        }
    }
}

@Composable
fun AbaloneGame() {
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
            val board = createBoard()
            val columnLengths = listOf(5, 6, 7, 8, 9, 8, 7, 6, 5)

            Column(
                modifier = Modifier.size(350.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                board.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier
                            .padding(start = (9 - columnLengths[index]) * 2.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        row.forEach { (label, color) ->
                            Column(
                                modifier = Modifier
                                    .padding(1.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Canvas(
                                    modifier = Modifier
                                        .size(30.dp)
                                ) {
                                    // Draw outer black stroke
                                    drawCircle(
                                        color = Color.White,
                                        radius = size.minDimension / 2,
                                        center = center
                                    )
                                    drawCircle(
                                        color = Color.Black,
                                        radius = size.minDimension / 2 * 0.9f,
                                        center = center,
                                        style = Stroke(3f)
                                    )

                                    // Draw colored piece if it exists
                                    color?.let {
                                        drawCircle(
                                            color = it,
                                            radius = size.minDimension / 2 * 0.8f,
                                            center = center
                                        )
                                    }

                                    // Draw text label inside the circle
                                    withTransform({
                                        translate(center.x + 2f, center.y + 10f)
                                    }) {
                                        drawContext.canvas.nativeCanvas.drawText(
                                            label,
                                            0f,
                                            0f,
                                            android.graphics.Paint().apply {
                                                textSize = 30f
                                                textAlign = android.graphics.Paint.Align.CENTER
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // buttons
            Spacer(modifier = Modifier.height(5.dp))
            Row {

                Button(onClick = { /* Reset logic here */ }, modifier = Modifier.padding(1.dp)) {
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


