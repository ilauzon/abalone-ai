package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.BoardState
import com.bcit.abalone.model.Coordinate
import com.bcit.abalone.model.LetterCoordinate as L
import com.bcit.abalone.model.NumberCoordinate as N
import com.bcit.abalone.model.StateRepresentation
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

/**
 * Isaac's heuristic function implementation.
 */
class IsaacHeuristic: Heuristic {

    /**
     * Based on the ABLA agent, described in
     * "A Simple Intelligent Agent for Playing Abalone Game: ABLA" by
     * Ender Ozcan and Berk Hulagu, on pp 6-7.
     *
     * This heuristic function evaluates two features: closeness to the
     * board centre, and how grouped the pieces are.
     *
     * @param state the state being evaluated.
     * @return a value when higher is best for Max, and when lower is best for Min.
     */
    override fun heuristic(state: StateRepresentation): Double {
        val closeness = closenessToCentre(state.board) * CLOSENESS_WEIGHT
        val adjacency = adjacency(state.board) * ADJACENCY_WEIGHT
//        val moves = state.movesRemaining * MOVES_WEIGHT
        val sum = closeness + adjacency
        return sum
    }

    companion object {
        private const val CLOSENESS_WEIGHT = 0.5
        private const val ADJACENCY_WEIGHT = 0.5
        private const val MOVES_WEIGHT = 2
        private const val OFFBOARD_DISTANCE = 20

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
         * The solution for the Manhattan distance in a hexagonal grid was taken from
         * this article by redblobgames.com:
         * [Hexagonal Grids](https://www.redblobgames.com/grids/hexagons/#distances-axial)
         *
         * @param c1 coordinate 1.
         * @param c2 coordinate 2.
         * @return the number of moves from c1 to c2.
         */
        fun dist(c1: Coordinate, c2: Coordinate): Int {
            val r1 = -c1.letter.ordinal
            val q1 = c1.number.ordinal
            val r2 = -c2.letter.ordinal
            val q2 = c2.number.ordinal

            return (abs(q1 - q2) + abs(q1 + r1 - q2 - r2) + abs(r1 - r2)) / 2
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
         * Returns a large negative number if White wins, and a large positive number if
         * Black wins. Returns 0 if neither win.
         *
         * Win value is weighted depending on the number of moves left, as well as the number
         * of pieces left if the given state is a terminal state.
         */
        private fun winValue(state: StateRepresentation): Double {
            val winWeight = 1_000_000.0
            var blackCount = 0
            var whiteCount = 0
            for (piece in state.board.cells) {
                if (piece.value == Piece.Black) blackCount++
                else if (piece.value == Piece.White) whiteCount++
            }

            val whitePiecesLeft = 14 - whiteCount
            val blackPiecesLeft = 14 - blackCount
            val blackWin = whitePiecesLeft >= 6
            val whiteWin = blackPiecesLeft >= 6
            return if (blackWin) {
                winWeight * (state.movesRemaining + 1) * (blackPiecesLeft - whitePiecesLeft)
            } else if (whiteWin) {
                -winWeight * (state.movesRemaining + 1) * (whitePiecesLeft - blackPiecesLeft)
            } else if (state.movesRemaining == 0) {
                if (blackCount > whiteCount) winWeight
                else if (whiteCount > blackCount) -winWeight
                else 0.0
            } else 0.0
        }
    }
}