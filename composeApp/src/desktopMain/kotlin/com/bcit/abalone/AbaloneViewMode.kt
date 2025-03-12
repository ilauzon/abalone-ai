package com.bcit.abalone

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

//  In this class, blue related variable is P1, red related variable is P2
class AbaloneViewModel : ViewModel() {
    var selectedLayout by mutableStateOf("Standard")

    var boardState = mutableStateOf(createBoard(selectedLayout))
    var currentPlayer = mutableStateOf(Piece.Black)
    var blueMoveNumber = mutableStateOf(0)
    var redMoveNumber = mutableStateOf(0)
    var bluePiecesTaken = mutableStateOf(0)
    var redPiecesTaken = mutableStateOf(0)
    var moveStartTime = mutableStateOf(System.currentTimeMillis())
    var moveDuration = mutableStateOf(0L)

    val totalTimePerPlayer = 30 * 60 * 1000L
    var blueTimeRemaining = mutableStateOf(totalTimePerPlayer)
    var redTimeRemaining = mutableStateOf(totalTimePerPlayer)

    var isPaused = MutableStateFlow(false)

    var p1TimeLimit by mutableStateOf(60f)
    var p1MaxTimePerTurn by mutableStateOf(60f)
    var p2TimeLimit by mutableStateOf(60f)
    var p2MaxTimePerTurn by mutableStateOf(60f)
    var timerJob: Job? = null


    var selectedMode by mutableStateOf("Vs. Human")
    var player1Color by mutableStateOf("Black")
    var moveLimit by mutableStateOf(50f)

    var pausedTimeRemaining = 0L
    var deepCopiedBoard = boardState.value.map { row -> row.map { it.copy() } }
    val moveHistory = mutableStateListOf<MoveRecord>()


    fun resetGame() {
        selectedLayout = selectedLayout
        boardState.value = createBoard(selectedLayout)
        currentPlayer.value = Piece.Black
        blueMoveNumber.value = 0
        redMoveNumber.value = 0
        bluePiecesTaken.value = 0
        redPiecesTaken.value = 0
        blueTimeRemaining.value = totalTimePerPlayer
        redTimeRemaining.value = totalTimePerPlayer
        moveStartTime.value = System.currentTimeMillis()
        isPaused.value = false
        p1TimeLimit = p1MaxTimePerTurn
        println("p1TimeLimit after reset: $p1TimeLimit")
        p2TimeLimit = p2MaxTimePerTurn
        println("p2TimeLimit after reset: $p2TimeLimit")
        timerJob?.cancel()
        moveHistory.clear()
//        println(boardState)
    }

    fun pauseOrResumeGame() {
        val currentTime = System.currentTimeMillis()

        if (isPaused.value) {
            // Resume the game
            moveStartTime.value = currentTime
            if(currentPlayer.value == Piece.Black) {
                blueTimeRemaining.value = pausedTimeRemaining
            } else {
                redTimeRemaining.value = pausedTimeRemaining
            }

        } else {
            // Pause the game
            pausedTimeRemaining = if(currentPlayer.value == Piece.Black) {
                blueTimeRemaining.value - (currentTime - moveStartTime.value)
            } else {
                redTimeRemaining.value - (currentTime - moveStartTime.value)
            }
        }
        isPaused.value = !isPaused.value
    }

    data class MoveRecord(
        val previousState: List<List<Cell>>,
        val previousPlayer: Piece,
        val blueMoveNumber: Int,
        val redMoveNumber: Int,
        val bluePiecesTaken: Int,
        val redPiecesTaken: Int,
        val blueTimeRemaining: Long,
        val redTimeRemaining: Long,
        val movePath: String,
        val moveDuration: Long
    )

    fun saveGameState(movePath: String, moveDuration: Long) {

        moveHistory.add(
            MoveRecord(
                previousState = deepCopiedBoard.map { row -> row.map { it.copy() } },
                currentPlayer.value,
                blueMoveNumber.value,
                redMoveNumber.value,
                bluePiecesTaken.value,
                redPiecesTaken.value,
                blueTimeRemaining.value,
                redTimeRemaining.value,
                movePath,
                moveDuration
            )
        )
    }


