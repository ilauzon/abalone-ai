package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.Coordinate
import com.bcit.abalone.model.LetterCoordinate
import com.bcit.abalone.model.NumberCoordinate
import com.bcit.abalone.model.StateRepresentation


/**
 * The heuristic function considers five factors that may affect the result:
 * 1. the total number of marbles for each player in every state; more marbles, more possibility to win
 * 2. if the marbles are more closer to center; this factor may be more important in early game which is a strategy to divide opponent's marbles to parts and more possible to push out opponent
 * 3. penalize closer to edge because marbles near edge are more vulnerable
 *
 * the heuristic function considers not only the state is good or not for the current player, but also for the opponent, which gives a relative evaluation to help maximize the current player's advantage and minimize the opponent's
 * the weights for each factor is changing based on the stage of the game. In early game, center control is more important; in late game the piece count is more important.
 */
class CarolHeuristic : Heuristic {

    private val boardCenter = Coordinate.get(LetterCoordinate.E, NumberCoordinate.FIVE)

    override fun heuristic(state: StateRepresentation): Double {
        val weights = dynamicWeights(state.movesRemaining)
        val currentScore = weightedScore(state, weights)

        val opponentState = StateRepresentation(
            board = state.board,
            players = state.players,
            movesRemaining = state.movesRemaining - 1,
            currentPlayer = state.currentPlayer.opposite()
        )
        val opponentScore = weightedScore(opponentState, weights)

        return currentScore - opponentScore
    }

    private fun weightedScore(state: StateRepresentation, weights: Map<String, Double>): Double {
        val maxDistFromCenter = maxDistanceFromCenter(state)
        return (
                weights["pieceCountWeight"]!! * dynamicPieceDifference(state) +
                        weights["centerDistanceWeight"]!! * centerDistance(state, maxDistFromCenter) +
                        weights["edgePenaltyWeight"]!! * edgePenalty(state)
                )
    }

    private fun dynamicPieceDifference(state: StateRepresentation): Double {
        val blackCaptured = state.players[Piece.Black]!!.score
        val whiteCaptured = state.players[Piece.White]!!.score

        val blackRemaining = 14 - blackCaptured
        val whiteRemaining = 14 - whiteCaptured

        val diff = if (state.currentPlayer == Piece.Black) {
            blackRemaining - whiteRemaining
        } else {
            whiteRemaining - blackRemaining
        }

        // Normalize to 0–10 scale
        return ((diff + 6).coerceIn(0, 12)) / 12.0 * 10
    }

    private fun centerDistance(state: StateRepresentation, maxDistance: Double): Double {
        return state.getBoardState().cells.entries.sumOf { (coordinate, piece) ->
            if (piece == state.currentPlayer) {
                val distance = coordinate.findDistanceFrom(boardCenter.letter, boardCenter.number).toDouble()
                val normalized = 1.0 - (distance / maxDistance)
                normalized * 10
            } else 0.0
        }
    }

    private fun edgePenalty(state: StateRepresentation): Double {
        return state.getBoardState().cells.entries.sumOf { (coordinate, piece) ->
            if (piece == state.currentPlayer) {
                val dist = distanceFromEdge(coordinate).toDouble()
                val penalty = (4.0 - dist).coerceAtLeast(0.0)
                -penalty * 0.5
            } else 0.0
        }
    }

    private fun distanceFromEdge(coordinate: Coordinate): Int {
        val top = coordinate.letter.ordinal
        val bottom = LetterCoordinate.entries.lastIndex - coordinate.letter.ordinal
        val left = coordinate.number.ordinal
        val right = NumberCoordinate.entries.lastIndex - coordinate.number.ordinal
        return minOf(top, bottom, left, right)
    }

    // Dynamically calculate max distance from center using current board's valid coordinates
    private fun maxDistanceFromCenter(state: StateRepresentation): Double {
        return state.getBoardState().cells.keys.maxOf {
            it.findDistanceFrom(boardCenter.letter, boardCenter.number).toDouble()
        }
    }

    private fun dynamicWeights(movesLeft: Int): Map<String, Double> {
        return when {
            movesLeft > 30 -> mapOf(
                "pieceCountWeight" to 0.3,
                "centerDistanceWeight" to 0.5,
                "edgePenaltyWeight" to 0.2
            )
            movesLeft > 15 -> mapOf(
                "pieceCountWeight" to 0.4,
                "centerDistanceWeight" to 0.4,
                "edgePenaltyWeight" to 0.2
            )
            else -> mapOf(
                "pieceCountWeight" to 0.6,
                "centerDistanceWeight" to 0.3,
                "edgePenaltyWeight" to 0.1
            )
        }
    }
}
