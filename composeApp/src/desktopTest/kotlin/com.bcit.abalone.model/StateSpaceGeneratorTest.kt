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
                Piece.Blue
            )
        )
    }

    @Test
    fun testActions() {
        val state = sampleData[0]
        for ((coordinate, piece) in sampleData[0].board.cells) {
//            println("$coordinate $piece")
        }
        println(state.board)
        TODO("Write tests for action generator")
    }
}