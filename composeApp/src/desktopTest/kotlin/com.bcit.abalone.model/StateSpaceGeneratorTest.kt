import com.bcit.abalone.Piece
import com.bcit.abalone.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
        testInputFile("Test1")
    }

    /**
     * Tests the state space generator on the Test2.input and Test2.board files provided for
     * the project.
     */
    @Test
    fun test2() {
        testInputFile("Test2")
    }

    @Test
    fun testAllExamples() {

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

    /**
     * Tests the file with the given name in the examples/ folder, and generates .board and .move
     * files in the out/ folder. A .board file in the examples/ folder is also needed for comparison.
     *
     * @param testname the name of the file, without the .input suffix. The function will look
     * for the file with the file name of examples/<filename>.input.
     */
    private fun testInputFile(testname: String) {
        println("------------------------------------------------------------")
        // read the file input
        val text = AbaloneFileIO.readDataFile("examples/$testname.input")
        val outputMoveFile = "out/$testname.move"
        val outputBoardFile = "out/$testname.board"

        //parse board from file
        val boardState = AbaloneFileIO.parseState(text[0], text[1])

        println("INITIAL STATE FOR $testname: ")
        println(boardState.toStringPretty())

        // feed board into StateSpaceGenerator
        val boards: MutableList<StateRepresentation> = mutableListOf()
        val actions: List<Action> = StateSpaceGenerator.actions(boardState).toList()
        for (action in actions) {
            val resultState = StateSpaceGenerator.result(boardState, action)
            boards.add(resultState)
        }

        // turn actions and boards into strings for file output
        val actionsStrings = AbaloneFileIO.stringifyActions(actions)
        val boardStrings = AbaloneFileIO.stringifyBoards(boards)

        // write output to respective files
        AbaloneFileIO.writeDataFile(outputMoveFile, actionsStrings)
        AbaloneFileIO.writeDataFile(outputBoardFile, boardStrings)

        val testBoards = AbaloneFileIO.readBoardsString(
            AbaloneFileIO.readDataFile("examples/$testname.board")
        )
        println("Test board count: ${testBoards.size}")
        val generatedBoards = AbaloneFileIO.readBoardsString(boardStrings)
        println("Generated board count: ${generatedBoards.size}")
        assertEquals(testBoards.size, generatedBoards.size)

        val testBoardsUsed: HashSet<BoardState> = HashSet()
        for (board in generatedBoards) {
            var boardMatch: BoardState? = null

            for (testBoard in testBoards) {
                var allEqual = true
                letter@ for (letter in LetterCoordinate.entries.drop(1)) {
                    for (number in letter.min .. letter.max) {
                        val coord = Coordinate.get(letter, number)
                        if (board.cells[coord] != testBoard.cells[coord]) {
                            allEqual = false
                            break@letter
                        }
                    }
                }

                if (allEqual) {
                    boardMatch = testBoard
                    testBoardsUsed.add(testBoard)
                }
            }

            assertNotNull(boardMatch, "A board was generated that is not present" +
                    " in the reference file.\n${board.toStringPretty()}")
        }
        assertEquals(testBoards.size, testBoardsUsed.size, "Not all test boards were generated.")
        println("All boards are equal for $testname.")
        println("------------------------------------------------------------")
    }
}