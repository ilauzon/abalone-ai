package com.bcit.abalone

data class Cell(val letter: Char, val number: Int, var piece:Piece = Piece.Empty)

fun createBoard(): List<List<Cell>> {
    val letter = 'I' downTo 'A'
    val columnLength = listOf(5, 6, 7, 8, 9, 8, 7, 6, 5)
    val board = mutableListOf<List<Cell>>()

    for ((index, c) in letter.withIndex()) {
        val cells = mutableListOf<Cell>()
        val start = when(c) {
            'I' -> 5
            'H' -> 4
            'G' -> 3
            'F' -> 2
            else -> 1
        }
        for (i in start until (start + columnLength[index])) {
            val piece = when (c) {
                'I', 'H' -> Piece.Blue
                'G' -> if (i in 5..7) Piece.Blue else Piece.Empty
                'A', 'B' -> Piece.Red
                'C' -> if (i in 3..5) Piece.Red else Piece.Empty
                else -> Piece.Empty
            }
            val cell = Cell(c, i, piece)
            cells.add(cell)
        }
        board.add(cells)
    }
    return board
}

