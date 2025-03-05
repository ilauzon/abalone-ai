package com.bcit.abalone

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * The viewmodel for the game board.
 */
class AbaloneViewModel : ViewModel() {
    var boardState = mutableStateOf(createBoard())
    var currentPlayer = mutableStateOf(Piece.Blue)
    var blueMoveNumber = mutableStateOf(0)
    var redMoveNumber = mutableStateOf(0)
    var bluePiecesTaken = mutableStateOf(0)
    var redPiecesTaken = mutableStateOf(0)
    var moveStartTime = mutableStateOf(System.currentTimeMillis())
    var moveDuration = mutableStateOf(0L)

    val totalTimePerPlayer = 30*60*1000L
    var blueTimeRemaining = mutableStateOf(totalTimePerPlayer)
    var redTimeRemaining = mutableStateOf(totalTimePerPlayer)

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

    /**
     * Move the marbles in the selected cells to a new set of cells.
     *
     * @param selectedCells the cells that contain the marbles to move.
     * @param targetCell the cell to move the first marble in the list to. The direction of
     * motion is replicated by the rest of the marbles in the list.
     */
    fun moveMarbles(selectedCells: MutableList<Cell>, targetCell: Cell) {

        moveDuration.value = System.currentTimeMillis() - moveStartTime.value
        if (currentPlayer.value == Piece.Blue) {
            blueTimeRemaining.value -= moveDuration.value
        } else {
            redTimeRemaining.value -= moveDuration.value
        }
        // move to empty cell
        if (targetCell.piece == Piece.Empty && selectedCells.isNotEmpty()) {
            if (selectedCells.size == 1 && isCellNeighbor(selectedCells[0], targetCell)) {
                targetCell.piece = selectedCells[0].piece
                selectedCells[0].piece = Piece.Empty
                if (currentPlayer.value == Piece.Blue) blueMoveNumber.value++ else redMoveNumber.value++
                selectedCells.clear()
                switchPlayer()
            } else if ((selectedCells.size == 2 || selectedCells.size == 3) && isCellNeighbor(
                    selectedCells[0],
                    targetCell
                )
            ) {

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
                    val newCell = boardState.value.flatten()
                        .find { it.letter == newLetter && it.number == newNumber }

                    // Ensure valid movement
                    if (newCell != null && !visitedPositions.contains(newLetter to newNumber) && newCell.piece == Piece.Empty) {
                        newCell.piece = cell.piece
                        visitedPositions.add(newLetter to newNumber)
                        cell.piece = Piece.Empty
                    } else {
                        // Invalid move, restore state
                        visitedPositions.clear()
                    }
                }
                if (currentPlayer.value == Piece.Blue) blueMoveNumber.value++ else redMoveNumber.value++
                selectedCells.clear()
                switchPlayer()
            }


            // target cell is opponent
        } else if (targetCell.piece != Piece.Empty && targetCell.piece != currentPlayer.value) {
            val targetLetter = targetCell.letter
            val targetNumber = targetCell.number
            val targetPiece = targetCell.piece

            var letterDiff = 0
            var numberDiff = 0
            if (selectedCells.size > 0) {
                letterDiff = targetLetter - selectedCells[0].letter
                numberDiff = targetNumber - selectedCells[0].number
            }

            if (selectedCells.size == 2) {
                val nextCell = boardState.value.flatten()
                    .find { it.letter == targetLetter + letterDiff && it.number == targetNumber + numberDiff }
                if (nextCell == null) {
                    selectedCells.forEach { cell ->
                        val updateCell = boardState.value.flatten()
                            .find { it.letter == (cell.letter + letterDiff) && it.number == (cell.number + numberDiff) }
                        updateCell?.piece = currentPlayer.value
                        cell.piece = Piece.Empty
                    }
                    // push out of board
                    if (currentPlayer.value == Piece.Blue) bluePiecesTaken.value++ else redPiecesTaken.value++
                    switchPlayer()
                } else if (nextCell.piece == Piece.Empty) {
                    selectedCells.forEach { cell ->
                        val updateCell = boardState.value.flatten()
                            .find { it.letter == (cell.letter + letterDiff) && it.number == (cell.number + numberDiff) }
                        updateCell?.piece = currentPlayer.value
                    }
                    nextCell.piece = targetPiece
                    selectedCells[1].piece = Piece.Empty
                    switchPlayer()
                }
            } else if (selectedCells.size == 3) {
                val nextCell = boardState.value.flatten()
                    .find { it.letter == targetLetter + letterDiff && it.number == targetNumber + numberDiff }
                if (nextCell == null) {
                    selectedCells.forEach { cell ->
                        val updateCell = boardState.value.flatten()
                            .find { it.letter == (cell.letter + letterDiff) && it.number == (cell.number + numberDiff) }
                        updateCell?.piece = currentPlayer.value
                        cell.piece = Piece.Empty
                    }
                    // push out of board
                    if (currentPlayer.value == Piece.Blue) redPiecesTaken.value++ else bluePiecesTaken.value++
                    switchPlayer()
                } else if (nextCell.piece == Piece.Empty) {
                    selectedCells.forEach { cell ->
                        val updateCell = boardState.value.flatten()
                            .find { it.letter == (cell.letter + letterDiff) && it.number == (cell.number + numberDiff) }
                        updateCell?.piece = currentPlayer.value
                    }
                    nextCell.piece = targetPiece
                    selectedCells[2].piece = Piece.Empty
                    switchPlayer()
                } else if (nextCell.piece == targetPiece) {
                    val nextnextCell = boardState.value.flatten()
                        .find { it.letter == targetLetter + 2 * letterDiff && it.number == targetNumber + 2 * numberDiff }
                    if (nextnextCell == null) {
                        targetCell.piece = currentPlayer.value
                        selectedCells[2].piece = Piece.Empty
                        println("Opponent out")
                        switchPlayer()
                    }
                }
            }
        }

    }

    /**
     * Ends the current player's turn.
     */
    fun switchPlayer() {
        currentPlayer.value = if (currentPlayer.value == Piece.Blue) Piece.Red else Piece.Blue
        moveStartTime.value = System.currentTimeMillis()
    }
}

/**
 * Determines if the currentCell and targetCell are immediate neighbours.
 *
 * @param currentCell a cell.
 * @param targetCell a cell.
 * @return true if the two cells are immediate neighbours, false if otherwise.
 */
private fun isCellNeighbor(currentCell: Cell, targetCell: Cell): Boolean {
    val letterDiff = kotlin.math.abs(currentCell.letter - targetCell.letter)
    val numberDiff = kotlin.math.abs(currentCell.number - targetCell.number)
    return (letterDiff == 1 && numberDiff == 0) || (letterDiff == 0 && numberDiff == 1) || (letterDiff == 1 && numberDiff == 1)
}

private fun isThirdCell(currentCells: MutableList<Cell>, addCell: Cell): Boolean {
    val letterDiff = currentCells[0].letter - currentCells[1].letter
    val numberDiff = currentCells[0].number - currentCells[1].number
    return addCell.letter == currentCells[1].letter - letterDiff && addCell.number == currentCells[1].number - numberDiff
}
