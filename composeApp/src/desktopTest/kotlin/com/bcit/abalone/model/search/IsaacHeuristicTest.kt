package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.*
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

        val closeness = IsaacHeuristic.closenessToCentre(testCase.board)
        println(testCase.toStringPretty())
        println("Closeness: $closeness")
    }

    @Test
    fun testAdjacency() {
        var testCase = StateRepresentation(
            board = BoardState(BoardState.Layout.STANDARD),
            movesRemaining = 50,
            currentPlayer = Piece.Black
        )

        var adjacency = IsaacHeuristic.adjacency(testCase.board)
        assertEquals(0, adjacency)

        val nextCase = StateSpaceGenerator.result(testCase, Action(setOf(
            Coordinate.get(L.A, N.ONE),
            Coordinate.get(L.B, N.TWO),
            Coordinate.get(L.C, N.THREE),
        ), MoveDirection.PosZ))

        adjacency = IsaacHeuristic.adjacency(nextCase.board)
        assertEquals(52 - 54, adjacency)
    }

    @Test
    fun testCase() {
        fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }
        val board = BoardMap()
        board[Coordinate.get(L.I, N.SIX)] = Piece.White
        board[Coordinate.get(L.I, N.EIGHT)] = Piece.Black
        board[Coordinate.get(L.I, N.NINE)] = Piece.Black
        board[Coordinate.get(L.H, N.FOUR)] = Piece.White
        board[Coordinate.get(L.H, N.FIVE)] = Piece.White
        board[Coordinate.get(L.H, N.SIX)] = Piece.White
        board[Coordinate.get(L.H, N.EIGHT)] = Piece.Black
        board[Coordinate.get(L.H, N.NINE)] = Piece.Black
        board[Coordinate.get(L.G, N.FOUR)] = Piece.White
        board[Coordinate.get(L.G, N.FIVE)] = Piece.White
        board[Coordinate.get(L.G, N.SIX)] = Piece.White
        board[Coordinate.get(L.G, N.SEVEN)] = Piece.Black
        board[Coordinate.get(L.G, N.EIGHT)] = Piece.Black
        board[Coordinate.get(L.F, N.SIX)] = Piece.Black
        board[Coordinate.get(L.D, N.THREE)] = Piece.Black
        board[Coordinate.get(L.D, N.FIVE)] = Piece.Black
        board[Coordinate.get(L.C, N.FIVE)] = Piece.White
        board[Coordinate.get(L.C, N.SIX)] = Piece.White
        board[Coordinate.get(L.C, N.SEVEN)] = Piece.White
        board[Coordinate.get(L.B, N.TWO)] = Piece.White
        board[Coordinate.get(L.B, N.THREE)] = Piece.White
        board[Coordinate.get(L.B, N.FOUR)] = Piece.White
        board[Coordinate.get(L.B, N.FIVE)] = Piece.White
        val state = StateRepresentation(
            board = BoardState(board),
            players = mapOf(
                Piece.Black to Player(0, 5000),
                Piece.White to Player(5, 5000)
            ),
            movesRemaining = 49,
            currentPlayer = Piece.White
        )
        println(state.toStringPretty())
        val bestAction = StateSearcher(IsaacHeuristic()).search(state, depth = 6)
        println(bestAction)
    }
}