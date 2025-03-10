import com.bcit.abalone.Piece
import com.bcit.abalone.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.TimeSource

class StateSpaceGeneratorTest {
    companion object {
        val sampleData = listOf(
            StateRepresentation(
                BoardState(BoardState.Layout.STANDARD),
                mapOf(
                    Piece.Black to Player(0, 60000),
                    Piece.White to Player(0, 60000),
                ),
                50,
                Piece.Black
            )
        )
    }

    @Test
    fun testActions() {
        val timeSource = TimeSource.Monotonic
        val initialState = sampleData[0]
        println("-------------------INITIAL STATE----------------------")
        println(initialState)
        println("----------ACTIONS AND THEIR RESULTING STATES----------")
        val mark1 = timeSource.markNow()
        val actions = StateSpaceGenerator.actions(initialState)
        val actionStates = mutableListOf<Pair<Action, StateRepresentation>>()
        actions.forEach {
            val state = StateSpaceGenerator.result(initialState, it)
            actionStates.add(it to state)
        }
        val mark2 = timeSource.markNow()
        println("Time taken to generate: ${mark2 - mark1}")
        println("# states generated: ${actionStates.size}")
        println("------------------------------------------------------")
        for ((action, state) in actionStates) {
            println("Action: $action")
            println("State:\n${state}")
        }
    }

    @Test
    fun benchmark() {
        val depth = 3
        println("--------------------------------------------------------------------------------")
        println("Expanding state space to depth $depth ...")
        val timeSource = TimeSource.Monotonic
        val mark1 = timeSource.markNow()
        val actionStates = expand(sampleData[0], depth)
        val mark2 = timeSource.markNow()
        println("Expansion finished!")
        println("Generating ${actionStates.size} states to depth $depth took ${mark2 - mark1}")
        println("--------------------------------------------------------------------------------")
    }

    @Test
    fun testCoordinateHashCode() {
        var count = 0
        var collisions = 0
        val generatedHashCodes = mutableSetOf<Int>()
        for (letter in LetterCoordinate.entries) {
            for (number in letter.min .. letter.max) {
                count++
                val hash = Coordinate.get(letter, number).hashCode()
//                println("$letter$number\t$hash")
                if (generatedHashCodes.contains(hash)) {
                    collisions++
                } else {
                    generatedHashCodes.add(hash)
                }
            }
        }
        assertEquals(collisions, 0)
        println("Collisions: $collisions")
        println("Count: $count")
    }

    private fun expand(state: StateRepresentation, depth: Int): List<Pair<Action, StateRepresentation>> {
        if (depth == 1) {
            return expandOnce(state)
        }
        val actionStates = expand(state, depth - 1)
        val newActionStates = mutableListOf<Pair<Action, StateRepresentation>>()
        for ((_, newState) in actionStates) {
            newActionStates.addAll(expandOnce(newState))
        }
        return newActionStates
    }

    private fun expandOnce(state: StateRepresentation): List<Pair<Action, StateRepresentation>> {
        val actions = StateSpaceGenerator.actions(state)
        val actionStates = mutableListOf<Pair<Action, StateRepresentation>>()
        actions.forEach {
            val newState = StateSpaceGenerator.result(state, it)
            actionStates.add(it to newState)
        }
        return actionStates
    }
}