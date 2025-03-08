package com.bcit.abalone.model

import com.bcit.abalone.Piece
import com.bcit.abalone.model.LetterCoordinate
import com.bcit.abalone.model.NumberCoordinate
import kotlin.ranges.ClosedRange
import com.bcit.abalone.model.LetterCoordinate as LetterC
import com.bcit.abalone.model.NumberCoordinate as NumberC

/**
 * The state representation of the game. Contains all the information needed for the search
 * and generation algorithms. Instances of this class may be passed between functions for state
 * space generation and search.
 *
 * Instances are mutable. When used for generation and/or search, it is the
 * responsibility of the dependent to make copies of instances when needed.
 */
class StateRepresentation(
    val board: BoardState,
    val players: Map<Piece, Player>,
    val movesRemaining: Int,
    val currentPlayer: Piece
) {

    init {
        if (players.keys.contains(Piece.Empty)) {
            throw IllegalArgumentException(
                "Piece.Empty is an invalid key for players; players can only be Black or White."
            )
        }
    }

    /**
     * Move the pieces in the specified coordinates in a direction.
     *
     * @param cells the coordinates of the pieces to move.
     * @param direction the direction to move the pieces in.
     */
    fun move(cells: Array<Coordinate>, direction: MoveDirection) {

    }
}

/**
 * Represents the state of the game board.
 */
class BoardState {

    /**
     * The game board. The value of a particular cell in accessed via a map, with the coordinates
     * as the key.
     */
    val cells: HashMap<Coordinate, Piece> = HashMap()

    init {
        fillBoardWithEmpty(cells)
    }

    constructor(layout: Layout) {
        when (layout) {
            Layout.STANDARD -> setBoard(generateStandardLayout())
            Layout.BELGIAN_DAISY -> setBoard(generateBelgianDaisyLayout())
            Layout.GERMAN_DAISY -> setBoard(generateGermanDaisyLayout())
        }
    }

    enum class Layout {
        STANDARD,
        BELGIAN_DAISY,
        GERMAN_DAISY,
    }

    constructor(board: Map<Coordinate, Piece>) {
        setBoard(board)
    }

    companion object {
        private fun fillBoardWithEmpty(
            board: HashMap<Coordinate, Piece>
        ) {
            for (l: LetterC in LetterC.entries.drop(1)) {
                for (n: NumberC in NumberC.entries.slice(l.min.ordinal..l.max.ordinal)) {
                    board[Coordinate(l, n)] = Piece.Empty
                }
            }
            board[Coordinate.offBoard] = Piece.OffBoard
        }

        private fun fillBoardLetter(
            board: HashMap<Coordinate, Piece>,
            letter: LetterC,
            piece: Piece,
            range: Iterable<NumberC>
        ) {
            for (n: NumberC in NumberC.entries.slice(letter.min.ordinal..letter.max.ordinal)) {
                if (n in range) {
                    board[Coordinate(letter, n)] = piece
                }
            }
        }

        private fun generateStandardLayout(): Map<Coordinate, Piece> {
            val board = HashMap<Coordinate, Piece>()
            fillBoardWithEmpty(board)
            fillBoardLetter(board, LetterC.A, Piece.Red, LetterC.A.min .. LetterC.A.max)
            fillBoardLetter(board, LetterC.B, Piece.Red, LetterC.B.min .. LetterC.B.max)
            fillBoardLetter(board, LetterC.C, Piece.Red, LetterC.C.min + 2 .. LetterC.C.max - 2)
            fillBoardLetter(board, LetterC.G, Piece.Blue, LetterC.G.min + 2 .. LetterC.G.max - 2)
            fillBoardLetter(board, LetterC.H, Piece.Blue, LetterC.H.min .. LetterC.H.max)
            fillBoardLetter(board, LetterC.I, Piece.Blue, LetterC.I.min .. LetterC.I.max)
            return board
        }

        private fun generateBelgianDaisyLayout(): Map<Coordinate, Piece> {
            TODO("implement the layout generation function for the Belgian Daisy.")
        }

        private fun generateGermanDaisyLayout(): Map<Coordinate, Piece> {
            TODO("implement the layout generation function for the German Daisy.")
        }
    }

    internal fun move() {
        TODO("implement the move function in BoardState")
    }

    private fun setBoard(board: Map<Coordinate, Piece>) {
        for (key in board.keys) {
            this.cells[key] = board[key]!!
        }
    }

    override fun toString(): String {
        val letterRowToString = {letter: LetterC ->
            (letter.min .. letter.max).joinToString(
                separator = ",",
                prefix = "[",
                postfix = "]",
                transform = {
                    val piece = cells[Coordinate(letter, it)]!!
                    when (piece) {
                        Piece.Empty -> "0"
                        Piece.Blue -> "2"
                        Piece.Red -> "1"
                        Piece.OffBoard -> " "
                    }
                }
            )
        }

        val returnString = """
                I ${letterRowToString(LetterC.I)} 
               H ${letterRowToString(LetterC.H)} 
              G ${letterRowToString(LetterC.G)} 
             F ${letterRowToString(LetterC.F)} 
            E ${letterRowToString(LetterC.E)} 
             D ${letterRowToString(LetterC.D)}9
              C ${letterRowToString(LetterC.C)}8
               B ${letterRowToString(LetterC.B)}7
                A ${letterRowToString(LetterC.A)}6
                    1 2 3 4 5
        """.trimIndent()

        return returnString
    }
}

/**
 * Represents the data related to a player's state.
 *
 * @property score the number of pieces this player has taken. When this is 6, the player
 * has won the game.
 * @property moveTime the time this player has to move, in milliseconds.
 */
data class Player(
    val score: Int,
    val moveTime: Int,
)