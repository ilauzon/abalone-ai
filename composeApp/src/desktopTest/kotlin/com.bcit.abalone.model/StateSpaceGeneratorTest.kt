import com.bcit.abalone.Piece
import com.bcit.abalone.model.*
import kotlin.test.Test
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
}