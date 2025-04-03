package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.BoardState
import com.bcit.abalone.model.StateRepresentation
import com.bcit.abalone.model.StateSpaceGenerator
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.measureTimedValue


class StateSearcherTest {

    @Test
    fun testStateSearcher() {
        val moves = 20
        var state = StateRepresentation(
            board = BoardState(BoardState.Layout.STANDARD),
            movesRemaining = moves,
            currentPlayer = Piece.Black
        )
        var firstMove = false
        var totalTime: Duration = Duration.ZERO
        var blackTurn = true
        var blackTime = Duration.ZERO
        var whiteTime = Duration.ZERO
        val black = StateSearcher(IsaacHeuristic())
        val white = StateSearcher(IsaacHeuristic())
        var searcher = black
        while (!searcher.terminalTest(state)) {
            val (action, time) = measureTimedValue {
                searcher.search(state, depth = 8, firstMove)
            }
            if (blackTurn) {
                searcher = white
            } else {
                searcher = black
            }
            println("TIME FOR MOVE: $time")
            println("Black's turn: $blackTurn")
            if (blackTurn) {
                blackTime += time
            } else {
                whiteTime += time
            }
            totalTime += time
            state = StateSpaceGenerator.result(state, action)
            println(action)
            println(state.toStringPretty())
            firstMove = false
            blackTurn = !blackTurn
        }
        println("TOTAL TIME FOR MATCH WITH $moves MOVES: $totalTime")
        println("BLACK TIME: $blackTime")
        println("BLACK CACHE HITS: ${black.cacheHits}")
        println("BLACK CACHE MISSES: ${black.cacheMisses}")
        println("WHITE TIME: $whiteTime")
        println("WHITE CACHE HITS: ${white.cacheHits}")
        println("WHITE CACHE MISSES: ${white.cacheMisses}")
    }
}