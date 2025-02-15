package com.bcit.abalone

import androidx.compose.ui.graphics.Color

val player1 = setOf("I5", "I6", "I7", "I8", "I9", "H4", "H5", "H6", "H7", "H8", "H9", "G5", "G6", "G7")
val player2 = setOf("A1", "A2", "A3", "A4", "A5", "B1","B2", "B3", "B4", "B5", "B6", "C3", "C4", "C5")

fun createBoard(): List<List<Pair<String, Color?>>> {
    val row = 'I' downTo 'A'
    val columnLength = listOf(5, 6, 7, 8, 9, 8, 7, 6, 5)
    val board = mutableListOf<List<Pair<String, Color?>>>()
    for ((rowIndex, r) in row.withIndex()) {
        val currentRow = mutableListOf<Pair<String, Color?>>()
        val startColumn = when(r) {
            'I' -> 5
            'H' -> 4
            'G' -> 3
            'F' -> 2
            else -> 1
        }
        for(c in startColumn until(startColumn+columnLength[rowIndex])){
            val label = "$r$c"
            val color = when{
                label in player1 -> Color.Red
                label in player2 -> Color.Blue
                else -> null

            }
            currentRow.add(label to color)
        }
        board.add(currentRow)
    }
    return board
}


