package com.bcit.abalone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bcit.abalone.model.Action
import com.bcit.abalone.model.BoardState
import com.bcit.abalone.model.Coordinate
import com.bcit.abalone.model.LetterCoordinate
import com.bcit.abalone.model.NumberCoordinate
import com.bcit.abalone.model.Player
import com.bcit.abalone.model.StateRepresentation
import com.bcit.abalone.model.StateSpaceGenerator
import java.io.File
import java.nio.file.Paths

const val MAX_PIECES = 14
const val SAMPLE_MOVE_TIME = 30
const val BLACK_MOVE_REMAINING = 30
const val WHITE_MOVE_REMAINING = 31

@Composable
fun StateSpaceGenerator(){
    val files:SnapshotStateList<String?> = getAllFiles()
    var fileNameDropdownInput by remember{ mutableStateOf("")}
    var fileName by remember{ mutableStateOf("")}
    var isFileLoaded by remember{ mutableStateOf(false)}
    Box(modifier = Modifier.safeContentPadding()) {

        Column {
            Text("State Space Generator", style = MaterialTheme.typography.h3)

            Text("To generate all possible moves and board configurations, please select" +
                    " a file below and press Enter. If you don't see a file here that you'd like" +
                    " to test, please move that file into the same folder as the executable, then" +
                    " rerun the executable.",
                style = MaterialTheme.typography.body1)
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                LazyColumn {
                    items(files.size) {
                        Box{
                            val text = if (files[it] == null) "" else files[it]
                            if (text != null) {
                                Box(modifier = Modifier
                                    .safeContentPadding()
                                    .clickable {
                                    fileNameDropdownInput = text
                                }) {
                                    Text(text, style = MaterialTheme.typography.body1)
                                }
                            }
                        }
                    }
                }
            }
            Button(onClick = {
                fileName = fileNameDropdownInput
                isFileLoaded = true
            }) {
                Text("Enter")
            }
            Column(modifier = Modifier.safeContentPadding()){
                if (isFileLoaded and fileName.isNotEmpty()) {
                    // create output file names
                    val outputMoveFile = "${fileName.split(".")[0]}.move"
                    val outputBoardFile = "${fileName.split(".")[0]}.board"

                    // read the file input
                    val text = readDataFile(fileName)

                    //parse board from file
                    val boardState = parseState(text[0], text[1])

                    // feed board into StateSpaceGenerator
                    val boards:MutableList<StateRepresentation> = mutableListOf()
                    val actions: List<Action> = StateSpaceGenerator.actions(boardState).toList()
                    for (action in actions) {
                        boards.add(StateSpaceGenerator.result(boardState, action))
                    }

                    // turn actions and boards into strings for file output
                    val actionsStrings = stringifyActions(actions)
                    val boardStrings = stringifyBoards(boards)

                    // write output to respective files
                    writeDataFile(outputMoveFile, actionsStrings)
                    writeDataFile(outputBoardFile, boardStrings)
                }
            }
        }
    }
}

/**
 * Writes the given list of states to a text file.
 *
 * @param path a string
 * @param outputList a List<String>
 */
fun writeDataFile(path: String, outputList: List<String>) {
    File(path).writeText(outputList.joinToString("\n"))
}

/**
 * Reads the given text file and returns the contents.
 *
 * @param path a string
 * @return file contents as a string
 */
fun readDataFile(path: String): List<String>{
    val text = File(path).readLines()
    return text
}

/**
 * Parses the given board as a string into a StateRepresentation object.
 *
 * @param board as a string
 * @return board as a StateRepresentation object
 */
fun parseState(currentPlayer: String, board: String): StateRepresentation{
    val marbleLayout: MutableMap<Coordinate, Piece> = mutableMapOf()
    val boardList: List<String> = board.split(",")
    var blackPieceCount = 0
    var whitePieceCount = 0
    for (cell in boardList) {
        val coordinate = Coordinate(
            LetterCoordinate.convertLetter(cell.substring(0,1)),
            NumberCoordinate.convertNumber(cell.substring(1,2))
        )
        val piece: Piece = Piece.convertPiece(cell.substring(2))
        if (piece == Piece.Black) blackPieceCount++
        if (piece == Piece.White) whitePieceCount++
        marbleLayout[coordinate] = piece
    }
    val marbleLayoutMap: Map<Coordinate, Piece> = marbleLayout.toMap()
    val boardState = BoardState(marbleLayoutMap)
    val blackPlayer = Player(MAX_PIECES - blackPieceCount, SAMPLE_MOVE_TIME)
    val whitePlayer = Player(MAX_PIECES - whitePieceCount, SAMPLE_MOVE_TIME)
    val players = mapOf(Piece.Black to blackPlayer, Piece.White to whitePlayer)
    val currentPlayerPiece = if (currentPlayer == "b") Piece.Black else Piece.White
    val movesRemaining = if (currentPlayer == "b") BLACK_MOVE_REMAINING else WHITE_MOVE_REMAINING
    return StateRepresentation(boardState, players, movesRemaining, currentPlayerPiece)
}

/**
 * Returns the given board as a string
 *
 * @param board a StateRepresentation object
 * @return board as a string
 */
fun stringifyBoard(board: StateRepresentation): String {
    val boardList: MutableList<String> = mutableListOf()
    val currentBoard = board.getBoardState()
    val cells = currentBoard.cells
    cells.forEach { (coordinate, piece) ->
        if (piece != Piece.Empty && coordinate != Coordinate.offBoard) {
            boardList.add("$coordinate$piece")
        }
    }
    boardList.sort()
    val boardString = boardList.joinToString(",")
    return boardString
}

/**
 * Returns the given list of boards as a list of strings.
 *
 * @param boards List<StateRepresentation>
 * @return boards as a List<String>
 */
fun stringifyBoards(boards: List<StateRepresentation>): List<String> {
    val stringBoards: MutableList<String> = mutableListOf()
    boards.forEach {
        stringBoards.add(stringifyBoard(it))
    }
    return stringBoards
}

/**
 * Returns the action as a string
 *
 * @param action an Action object
 * @return action as a string
 */
fun stringifyAction(action: Action): String {
    return "${action.coordinates} ${action.direction}"
}

/**
 * Returns the given list of actions as a list of strings.
 *
 * @param actions List<Action>
 * @return actions as a List<String>
 */
fun stringifyActions(actions: List<Action>): List<String> {
    val stringActions: MutableList<String> = mutableListOf()
    actions.forEach {
        stringActions.add(stringifyAction(it))
    }
    return stringActions
}

/**
 * Returns a list of all the .input files in the current directory.
 *
 * @return .input files as a SnapShotStateList<String> object
 */
fun getAllFiles(): SnapshotStateList<String?> {
    val path = Paths.get("").toAbsolutePath().toString()
    val files = File(path).listFiles()
    val fileNames = arrayOfNulls<String>(files?.size ?: 0 )
    files?.mapIndexed { index, item ->
        fileNames[index] = item?.name
    }
    fileNames.filterNotNull()
    return fileNames.filter { it!!.contains(".input") }.toMutableList().toMutableStateList()
}
