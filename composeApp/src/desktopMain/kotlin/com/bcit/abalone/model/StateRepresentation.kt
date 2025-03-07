package com.bcit.abalone.model

import com.bcit.abalone.Piece
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
            Layout.GERMAN_DAISY -> setBoard(generateGermainDaisyLayout())
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
            range: ClosedRange<NumberC>
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

        private fun generateGermainDaisyLayout(): Map<Coordinate, Piece> {
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
        // TODO implement the toString method for displaying the BoardState.
        return super.toString()
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