    fun undoLastMove() {
        if (moveHistory.isNotEmpty()) {
            val lastMove = moveHistory.removeLast()
            deepCopiedBoard = lastMove.previousState.map { row -> row.map { it.copy() } }
            boardState.value = deepCopiedBoard

            currentPlayer.value = lastMove.previousPlayer
            blueMoveNumber.value = lastMove.blueMoveNumber
            redMoveNumber.value = lastMove.redMoveNumber
            bluePiecesTaken.value = lastMove.bluePiecesTaken
            redPiecesTaken.value =lastMove.redPiecesTaken
            blueTimeRemaining.value = lastMove.blueTimeRemaining
            redTimeRemaining.value = lastMove.redTimeRemaining

        }
    }

    /** this is for selecting marbles, including choose one or two or three.
        when choosing the second one, it will check if it is a neighbor cell. if not, it will clear the list.
        when choosing the third one, it will check if the third one is on the line and the three cells are in order. if not, it will clear the list.
     */
    fun selectMarbles(selectedCells: MutableList<Cell>, cell: Cell) {
        if (cell.piece != currentPlayer.value) {
            return
        }
        else if(isPaused.value) {return}
        else {
            when (selectedCells.size) {
                0 -> selectedCells.add(cell)
                1 -> handleSelection(
                    selectedCells,
                    cell
                ) { cells, newCell -> isCellNeighbor(cells.last(), newCell) }

                2 -> handleSelection(selectedCells, cell) { cells, newCell ->
                    isThirdCell(
                        cells,
                        newCell
                    )
                }

                else -> selectedCells.clear()
            }
        }
    }

    private fun handleSelection(selectedCells: MutableList<Cell>, cell: Cell, validation: (MutableList<Cell>, Cell) -> Boolean) {
        if (validation(selectedCells, cell) && cell.piece == currentPlayer.value) {
            selectedCells.add(cell)
        } else {
            selectedCells.clear()
            selectedCells.add(cell)
        }
    }

    /**  Move marbles method includes move one piece to empty cell; and two or three
        pieces to empty cell and push opponent piece.
     */
    fun moveMarbles(selectedCells: MutableList<Cell>, targetCell: Cell) {
        if (selectedCells.isEmpty()) {
            return
        }

        moveDuration.value = System.currentTimeMillis() - moveStartTime.value
        if (currentPlayer.value == Piece.Black) {
            blueTimeRemaining.value -= moveDuration.value
        } else {
            redTimeRemaining.value -= moveDuration.value
        }


        val (letterDiff, numberDiff, moveOrder) = determineMoveDirection(selectedCells, targetCell) ?: return

        if (targetCell.piece == Piece.Empty) {
            if (selectedCells.size == 1 && isCellNeighbor(selectedCells[0], targetCell)) {
                val movedPiece = selectedCells[0].piece
                val startPos = "${selectedCells[0].letter}${selectedCells[0].number}${movedPiece.name[0]}"
                val endPos = "${targetCell.letter}${targetCell.number}${movedPiece.name[0]}"
                targetCell.piece = movedPiece
                selectedCells[0].piece = Piece.Empty
                val movePath = "[$startPos] -> [$endPos]"
                saveGameState(movePath, moveDuration.value)
                incrementMoveCount()
                selectedCells.clear()
                switchPlayer()
            } else if (selectedCells.size in 2..3) {
                val possibleMoveList = twoOrThreeMarbleMovePossibilities(selectedCells, boardState.value)
                if (targetCell !in possibleMoveList) return
                performMove(moveOrder, letterDiff, numberDiff)
            }
        } else if (targetCell.piece != currentPlayer.value) {
            handlePushMove(selectedCells, targetCell, letterDiff, numberDiff, moveOrder)
        }
    }

    // This helper method check if the target cell is a neighbor cell of the first selected cell or the last selected cell.
    private fun determineMoveDirection(selectedCells: MutableList<Cell>, targetCell: Cell): Triple<Int, Int, List<Cell>>? {
        if (selectedCells.isEmpty()) { return null }
        val letterDiff: Int
        val numberDiff: Int
        val moveOrder: List<Cell>

        return if (isCellNeighbor(selectedCells[0], targetCell)) {
            letterDiff = targetCell.letter - selectedCells[0].letter
            numberDiff = targetCell.number - selectedCells[0].number
            moveOrder = selectedCells
            Triple(letterDiff, numberDiff, moveOrder)
        } else if (isCellNeighbor(selectedCells.last(), targetCell)) {
            letterDiff = targetCell.letter - selectedCells.last().letter
            numberDiff = targetCell.number - selectedCells.last().number
            moveOrder = selectedCells.reversed()
            Triple(letterDiff, numberDiff, moveOrder)
        } else {
            null
        }
    }

