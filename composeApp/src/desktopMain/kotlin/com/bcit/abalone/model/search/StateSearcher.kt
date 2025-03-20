package com.bcit.abalone.model.search

import androidx.compose.animation.core.animateValueAsState
import com.bcit.abalone.Piece
import com.bcit.abalone.model.Action
import com.bcit.abalone.model.StateRepresentation
import com.bcit.abalone.model.StateSpaceGenerator.Companion.actions
import com.bcit.abalone.model.StateSpaceGenerator.Companion.expand
import com.bcit.abalone.model.StateSpaceGenerator.Companion.result
import kotlin.math.max
import kotlin.math.min

/**
 * Responsible for performing search on a game tree. The game tree is generated dynamically.
 */
class StateSearcher(private val heuristic: Heuristic) {

    /**
     * Performs minimax search with alpha-beta pruning.
     *
     * Returns the best action to take in the current state, which is dependent on the search
     * strategy.
     *
     * @param state the current state, which the returned action is acting on.
     * @param depth the depth to search to.
     * @return the "best" action to take from that state.
     */
    fun search(state: StateRepresentation, depth: Int): Action {
        if (depth < 1) {
            throw IllegalArgumentException("depth must be 1 or more for search to occur.")
        } else if (terminalTest(state)) {
            throw IllegalArgumentException("the given state is a terminal state. No action can be chosen.")
        }
        val currentPlayer = toMove(state)

        val bestState: StateRepresentation

        // Black is Max because they move first.
        if (currentPlayer == Piece.Black) {
            bestState = maxValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth).second!!
        } else {
            bestState = minValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth).second!!
        }

        val actionStates = expand(state, 1)
        return actionStates.find { it.second == bestState }!!.first
    }

    /**
     * Determines if the given state is a terminal state for minimax search.
     *
     * @param state the state being examined.
     * @return true if the state is terminal, false if otherwise.
     */
    fun terminalTest(state: StateRepresentation): Boolean {
        return state.players[state.currentPlayer]!!.score >= 6
            || state.players[state.currentPlayer.opposite()]!!.score >= 6
            || state.movesRemaining <= 0
    }

    private fun maxValue(state: StateRepresentation, a: Double, b: Double, depth: Int): Pair<Double, StateRepresentation?> {
        if (depth <= 0 || terminalTest(state)) {
            return eval(state) to null
        }

        var v = Double.NEGATIVE_INFINITY
        var bestResult: StateRepresentation? = null
        var alpha = a
        var beta = b
        for (action in actions(state)) {
            val result = result(state, action)
            val newV = minValue(result, alpha, beta, depth - 1).first
            if (newV > v) {
                v = newV
                bestResult = result
            }
            if (v > beta) return v to bestResult
            alpha = max(alpha, v)
        }
        return v to bestResult
    }

    private fun minValue(state: StateRepresentation, a: Double, b: Double, depth: Int): Pair<Double, StateRepresentation?> {
        if (depth <= 0 || terminalTest(state)) {
            return eval(state) to null
        }

        var v = Double.POSITIVE_INFINITY
        var bestResult: StateRepresentation? = null
        var alpha = a
        var beta = b
        for (action in actions(state)) {
            val result = result(state, action)
            val newV = maxValue(result, alpha, beta, depth - 1).first
            if (newV < v) {
                v = newV
                bestResult = result
            }
            if (v < alpha) return v to bestResult
            beta = min(alpha, v)
        }
        return v to bestResult
    }

    /**
     * Estimates a state's utility value.
     *
     */
    private fun eval(state: StateRepresentation): Double {
        return heuristic.heuristic(state)
    }

    /**
     * Returns the colour whose move it currently is.
     *
     * @param state the state being examined.
     * @return Piece.Black or Piece.White.
     */
    private fun toMove(state: StateRepresentation): Piece {
        val currentPlayer = state.currentPlayer
        if (currentPlayer == Piece.Empty || currentPlayer == Piece.OffBoard) {
            throw IllegalArgumentException(
                "$currentPlayer is an invalid current player, " +
                        "the next move must be either Black or White."
            )
        }
        return currentPlayer
    }
}