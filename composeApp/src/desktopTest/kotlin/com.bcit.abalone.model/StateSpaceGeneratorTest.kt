import com.bcit.abalone.Piece
import com.bcit.abalone.model.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class StateSpaceGeneratorTest {
    companion object {
        val sampleData = listOf<StateRepresentation>(
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
        val state = sampleData[0]
        println("-----------BOARD-----------")
        println(state.board)
        println("----------ACTIONS----------")
        val actions = StateSpaceGenerator.actions(state)
        actions.forEach {
            println(it)
        }

    }
}