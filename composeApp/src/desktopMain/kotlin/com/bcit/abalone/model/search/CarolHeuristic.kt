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
 * 4. marbles in group is more strong, giving bonus
 * 5. bonus for marbles can do a possible push
 * 6. penalty for being alone
 *
 * the heuristic function considers not only the state is good or not for the current player, but also for the opponent, which gives a relative evaluation to help maximize the current player's advantage and minimize the opponent's
 * the weights for each factor is changing based on the stage of the game. In early game, center control is more important; in late game the piece count is more important.
 */
class CarolHeuristic : Heuristic {
    override fun heuristic(state: StateRepresentation): Double {
        val weights = dynamicWeights(state.movesRemaining)
        val current = weightedScore(state, weights)
        val opponentMovesRemaining = state.movesRemaining - 1
        val opponent = weightedScore(
            StateRepresentation(
                board = state.board,
                players = state.players,
                movesRemaining = opponentMovesRemaining,
                currentPlayer = state.currentPlayer.opposite(),
            ), weights
        )

        return current - opponent
    }

    private fun weightedScore(state: StateRepresentation, weights: Map<String, Double>): Double {
        return (
                weights["pieceCountWeight"]!! * pieceDifference(state) +
                        weights["centerDistanceWeight"]!! * centerDistance(state) +
                        weights["edgePenaltyWeight"]!! * edgePenalty(state) +
                        weights["groupBonusWeight"]!! * groupBonus(state) +
                        weights["possiblePushWeight"]!! * possiblePush(state) +
                        weights["isolationPenaltyWeight"]!! * isolationPenalty(state)
                )
    }

    private fun pieceDifference(state: StateRepresentation): Int {
        val black = state.players[Piece.Black]!!.score
        val white = state.players[Piece.White]!!.score
        val blackRemaining = 14 - black
        val whiteRemaining = 14 - white
        return if (state.currentPlayer == Piece.Black) {
            blackRemaining - whiteRemaining
        } else {
            whiteRemaining - blackRemaining
        }
    }

    private fun centerDistance(state: StateRepresentation): Int {
        val boardCenter = Coordinate.get(LetterCoordinate.E, NumberCoordinate.FIVE)
        return state.getBoardState().cells.entries.sumOf { (coordinate, piece) ->
            if (piece == state.currentPlayer) 9 - coordinate.findDistanceFrom(boardCenter.letter, boardCenter.number) else 0
        }
    }

    private fun edgePenalty(state: StateRepresentation): Int {
        return state.getBoardState().cells.entries.sumOf { (coordinate, piece) ->
            if (piece == state.currentPlayer) {
                val penalty = maxOf(0, (4 - distanceFromEdge(coordinate)))
                -penalty
            } else 0
        }
    }

    private fun distanceFromEdge(coordinate: Coordinate): Int {
        val top = coordinate.letter.ordinal - 1
        val bottom = LetterCoordinate.entries.lastIndex - coordinate.letter.ordinal
        val left = coordinate.number.ordinal - 1
        val right = NumberCoordinate.entries.lastIndex - coordinate.number.ordinal
        return minOf(top, bottom, left, right)
    }

    private fun groupBonus(state: StateRepresentation): Int {
        val board = state.getBoardState().cells
        val currentCoordinates = board.filter { it.value == state.currentPlayer }.keys
        return currentCoordinates.sumOf { coordinate ->
            coordinate.adjacentCoordinates().count { (neighbor, _) -> board[neighbor] == state.currentPlayer }
        }
    }

    private fun possiblePush(state: StateRepresentation): Int {
        val board = state.getBoardState().cells
        val player = state.currentPlayer
        val opponent = player.opposite()

        return board.entries.sumOf { (coord, piece) ->
            if (piece != player) return@sumOf 0

            var score = 0

            for ((next1, dir) in coord.adjacentCoordinates()) {
                if (board[next1] != player) continue

                // Inline 2 push
                val enemy = next1.move(dir)
                if (board[enemy] == opponent) {
                    val behind = enemy.move(dir)
                    if (board[behind] == Piece.Empty || board[behind] == Piece.OffBoard) {
                        score += 5  // Higher reward
                    }
                }

                // Inline 3 push
                val next2 = next1.move(dir)
                if (board[next2] == player) {
                    val enemy2 = next2.move(dir)
                    if (board[enemy2] == opponent) {
                        val behind2 = enemy2.move(dir)
                        if (board[behind2] == Piece.Empty || board[behind2] == Piece.OffBoard) {
                            score += 10 // Even higher reward
                        }
                    }
                }
            }

            score
        }
    }



    private fun isolationPenalty(state: StateRepresentation): Int {
        val board = state.getBoardState().cells
        return board.entries.sumOf { (coord, piece) ->
            val value: Int = if (piece == state.currentPlayer) {
                val neighbors = coord.adjacentCoordinates().count { (neighbor, _) ->
                    board[neighbor] == state.currentPlayer
                }
                if (neighbors == 0) -2 else 0
            } else 0
            value
        }
    }



    fun dynamicWeights(movesLeft: Int): Map<String, Double> {
        return when {
            movesLeft > 30 -> mapOf(
                "pieceCountWeight" to 0.3,
                "centerDistanceWeight" to 0.3,
                "edgePenaltyWeight" to 0.4,
                "groupBonusWeight" to 0.2,
                "possiblePushWeight" to 1.2, // Stronger early-game incentive
                "isolationPenaltyWeight" to 0.2
            )
            movesLeft > 15 -> mapOf(
                "pieceCountWeight" to 0.3,
                "centerDistanceWeight" to 0.2,
                "edgePenaltyWeight" to 0.3,
                "groupBonusWeight" to 0.1,
                "possiblePushWeight" to 1.5, // Even stronger in mid game
                "isolationPenaltyWeight" to 0.2
            )
            else -> mapOf(
                "pieceCountWeight" to 0.4,
                "centerDistanceWeight" to 0.1,
                "edgePenaltyWeight" to 0.2,
                "groupBonusWeight" to 0.1,
                "possiblePushWeight" to 1.6, // Encourage aggressive finish
                "isolationPenaltyWeight" to 0.2
            )
        }
    }
}
