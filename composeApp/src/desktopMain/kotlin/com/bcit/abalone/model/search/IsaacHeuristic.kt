package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.BoardState
import com.bcit.abalone.model.Coordinate
import com.bcit.abalone.model.LetterCoordinate as L
import com.bcit.abalone.model.NumberCoordinate as N
import com.bcit.abalone.model.StateRepresentation
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

/**
 * Isaac's heuristic function implementation.
 */
class IsaacHeuristic: Heuristic {

    private val closenessWeight = 1.5
    private val adjacencyWeight = 1
    private val piecesWeight = 8

    /**
     * Based on the ABLA agent, described in
     *
     * "A Simple Intelligent Agent for Playing Abalone Game: ABLA" by
     * Ender Ozcan and Berk Hulagu, on pp 6-7.
     *
     * This heuristic function evaluates two features: closeness to the
     * board centre and grouped-togetherness.
     *
     * @param state the state being evaluated.
     * @return a value when higher is best for Max, and when lower is best for Min.
     */
    override fun heuristic(state: StateRepresentation): Double {
        val closeness = closenessToCentre(state.board) * closenessWeight
        val adjacency = adjacency(state.board) * adjacencyWeight
        val pieces = pieceAdvantage(state) * piecesWeight
        println("Closeness: $closeness   \tAdjacency: $adjacency   \tPieces: $pieces")
        return (closeness + adjacency + pieces).toDouble()
    }

    companion object {
        /**
         * Computes how close the pieces are to the centre of the board, E5.
         * This algorithm uses the sum of the Manhattan distances of the marbles
         * on the board based on colour, and finds the difference between the
         * two.
         * <p>
         * A positive value means that black's pieces are closer to the centre, and
         * a negative value means that white's pieces are closer to the centre.
         *
         * @param board the board state being examined.
         * @return the sum of white's distance to the centre, minus the sum of black's
         * distance to the centre.
         */
        fun closenessToCentre(board: BoardState): Int {
            val blackMarbles = board.cells.filter { it.value == Piece.Black }
            val whiteMarbles = board.cells.filter { it.value == Piece.White }

            val blackSum = blackMarbles.keys.sumOf { dist(it, Coordinate.get(L.E, N.FIVE)) }
            val whiteSum = whiteMarbles.keys.sumOf { dist(it, Coordinate.get(L.E, N.FIVE)) }

            return whiteSum - blackSum
        }

        /**
         * Computes the number of moves to move a marble from one coordinate to another,
         * i.e. the Manhattan distance.
         *
         * @param c1 coordinate 1.
         * @param c2 coordinate 2.
         * @return the number of moves from c1 to c2.
         */
        fun dist(c1: Coordinate, c2: Coordinate): Int {
            val xDist = c1.number.ordinal - c2.number.ordinal
            val yDist = c1.letter.ordinal - c2.letter.ordinal

            return if (xDist.sign == yDist.sign)
                abs(xDist - yDist) + max(abs(xDist), abs(yDist))
            else
                max(abs(xDist), abs(yDist))
        }

        /**
         * Computes the overall adjacency advantage for Black. This is done by counting
         * the number of same-colour neighbours for Black and White, then subtracting
         * Black's count from White's.
         *
         * @param board the game board being examined.
         * @return
         */
        fun adjacency(board: BoardState): Int {
            val blackMarbles = board.cells.filter { it.value == Piece.Black }
            val whiteMarbles = board.cells.filter { it.value == Piece.White }

            var blackAdjacent = 0
            var whiteAdjacent = 0
            for ((marble, _) in blackMarbles) {
                val coords = marble.adjacentCoordinates().map { it.first }
                for (coord in coords) {
                    if (board.cells[coord] == Piece.Black) {
                        blackAdjacent++
                    }
                }
            }

            for ((marble, _) in whiteMarbles) {
                val coords = marble.adjacentCoordinates().map { it.first }
                for (coord in coords) {
                    if (board.cells[coord] == Piece.White) {
                        whiteAdjacent++
                    }
                }
            }

            return blackAdjacent - whiteAdjacent
        }

        /**
         * @return the difference of black pieces to white pieces.
         */
        fun pieceAdvantage(state: StateRepresentation): Int {
            return state.players[Piece.Black]!!.score - state.players[Piece.White]!!.score
        }
    }
}