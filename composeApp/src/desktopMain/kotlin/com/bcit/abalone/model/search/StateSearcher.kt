package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.Action
import com.bcit.abalone.model.BoardState
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

    private val cache = TranspositionTable(1_000_000)
    var cacheHits = 0
    var cacheMisses = 0
    var collisions = 0

    /**
     * Performs minimax search with alpha-beta pruning.
     *
     * Returns the best action to take in the current state, which is dependent on the search
     * strategy.
     *
     * @param state the current state, which the returned action is acting on.
     * @param depth the depth to search to.
     * @param firstMove if this is the first move of the agent.
     * @return the "best" action to take from that state.
     */
    fun search(state: StateRepresentation, depth: Int, firstMove: Boolean = false): Action {
        if (depth < 1) {
            throw IllegalArgumentException("depth must be 1 or more for search to occur.")
        } else if (terminalTest(state)) {
            throw IllegalArgumentException("the given state is a terminal state. No action can be chosen.")
        }
        val currentPlayer = toMove(state)

        val bestState: StateRepresentation

        // Black is Max because they move first.
        if (currentPlayer == Piece.Black) {
            if (firstMove) {
                val states = expand(state, 1)
                println("RANDOM FIRST MOVE")
                bestState = states.random().second
            } else {
                bestState = value(true, state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth).second
            }
        } else {
            bestState = value(false, state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth).second
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

    /**
     * Combined Min-Value and Max-Value functions, controlled by the isMax flag.
     *
     * @param isMax true if the calculation is Max-Value().
     * @param state the state being examined.
     * @param a alpha.
     * @param b beta.
     * @param depth the depth to search to.
     * @return the estimated utility of the given state.
     */
    private fun value(isMax: Boolean, state: StateRepresentation, a: Double, b: Double, depth: Int): Pair<Double, StateRepresentation> {
        if (depth <= 0 || terminalTest(state)) {
            return eval(state) to state
        }

        val cachedValue = cache[state.board.cells]
        if (cachedValue != null && cachedValue.depth >= depth) {
            cacheHits++
            return cachedValue.value.toDouble() to state
        } else {
            cacheMisses++
        }

        var v = if (isMax) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
        var bestResult: StateRepresentation? = null
        var alpha = a
        var beta = b
//        val states = actions(state).map { result(state, it) to eval(state) }
//        val sortedStates: List<Pair<StateRepresentation, Double>>
//        if (isMax) {
//            sortedStates = states.sortedByDescending { it.second }
//        } else {
//            sortedStates = states.sortedBy { it.second }
//        }
//        for ((result, _) in sortedStates) {
        for (action in actions(state)) {
            val result = result(state, action)
            val newV = value(!isMax, result, alpha, beta, depth - 1).first
            if (isMax && newV > v || !isMax && newV < v) {
                v = newV
                bestResult = result
            }
            if (isMax && v > beta || !isMax && v < alpha) {
//                if (cache[state.board.cells] != null && cache[state.board.cells] != TranspositionTable.Entry(v.toFloat(), depth)) {
//                    collisions++
//                }
                cache[state.board.cells] = TranspositionTable.Entry(v.toFloat(), depth)
                return v to bestResult!!
            }
            if (isMax) {
                alpha = max(alpha, v)
            } else {
                beta = min(beta, v)
            }
        }
//        if (cache[state.board.cells] != null && cache[state.board.cells] != TranspositionTable.Entry(v.toFloat(), depth)) {
//            collisions++
//        }
        cache[state.board.cells] = TranspositionTable.Entry(v.toFloat(), depth)
        return v to bestResult!!
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