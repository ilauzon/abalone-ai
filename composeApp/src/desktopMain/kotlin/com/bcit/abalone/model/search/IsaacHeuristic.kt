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

    private val closenessWeight = 1
    private val adjacencyWeight = 1

    /**
     * Based on the ABLA agent, described in
     * "A Simple Intelligent Agent for Playing Abalone Game: ABLA" by
     * Ender Ozcan and Berk Hulagu, on pp 6-7.
     *
     * This heuristic function evaluates three features: closeness to the
     * board centre, how grouped the pieces are, and the number of pieces taken.
     *
     * @param state the state being evaluated.
     * @return a value when higher is best for Max, and when lower is best for Min.
     */
    override fun heuristic(state: StateRepresentation): Double {
        val closeness = closenessToCentre(state.board) * closenessWeight
        val adjacency = adjacency(state.board) * adjacencyWeight
        val sum = closeness + adjacency + isWin(state.board)
        return sum
    }

    companion object {
        private const val OFFBOARD_DISTANCE = 10
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
        fun closenessToCentre(board: BoardState): Double {
            val blackMarbles = board.cells.filter { it.value == Piece.Black }
            val whiteMarbles = board.cells.filter { it.value == Piece.White }

            var blackSum = blackMarbles.keys.sumOf { dist(it, Coordinate.get(L.E, N.FIVE)) }
            var whiteSum = whiteMarbles.keys.sumOf { dist(it, Coordinate.get(L.E, N.FIVE)) }

            blackSum += OFFBOARD_DISTANCE * (14 - blackMarbles.count())
            whiteSum += OFFBOARD_DISTANCE * (14 - whiteMarbles.count())

            return (whiteSum - blackSum).toDouble()
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
        fun pieceAdvantage(board: BoardState): Int {
            var blackCount = 0
            var whiteCount = 0
            for (piece in board.cells) {
                if (piece.value == Piece.Black) blackCount++
                else if (piece.value == Piece.White) whiteCount++
            }
            val difference = blackCount - whiteCount
            return difference
        }

        /**
         * Returns negative infinity if White wins, and positive infinity if
         * Black wins. Returns 0 if neither win.
         */
        fun isWin(board: BoardState): Double {
            if (pieceAdvantage(board) >= 6) return Double.POSITIVE_INFINITY
            else if (pieceAdvantage(board) <= -6) return Double.NEGATIVE_INFINITY
            else return 0.0
        }
    }
}