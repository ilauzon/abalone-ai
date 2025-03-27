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
            Coordinate.get(L.I, N.NINE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.I, N.EIGHT) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.I, N.SEVEN) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.I, N.SIX) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.I, N.FIVE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.H, N.FOUR) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.G, N.THREE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.F, N.TWO) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.E, N.ONE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.D, N.ONE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.C, N.ONE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.B, N.ONE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.A, N.ONE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.A, N.TWO) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.A, N.THREE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.A, N.FOUR) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.A, N.FIVE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.B, N.SIX) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.C, N.SEVEN) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.D, N.EIGHT) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.E, N.NINE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.F, N.NINE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.G, N.NINE) to Coordinate.get(L.E, N.FIVE),
            Coordinate.get(L.H, N.NINE) to Coordinate.get(L.E, N.FIVE),
        )

        for ((c1, c2) in tests) {
            assertEquals(runDistTest(c1, c2), 4)
        }
    }

    private fun runDistTest(c1: Coordinate, c2: Coordinate): Int {
        val dist = IsaacHeuristic.dist(c1, c2)
        println("$c1 -> $c2 $dist")
        return dist
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

        val closeness = IsaacHeuristic.closenessToCentre(testCase.board, testCase)
        println(testCase.toStringPretty())
        println("Closeness: $closeness")
    }
}