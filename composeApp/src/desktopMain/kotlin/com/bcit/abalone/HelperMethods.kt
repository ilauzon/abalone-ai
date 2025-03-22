package com.bcit.abalone

import com.bcit.abalone.model.Action
import com.bcit.abalone.model.MoveDirection
import org.jetbrains.skiko.currentNanoTime

fun isCellNeighbor(currentCell: Cell, targetCell: Cell): Boolean {
    val letterDiff = currentCell.letter - targetCell.letter
    val numberDiff = currentCell.number - targetCell.number
    return (letterDiff == 1 && numberDiff == 1) || (letterDiff == 0 && numberDiff == 1) || (letterDiff == -1 && numberDiff == 0)
            || (letterDiff == -1 && numberDiff == -1) || (letterDiff == 0 && numberDiff == -1) || (letterDiff == 1 && numberDiff == 0)
}

fun isThirdCell(currentCells: MutableList<Cell>, addCell: Cell): Boolean {
    val letterDiff = currentCells[0].letter - currentCells[1].letter
    val numberDiff = currentCells[0].number - currentCells[1].number
    return addCell.letter == currentCells[1].letter - letterDiff && addCell.number == currentCells[1].number - numberDiff
}

//fun formatTime(milliseconds: Long): String {
//    val totalSeconds = milliseconds / 1000
//    val minutes = totalSeconds / 60
//    val seconds = totalSeconds % 60
//    val centiseconds = (milliseconds / 10) % 100
//    return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds)
//}

fun validCell(board: List<List<Cell>>, letter: Char, number: Int): Cell?{
    // Check if the letter and number are within the bounds of the board
    return board.flatten().find { it.letter == letter && it.number == number }
}


fun oneMarbleMovePossibilities(currentCell: Cell, board: List<List<Cell>>): MutableList<Cell> {
    val possibilities = mutableListOf<Cell>()

    listOf(
        Cell(currentCell.letter, currentCell.number + 1),
        Cell(currentCell.letter, currentCell.number - 1),
        Cell(currentCell.letter + 1, currentCell.number),
        Cell(currentCell.letter - 1, currentCell.number),
        Cell(currentCell.letter + 1, currentCell.number + 1),
        Cell(currentCell.letter - 1, currentCell.number - 1)
    ).forEach { cell ->
        validCell(board, cell.letter, cell.number)?.let { possibilities.add(it) }
    }

    return possibilities
}

fun twoOrThreeMarbleMovePossibilities(currentCellList: MutableList<Cell>, board: List<List<Cell>>): MutableList<Cell> {
    val possibilities = mutableListOf<Cell>()
    if (currentCellList.size == 2 || currentCellList.size == 3) {
        val letterDiff = currentCellList[0].letter - currentCellList[1].letter
        val numberDiff = currentCellList[0].number - currentCellList[1].number

        val potentialCells = mutableListOf<Cell>()

        if (numberDiff == 0 && letterDiff != 0) {
            potentialCells.addAll(
                listOf(
                    Cell(currentCellList[0].letter + letterDiff, currentCellList[0].number), // inline
                    Cell(currentCellList.last().letter - letterDiff, currentCellList.last().number), // inline
                    Cell(currentCellList[0].letter, currentCellList[0].number - letterDiff),
                    Cell(currentCellList[0].letter + letterDiff, currentCellList[0].number + letterDiff),
                    Cell(currentCellList.last().letter - letterDiff, currentCellList.last().number - letterDiff),
                    Cell(currentCellList.last().letter, currentCellList.last().number + letterDiff)
                )
            )
        } else if (letterDiff != 0 && numberDiff != 0) {
            potentialCells.addAll(
                listOf(
                    Cell(currentCellList[0].letter + letterDiff, currentCellList[0].number + numberDiff),
                    Cell(currentCellList.last().letter - letterDiff, currentCellList.last().number - numberDiff),
                    Cell(currentCellList[0].letter + letterDiff, currentCellList[0].number),
                    Cell(currentCellList[0].letter, currentCellList[0].number + numberDiff),
                    Cell(currentCellList.last().letter - letterDiff, currentCellList.last().number),
                    Cell(currentCellList.last().letter, currentCellList.last().number - numberDiff)
                )
            )
        } else if (letterDiff == 0 && numberDiff != 0) {
            potentialCells.addAll(
                listOf(
                    Cell(currentCellList[0].letter, currentCellList[0].number + numberDiff),
                    Cell(currentCellList.last().letter, currentCellList.last().number - numberDiff),
                    Cell(currentCellList[0].letter - numberDiff, currentCellList[0].number),
                    Cell(currentCellList[0].letter + numberDiff, currentCellList[0].number + numberDiff),
                    Cell(currentCellList.last().letter - numberDiff, currentCellList.last().number - numberDiff),
                    Cell(currentCellList.last().letter + numberDiff, currentCellList.last().number)
                )
            )
        }

        // Add not-null cells from the board
        potentialCells.forEach { cell ->
            validCell(board, cell.letter, cell.number)?.let { possibilities.add(it) }
        }
    }
    return possibilities
}

fun outputState(board: List<List<Cell>>, currentPlayer: Piece): Pair<String,String> {
    val positions = board.flatten()
        .filter { it.piece == Piece.Black || it.piece == Piece.White }
        .joinToString(",") { "${it.letter}${it.number}${it.piece.name.first().lowercase()}" }
    val player = if (currentPlayer == Piece.Black) "b" else "w"
    return player to positions
}

fun getTargetCellFromAction(action: Action, board: List<List<Cell>>): Cell? {
    // Determine move direction offsets
    val (dLetter, dNumber) = when (action.direction) {
        MoveDirection.PosX -> 0 to 1
        MoveDirection.NegX -> 0 to -1
        MoveDirection.PosY -> -1 to 0
        MoveDirection.NegY -> 1 to 0
        MoveDirection.PosZ -> -1 to -1
        MoveDirection.NegZ -> 1 to 1
    }

    // Find the front-most marble based on direction
    val frontMarble = action.coordinates.maxByOrNull { coord ->
        val score = coord.letter.ordinal * 10 + coord.number.ordinal
        when (action.direction) {
            MoveDirection.PosX, MoveDirection.PosY, MoveDirection.PosZ -> -score
            else -> score
        }
    } ?: return null

    val targetLetterChar = frontMarble.letter.toString().first()
    val targetNumberInt = frontMarble.number.ordinal + dNumber
    val targetLetter = (targetLetterChar.code + dLetter).toChar()

    return board.flatten().find { it.letter == targetLetter && it.number == targetNumberInt }
}

