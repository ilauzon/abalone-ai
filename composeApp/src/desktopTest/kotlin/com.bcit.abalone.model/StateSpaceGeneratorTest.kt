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

    /**
     * Tests the state space generator on the Test1.input and Test1.board files provided for
     * the project.
     */
    @Test
    fun test1() {
        // read the file input
        val text = AbaloneFileIO.readDataFile("examples/Test1.board")

        //parse board from file
        val boardState = AbaloneFileIO.parseState(text[0], text[1])

        println("INITIAL STATE: ")
        println(boardState.toStringPretty())

        // feed board into StateSpaceGenerator
        val boards: MutableList<StateRepresentation> = mutableListOf()
        val actions: List<Action> = StateSpaceGenerator.actions(boardState).toList()
        for (action in actions) {
            println("------------------------------------------------------------")
            val resultState = StateSpaceGenerator.result(boardState, action)
            println("ACTION: $action")
            println("STATE:")
            println(resultState.toStringPretty())
            boards.add(resultState)
        }

        // turn actions and boards into strings for file output
        val actionsStrings = AbaloneFileIO.stringifyActions(actions)
        val boardStrings = AbaloneFileIO.stringifyBoards(boards)
        TODO("Finish test for Test1.input")
    }

    /**
     * Tests the state space generator on the Test2.input and Test2.board files provided for
     * the project.
     */
    @Test
    fun test2() {
        TODO("Add test for Test2.input")
    }

    @Test
    fun benchmark() {
        val depth = 3
        println("--------------------------------------------------------------------------------")
        println("Expanding state space to depth $depth ...")
        val timeSource = TimeSource.Monotonic
        val mark1 = timeSource.markNow()
        val actionStates = StateSpaceGenerator.expand(sampleData[0], depth)
        val mark2 = timeSource.markNow()
        println("Expansion finished!")
        println("Generating ${actionStates.size} states to depth $depth took ${mark2 - mark1}")
        println("--------------------------------------------------------------------------------")
    }
}