    // This helper method moves the selected pieces to the target cells.
    private fun performMove(moveOrder: List<Cell>, letterDiff: Int, numberDiff: Int) {
        val movingPieces = moveOrder.map { it to it.piece }
        val startPositions = moveOrder.map { "${it.letter}${it.number}${it.piece.name[0]}" }
        movingPieces.forEach { (cell, _) -> cell.piece = Piece.Empty }

        val newPositions = mutableListOf<Pair<Cell, Cell>>()
        for ((cell, originalPiece) in movingPieces) {
            val newCell = boardState.value.flatten().find {
                it.letter == cell.letter + letterDiff && it.number == cell.number + numberDiff
            }

            if (newCell == null || (newCell.piece != Piece.Empty && newCell !in movingPieces.map { it.first })) {
                movingPieces.forEach { (oldCell, piece) -> oldCell.piece = piece }
                return
            } else {
                newPositions.add(cell to newCell)
            }
        }

        newPositions.reversed().forEach { (oldCell, newCell) ->
            newCell.piece = movingPieces.find { it.first == oldCell }?.second ?: Piece.Empty
        }

        val endPositions = newPositions.map { "${it.second.letter}${it.second.number}${it.second.piece.name[0]}" }
        val movePath = "[${startPositions.joinToString(", ")}] -> [${endPositions.joinToString(", ")}]"

        saveGameState(movePath, moveDuration.value)
        incrementMoveCount()
        switchPlayer()

    }

    // this method handles pushing opponent piece.
    private fun handlePushMove(selectedCells: MutableList<Cell>, targetCell: Cell, letterDiff: Int, numberDiff: Int, moveOrder: List<Cell>) {
        val nextCell = boardState.value.flatten().find { it.letter == targetCell.letter + letterDiff && it.number == targetCell.number + numberDiff }
        val nextnextCell = boardState.value.flatten().find { it.letter == targetCell.letter + 2 * letterDiff && it.number == targetCell.number + 2 * numberDiff }

        if (selectedCells.size == 2) {
            if (nextCell == null) {
                targetCell.piece = Piece.Empty
                performMove(moveOrder, letterDiff, numberDiff)
                updatePiecesTaken()
            } else if (nextCell.piece == Piece.Empty) {
                nextCell.piece = targetCell.piece
                targetCell.piece = Piece.Empty
                performMove(moveOrder, letterDiff, numberDiff)
            }
        } else if (selectedCells.size == 3) {
            if (nextCell == null) {
                targetCell.piece = Piece.Empty
                performMove(moveOrder, letterDiff, numberDiff)
                updatePiecesTaken()
            } else if (nextCell.piece == Piece.Empty) {
                nextCell.piece = targetCell.piece
                targetCell.piece = Piece.Empty
                performMove(moveOrder, letterDiff, numberDiff)
            } else if (nextCell.piece == targetCell.piece) {
                if (nextnextCell == null) {
                    targetCell.piece = Piece.Empty
                    performMove(moveOrder, letterDiff, numberDiff)
                    updatePiecesTaken()
                } else if (nextnextCell.piece == Piece.Empty) {
                    nextnextCell.piece = targetCell.piece
                    nextCell.piece = targetCell.piece
                    targetCell.piece = Piece.Empty
                    performMove(moveOrder, letterDiff, numberDiff)
                }
            }
        }
    }

    private fun updatePiecesTaken() {
        if (currentPlayer.value == Piece.Black) bluePiecesTaken.value++ else redPiecesTaken.value++
    }

    private fun incrementMoveCount() {
        if (currentPlayer.value == Piece.Black) blueMoveNumber.value++ else redMoveNumber.value++
    }

    fun switchPlayer() {
        deepCopiedBoard = boardState.value.map { row -> row.map { it.copy() } }
        currentPlayer.value = if (currentPlayer.value == Piece.Black) Piece.White else Piece.Black
        moveStartTime.value = System.currentTimeMillis()
    }
    fun updateSettings(
        p1Time: Float,
        p2Time: Float,
        layout: String,
        mode: String,
        p1Color: String,
        moves: Float
    ){
        p1TimeLimit = p1Time
        p1MaxTimePerTurn = p1Time
        p2TimeLimit = p2Time
        p2MaxTimePerTurn = p2Time
        selectedLayout = layout
        selectedMode = mode
        player1Color = p1Color
        moveLimit = moves
        boardState = mutableStateOf(createBoard(selectedLayout))
    }
}

