package com.bcit.abalone.model

import com.bcit.abalone.Piece

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
class BoardState() {

    /**
     * The game board. The value of a particular cell in accessed via a map, with the coordinates
     * as the key.
     */
    val board: HashMap<Coordinate, Piece?> = HashMap()

    init {
        for (l: LetterCoordinate in LetterCoordinate.entries.drop(1)) {
            for (n: NumberCoordinate in NumberCoordinate.entries.slice(l.min.ordinal..l.max.ordinal)) {
                board[Coordinate(l, n)] = null
            }
        }
    }

    constructor(layout: Layout) : this() {
        when (layout) {
            Layout.STANDARD -> TODO("Implement standard layout")
            Layout.BELGIAN_DAISY -> TODO("Implement belgian daisy layout")
            Layout.GERMAN_DAISY -> TODO("Implement german daisy layout")
        }
    }

    enum class Layout {
        STANDARD,
        BELGIAN_DAISY,
        GERMAN_DAISY,
    }

    internal fun move() {
        TODO("implement the move function in BoardState")
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