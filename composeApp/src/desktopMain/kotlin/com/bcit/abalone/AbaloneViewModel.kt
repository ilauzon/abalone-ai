package com.bcit.abalone

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel


class AbaloneViewModel : ViewModel() {
    var boardState = mutableStateOf(createBoard())
    var currentPlayer = mutableStateOf(Piece.Blue)
    var blueMoveNumber = mutableStateOf(0)
    var redMoveNumber = mutableStateOf(0)
    var bluePiecesTaken = mutableStateOf(0)
    var redPiecesTaken = mutableStateOf(0)
    var moveStartTime = mutableStateOf(System.currentTimeMillis())
    var moveDuration = mutableStateOf(0L)

    val totalTimePerPlayer = 30L
    var blueTimeRemaining = mutableStateOf(totalTimePerPlayer)
    var redTimeRemaining = mutableStateOf(totalTimePerPlayer)

    var p1TimeLimit by mutableStateOf(60f)
    var p2TimeLimit by mutableStateOf(60f)


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

        moveDuration.value = System.currentTimeMillis() - moveStartTime.value
        if (currentPlayer.value == Piece.Blue) {
            blueTimeRemaining.value -= moveDuration.value
        } else {
            redTimeRemaining.value -= moveDuration.value
        }

        // one marble can only move to empty cell
        if (targetCell.piece == Piece.Empty && selectedCells.isNotEmpty()) {
            if (selectedCells.size == 1 && isCellNeighbor(selectedCells[0], targetCell)) {
                targetCell.piece = selectedCells[0].piece
                selectedCells[0].piece = Piece.Empty
                if (currentPlayer.value == Piece.Blue) blueMoveNumber.value++ else redMoveNumber.value++
                selectedCells.clear()
                switchPlayer()

                // the following is for two or three marbles
            } else if ((selectedCells.size == 2 || selectedCells.size == 3)) {
                val possibleMoveList =
                    twoOrThreeMarbleMovePossibilities(selectedCells, boardState.value)
                if (!possibleMoveList.contains(targetCell)) {
                    return
                } else {
                    var letterDiff = 0
                    var numberDiff = 0
                    var moveOrder: List<Cell> = emptyList()

                    if (isCellNeighbor(selectedCells[0], targetCell)) {
                        letterDiff = targetCell.letter - selectedCells[0].letter
                        numberDiff = targetCell.number - selectedCells[0].number
                        moveOrder = selectedCells // Move in normal order
                    } else if (isCellNeighbor(selectedCells.last(), targetCell)) {
                        letterDiff = targetCell.letter - selectedCells.last().letter
                        numberDiff = targetCell.number - selectedCells.last().number
                        moveOrder = selectedCells.reversed() // Move in reverse order
                    }

                    val newPositions = mutableListOf<Pair<Cell, Cell>>() // Store old and new cell mappings
                    var isValidMove = true

                    // Step 1: Temporarily mark moving pieces as "in motion"
                    val movingPieces = moveOrder.map { it to it.piece }
                    for ((cell, _) in movingPieces) {
                        cell.piece = Piece.Empty // Temporarily set it empty
                    }

                    // Step 2: Check if all moves are valid
                    for ((cell, originalPiece) in movingPieces) {
                        val newLetter = cell.letter + letterDiff
                        val newNumber = cell.number + numberDiff

                        val newCell = boardState.value.flatten()
                            .find { it.letter == newLetter && it.number == newNumber }

                        if (newCell == null || (newCell.piece != Piece.Empty && newCell !in movingPieces.map { it.first })) {
                            isValidMove = false // If any new position is not empty and not in motion, move is invalid
                            break
                        } else {
                            newPositions.add(cell to newCell)
                        }
                    }
                    // Step 3: If valid, apply move
                    if (isValidMove) {
                        for ((oldCell, newCell) in newPositions.reversed()) { // Process from last to first
                            newCell.piece = movingPieces.find { it.first == oldCell }?.second ?: Piece.Empty
                        }
                    } else {
                        // Restore pieces if move is invalid
                        for ((cell, originalPiece) in movingPieces) {
                            cell.piece = originalPiece
                        }
                    }

                    if (isValidMove) {
                        if (currentPlayer.value == Piece.Blue) blueMoveNumber.value++ else redMoveNumber.value++
                        selectedCells.clear()
                        switchPlayer()
                    }
                }
            }


            // target cell is opponent
        } else if (targetCell.piece != Piece.Empty && targetCell.piece != currentPlayer.value) {
            val targetLetter = targetCell.letter
            val targetNumber = targetCell.number
            val targetPiece = targetCell.piece

            var letterDiff = 0
            var numberDiff = 0
            var moveOrder: List<Cell> = emptyList()

            if (isCellNeighbor(selectedCells[0], targetCell)) {
                letterDiff = targetLetter - selectedCells[0].letter
                numberDiff = targetNumber - selectedCells[0].number
                moveOrder = selectedCells // Move in normal order
            } else if (isCellNeighbor(selectedCells.last(), targetCell)) {
                letterDiff = targetLetter - selectedCells.last().letter
                numberDiff = targetNumber - selectedCells.last().number
                moveOrder = selectedCells.reversed() // Move in reverse order
            }

            val newPositions = mutableListOf<Pair<Cell, Cell>>() // Store old and new cell mapping


            val nextCell = boardState.value.flatten().find { it.letter == targetLetter + letterDiff && it.number == targetNumber + numberDiff }

            // Push Logic for Two Marbles
            if (selectedCells.size == 2) {
                if (nextCell == null) {
                    // Opponent pushed out of board
                    targetCell.piece = Piece.Empty
                    moveOrder.forEach { cell ->
                        val updateCell = boardState.value.flatten()
                            .find { it.letter == (cell.letter + letterDiff) && it.number == (cell.number + numberDiff) }
                        updateCell?.piece = currentPlayer.value
                        cell.piece = Piece.Empty
                    }
                    if (currentPlayer.value == Piece.Blue) bluePiecesTaken.value++ else redPiecesTaken.value++
                    switchPlayer()

                } else if (nextCell.piece == Piece.Empty) {
                    // Push opponent to the empty space
                    newPositions.add(targetCell to nextCell)
                    moveOrder.forEach { cell ->
                        val updateCell = boardState.value.flatten()
                            .find { it.letter == (cell.letter + letterDiff) && it.number == (cell.number + numberDiff) }
                        updateCell?.let { newPositions.add(cell to it) }
                    }
                }
            }
            // Push Logic for Three Marbles
            else if (selectedCells.size == 3) {
                if (nextCell == null) {
                    // Opponent pushed out of board
                    targetCell.piece = Piece.Empty
                    moveOrder.forEach { cell ->
                        val updateCell = boardState.value.flatten()
                            .find { it.letter == (cell.letter + letterDiff) && it.number == (cell.number + numberDiff) }
                        updateCell?.piece = currentPlayer.value
                        cell.piece = Piece.Empty
                    }
                    if (currentPlayer.value == Piece.Blue) redPiecesTaken.value++ else bluePiecesTaken.value++
                    switchPlayer()
                } else if (nextCell.piece == Piece.Empty) {
                    // Push opponent to the empty space
                    newPositions.add(targetCell to nextCell)
                    moveOrder.forEach { cell ->
                        val updateCell = boardState.value.flatten()
                            .find { it.letter == (cell.letter + letterDiff) && it.number == (cell.number + numberDiff) }
                        updateCell?.let { newPositions.add(cell to it) }
                    }
                } else if (nextCell.piece == targetPiece) {
                    val nextnextCell = boardState.value.flatten().find { it.letter == targetLetter + 2 * letterDiff && it.number == targetNumber + 2 * numberDiff }
                    if (nextnextCell == null) {
                        // Two opponent marbles pushed out
                        newPositions.add(targetCell to nextCell)
                        moveOrder.forEach { cell ->
                            val updateCell = boardState.value.flatten()
                                .find { it.letter == (cell.letter + letterDiff) && it.number == (cell.number + numberDiff) }
                            updateCell?.let { newPositions.add(cell to it) }
                        }

                        if (currentPlayer.value == Piece.Blue) redPiecesTaken.value++ else bluePiecesTaken.value++
                        switchPlayer()
                    }
                }

            }


            // Apply the moves
            if (newPositions.isNotEmpty()) {
                for ((oldCell, newCell) in newPositions.reversed()) {
                    newCell.piece = oldCell.piece
                }
                for ((oldCell, _) in newPositions) {
                    oldCell.piece = Piece.Empty
                }

                if (currentPlayer.value == Piece.Blue) blueMoveNumber.value++ else redMoveNumber.value++
                selectedCells.clear()
                switchPlayer()
            }
        }
    }

    fun switchPlayer() {
        currentPlayer.value = if (currentPlayer.value == Piece.Blue) Piece.Red else Piece.Blue
        moveStartTime.value = System.currentTimeMillis()
    }

    fun updateP1TimeLimit(p1TimePerTurn : Float){
        p1TimeLimit = p1TimePerTurn
    }
    fun updateP2TimeLimit(p2TimePerTurn : Float) {
        p2TimeLimit = p2TimePerTurn
    }

}

