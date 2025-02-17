package com.bcit.abalone

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AbaloneViewModel : ViewModel() {
    var boardState = mutableStateOf(createBoard())
    var currentPlayer = mutableStateOf(Piece.Blue)

    fun selectMarbles(selectedCells: MutableList<Cell>, cell: Cell) {
        if (selectedCells.isEmpty()) {
            if (cell.piece != Piece.Empty && cell.piece == currentPlayer.value) {
                selectedCells.add(cell)
            }
        } else {
            if (selectedCells.contains(cell)) {
                selectedCells.remove(cell)
            } else {
                if (selectedCells.size == 1) {
                    if (isCellNeighbor(
                            selectedCells.last(),
                            cell
                        ) && cell.piece != Piece.Empty && cell.piece == currentPlayer.value
                    ) {
                        selectedCells.add(cell)
                    } else if (!isCellNeighbor(selectedCells.last(), cell)) {
                        selectedCells.clear()
                        selectedCells.add(cell)
                    }
                } else if (selectedCells.size == 2) {
                    if (isThirdCell(
                            selectedCells,
                            cell
                        ) && cell.piece != Piece.Empty && cell.piece == currentPlayer.value
                    ) {
                        selectedCells.add(cell)
                    } else if (!isThirdCell(selectedCells, cell)) {
                        selectedCells.clear()
                        selectedCells.add(cell)
                    }
                }
            }
        }
    }

    fun moveMarbles(selectedCells: MutableList<Cell>, targetCell: Cell) {
        if (targetCell.piece == Piece.Empty && selectedCells.isNotEmpty()) {
            if (selectedCells.size == 1 && isCellNeighbor(selectedCells[0], targetCell)) {
                targetCell.piece = selectedCells[0].piece
                selectedCells[0].piece = Piece.Empty
                selectedCells.clear()
                switchPlayer()
            } else if ((selectedCells.size == 2 || selectedCells.size == 3) && isCellNeighbor(selectedCells[0], targetCell)){

                val letterDiff = targetCell.letter - selectedCells[0].letter
                val numberDiff = targetCell.number - selectedCells[0].number

                // Ensure each marble moves correctly without merging into one
                val visitedPositions = mutableSetOf<Pair<Char, Int>>()

                // Move each marble correctly
                for (i in selectedCells.indices) {
                    val cell = selectedCells[i]
                    val newLetter = cell.letter + letterDiff
                    val newNumber = cell.number + numberDiff

                    // Calculate next position in the line
                    val newCell = boardState.value.flatten().find { it.letter == newLetter && it.number == newNumber }

                    // Ensure valid movement
                    if (newCell != null && !visitedPositions.contains(newLetter to newNumber) && newCell.piece == Piece.Empty) {
                        newCell.piece = cell.piece
                        visitedPositions.add(newLetter to newNumber)
                        cell.piece = Piece.Empty
                    } else {
                        // Invalid move, restore state
                        visitedPositions.clear()
                        return
                    }
                }

                selectedCells.clear()
                switchPlayer()
            }
        }
    }


    fun switchPlayer() {
        currentPlayer.value = if (currentPlayer.value == Piece.Blue) Piece.Red else Piece.Blue
    }
}


fun isCellNeighbor(currentCell: Cell, targetCell: Cell): Boolean {
    val letterDiff = kotlin.math.abs(currentCell.letter - targetCell.letter)
    val numberDiff = kotlin.math.abs(currentCell.number - targetCell.number)
    return (letterDiff == 1 && numberDiff == 0) || (letterDiff == 0 && numberDiff == 1) || (letterDiff == 1 && numberDiff == 1)
}

fun isThirdCell(currentCells: MutableList<Cell>, addCell: Cell): Boolean {
    val letterDiff = currentCells[0].letter - currentCells[1].letter
    val numberDiff = currentCells[0].number - currentCells[1].number
    return addCell.letter == currentCells[1].letter - letterDiff && addCell.number == currentCells[1].number - numberDiff
}