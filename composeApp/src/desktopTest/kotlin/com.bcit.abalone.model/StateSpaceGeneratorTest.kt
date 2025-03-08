import com.bcit.abalone.Piece
import com.bcit.abalone.model.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.time.TimeSource

class StateSpaceGeneratorTest {
    companion object {
        val sampleData = listOf(
            StateRepresentation(
                BoardState(BoardState.Layout.STANDARD),
                mapOf(
                    Pair(Piece.Red, Player(0, 60000)),
                    Pair(Piece.Blue, Player(0, 60000))
                ),
                50,
                Piece.Red
            )
        )
    }

    @Test
    fun testActions() {
        val timeSource = TimeSource.Monotonic
        val state = sampleData[0]
        println("-------------------INITIAL STATE----------------------")
        println(state)
        println("----------ACTIONS AND THEIR RESULTING STATES----------")
        val mark1 = timeSource.markNow()
        val actions = StateSpaceGenerator.actions(state)
        actions.forEach {
            println("Action: $it")
            println("State:\n${StateSpaceGenerator.result(state, it)}")
        }
        val mark2 = timeSource.markNow()
        println("Time taken to generate: ${mark2 - mark1}")
    }
}