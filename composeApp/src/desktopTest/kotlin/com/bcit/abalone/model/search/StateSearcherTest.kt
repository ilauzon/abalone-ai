package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.Action
import com.bcit.abalone.model.BoardState
import com.bcit.abalone.model.StateRepresentation
import com.bcit.abalone.model.StateSpaceGenerator
import kotlin.test.Test
import kotlin.time.measureTimedValue


class StateSearcherTest {

    @Test
    fun testStateSearcher() {
        val searcher = StateSearcher(IsaacHeuristic())
        var state = StateRepresentation(
            board = BoardState(BoardState.Layout.STANDARD),
            movesRemaining = 20,
            currentPlayer = Piece.Black
        )
        var firstMove = true
        while (!searcher.terminalTest(state)) {
            val (action, time) = measureTimedValue {
                searcher.search(state, depth = 3, firstMove)
            }
            println("TIME FOR MOVE: $time")
            state = StateSpaceGenerator.result(state, action)
            println(state.toStringPretty())
            firstMove = false
        }
    }
}