package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.Action
import com.bcit.abalone.model.BoardState
import com.bcit.abalone.model.Coordinate
import com.bcit.abalone.model.MoveDirection
import com.bcit.abalone.model.StateRepresentation
import com.bcit.abalone.model.StateSpaceGenerator
import com.bcit.abalone.model.LetterCoordinate as L
import com.bcit.abalone.model.NumberCoordinate as N
import kotlin.test.Test
import kotlin.test.assertEquals
import com.bcit.abalone.model.search.IsaacHeuristic

class IsaacHeuristicTest {

    @Test
    fun testDist() {
        val tests = listOf(
            Coordinate.get(L.A, N.ONE) to Coordinate.get(L.G, N.SEVEN),
            Coordinate.get(L.B, N.ONE) to Coordinate.get(L.G, N.SEVEN),
            Coordinate.get(L.C, N.ONE) to Coordinate.get(L.G, N.SEVEN),
            Coordinate.get(L.G, N.SEVEN) to Coordinate.get(L.A, N.ONE),
            Coordinate.get(L.G, N.SEVEN) to Coordinate.get(L.B, N.ONE),
            Coordinate.get(L.G, N.SEVEN) to Coordinate.get(L.C, N.ONE),

            Coordinate.get(L.E, N.ONE) to Coordinate.get(L.E, N.NINE),
        )

        for ((c1, c2) in tests) {
            runDistTest(c1, c2)
        }
    }

    private fun runDistTest(c1: Coordinate, c2: Coordinate) {
        println(c1.toString() + " -> " + c2.toString() + " " + IsaacHeuristic.dist(c1, c2))
    }

    @Test
    fun testClosenessToCentre() {
        var testCase = StateRepresentation(
            board = BoardState(BoardState.Layout.STANDARD),
            movesRemaining = 50,
            currentPlayer = Piece.Black
        )

        val action = Action(setOf(
            Coordinate.get(L.I, N.SEVEN),
            Coordinate.get(L.H, N.SEVEN),
            Coordinate.get(L.G, N.SEVEN),
        ), MoveDirection.NegY)

        testCase = StateSpaceGenerator.result(testCase, action)

        val closeness = IsaacHeuristic.closenessToCentre(testCase.board)
        println(testCase.toStringPretty())
        println("Closeness: $closeness")
    }
}