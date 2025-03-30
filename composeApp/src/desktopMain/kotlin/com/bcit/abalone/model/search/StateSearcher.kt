package com.bcit.abalone.model.search

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

    private val cache = TranspositionTable(1_000_000)
    var cacheHits = 0
    var cacheMisses = 0

    /**
     * A place to store the action chosen by minimax search once it has completed.
     * Is mutated by calling the search() function.
     */
    private var actionChosen: Action? = null

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

        // Black is Max because they move first.
        if (currentPlayer == Piece.Black) {
            if (firstMove) {
                val states = expand(state)
                println("RANDOM FIRST MOVE")
                return states.random().first
            } else {
                value(true, state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth)
            }
        } else {
            value(false, state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth)
        }

        return actionChosen!!
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
     *
     * This function writes to the actionChosen instance variable upon completing its recursive calls. The alternative
     * to this would have been passing around a reference to an action to every recursive call of value().
     *
     * @param isMax true if the calculation is Max-Value().
     * @param state the state being examined.
     * @param a alpha.
     * @param b beta.
     * @param depth the depth to search to.
     * @return the estimated utility of the given state.
     */
    private fun value(isMax: Boolean, state: StateRepresentation, a: Double, b: Double, depth: Int): Double {
        val startTime = System.nanoTime()
        if (depth <= 0 || terminalTest(state)) {
            return eval(state)
        }

        val cachedValue = getCachedState(state, depth)
        if (cachedValue != null) {
            actionChosen = cachedValue.action
            return cachedValue.value
        }

        var v = if (isMax) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
        var bestAction: Action? = null
        var alpha = a
        var beta = b

        // ordering nodes, if isMax is true, sort in descending order, else sort in ascending order
        val sortedStates = actions(state)
            .map { action ->
                val newState = result(state, action)
                Triple(newState, eval(newState), action) }
            .sortedByDescending { if (isMax) it.second else -it.second }

//        for ((s, value) in sortedStates) {
//            print("$value, ")
//            print(s)
//        }
//        println()
//        println("-----------------------------------------------")
        if (sortedStates.isEmpty()) throw IllegalArgumentException(
            "Zero actions were generated from the non-terminal state."
        )
        for ((result, _, action) in sortedStates) {
            val newV = value(!isMax, result, alpha, beta, depth - 1)
            if (isMax && newV > v || !isMax && newV < v) {
                v = newV
                bestAction = action
            }
            if (isMax && v > beta || !isMax && v < alpha) {
                cacheState(state, bestAction!!, v, depth)
                actionChosen = bestAction
                return v
            }
            if (isMax) {
                alpha = max(alpha, v)
            } else {
                beta = min(beta, v)
            }
        }
//        val endTime = System.nanoTime() // End time
//        println("Minimax: Depth: $depth, Time: ${(endTime - startTime) / 1_000_000} ms")
        cacheState(state, bestAction!!, v, depth)
        actionChosen = bestAction
        return v
    }

    /**
     * Estimates a state's utility value.
     *
     */
    private fun eval(state: StateRepresentation): Double {
        return heuristic.heuristic(state)
    }

    private fun cacheState(state: StateRepresentation, action: Action, value: Double, depth: Int) {
        cache[TranspositionTable.Key(
            state.board.cells,
            state.currentPlayer)] = TranspositionTable.Entry(value, action, depth)
    }

    private fun getCachedState(state: StateRepresentation, depth: Int): TranspositionTable.Entry? {
        val key = TranspositionTable.Key(state.board.cells, state.currentPlayer)
        val cachedValue = cache[key]
        if (cachedValue == null || cachedValue.depth < depth) {
            cacheMisses++
            return null
        } else {
            cacheHits++
            return cachedValue
        }
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