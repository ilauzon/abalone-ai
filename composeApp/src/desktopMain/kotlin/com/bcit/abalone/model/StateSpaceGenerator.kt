/**
 * Houses the state space generator.
 */

package com.bcit.abalone.model

import com.bcit.abalone.Piece

class StateSpaceGenerator {
   companion object {
       /**
        * The __Actions__ available to the agent from a given state.
        *
        * Defined formally in the text as
        * `Actions(s)` returning a finite set of actions that can be executed in `s`.
        *
        * Implementation:
        *  1. put all current-player nodes that have at least one empty/opponent space as an immediate
        *  neighbour in a list P.
        *  2. for each node n in P, add all actions leading to moving n to an empty space to
        *  a list of actions A.
        *  3. for each node n in P, look at the current-player node neighbours, but only the ones that
        *  have a smaller hash value than n's. Add all empty-node moves to A. Look at the immediate
        *  opponent-node neighbours, and see if the next node in the line formed by those three nodes
        *  is empty. If so, add to A.
        *  4. for each node n in P, extend each two-node line that was blocked by a line of two
        *  opponent nodes, if there is another current-player node in that extension. If there is
        *  only a line of two opponent nodes there, add that action to A.
        *  5. return A.
        *
        * @param state the state representation.
        * @return a set of __valid__ actions to take from the given state.
        */
       fun actions(state: StateRepresentation): Set<Action> {
           val board = state.board.cells
           val actions = mutableSetOf<Action>()

           // filter player nodes
           val playerCoordinates = board.keys.filter { board[it] == state.currentPlayer }

           // TODO benchmark filtering out player nodes with no available move spaces beforehand.

           // add unary actions
           val unaryActions = mutableSetOf<Action>()
           for (coord in playerCoordinates) {
               for ((adjCoord, direction) in coord.adjacentCoordinates()) {
                   if (board[adjCoord] == Piece.Empty) { // if that cell is empty
                       unaryActions.add(Action(setOf(adjCoord), direction))
                   }
               }
           }
           actions.addAll(unaryActions)

           // add non-sumito binary and ternary actions
           val inlineBinaryActions = mutableSetOf<Action>()
           val crossBinaryActions = mutableSetOf<Action>()
           val inlineTernaryActions = mutableSetOf<Action>()
           val crossTernaryActions = mutableSetOf<Action>()
           for (action in unaryActions) {
               val actionCoordinate = action.coordinates.first()
               val inlineCoordinate = actionCoordinate.move(action.direction.opposite())
               if (board[inlineCoordinate] == state.currentPlayer) {
                   inlineBinaryActions.add(Action(
                       setOf(actionCoordinate, inlineCoordinate),
                       action.direction
                   ))

                   // for ternary actions
                   val backInlineCoordinate = inlineCoordinate.move(action.direction.opposite())
                   if (board[backInlineCoordinate] == state.currentPlayer) {
                       inlineTernaryActions.add(Action(
                           setOf(actionCoordinate, inlineCoordinate, backInlineCoordinate),
                           action.direction
                       ))
                   }
               }
               val crossDirections = MoveDirection.entries.filter {
                   it != action.direction && it != action.direction.opposite()
               }
               for (crossDirection in crossDirections) {
                   val crossCoordinate = actionCoordinate.move(crossDirection)

                   /*
                       the hashcode is checked to prevent duplicate sets caused by checking cross-
                       neighbours from different coordinates in the same set. This works because
                       hash1 > hash2 and hash2 < hash1 occur in all the same conditions, and that
                       all hashes of coordinates should be unique.
                    */
                   if (
                       board[crossCoordinate] == state.currentPlayer
                       && actionCoordinate.hashCode() > crossCoordinate.hashCode()
                       && board[crossCoordinate.move(action.direction)] == Piece.Empty
                   ) {
                       crossBinaryActions.add(Action(
                           setOf(actionCoordinate, crossCoordinate),
                           action.direction
                       ))

                       // for ternary actions
                       val thirdCrossCoordinate = crossCoordinate.move(crossDirection)
                       if (
                           board[thirdCrossCoordinate] == state.currentPlayer
                           && board[thirdCrossCoordinate.move(action.direction)] == Piece.Empty
                       ) {
                           crossTernaryActions.add(Action(
                               setOf(actionCoordinate, crossCoordinate, thirdCrossCoordinate),
                               action.direction
                           ))
                       }
                   }
               }
           }
           actions.addAll(inlineBinaryActions)
           actions.addAll(crossBinaryActions)
           actions.addAll(inlineTernaryActions)
           actions.addAll(crossTernaryActions)

           // add sumito binary actions
           val sumitoBinaryActions = mutableSetOf<Action>()
           val sumitoTernaryActions = mutableSetOf<Action>()
           for (coord in playerCoordinates) {
               for ((adjCoord, direction) in coord.adjacentCoordinates()) {
                   var coordBehind = coord.move(direction.opposite())
                   var coordAhead = adjCoord.move(direction)
                   // moving into the coordinate is a valid binary sumito if:
                   if (
                       // the adjacent piece is an opponent piece
                       board[adjCoord] !in listOf(Piece.OffBoard, Piece.Empty, state.currentPlayer)
                       // the current piece has a piece of the current player's colour behind it
                       && board[coordBehind] == state.currentPlayer
                   ) {
                       if (board[coordAhead] in listOf(Piece.Empty, Piece.OffBoard)) {
                           sumitoBinaryActions.add(Action(
                               setOf(coordBehind, coord, adjCoord),
                               direction
                           ))
                       }

                       // ternary sumitos
                       val thirdCoordBehind = coordBehind.move(direction.opposite())
                       val thirdCoordAhead = coordAhead.move(direction)
                       if (
                           board[coordAhead] !in listOf(Piece.OffBoard, Piece.Empty, state.currentPlayer)
                           && board[thirdCoordBehind] == state.currentPlayer
                           && board[thirdCoordAhead] in listOf(Piece.Empty, Piece.OffBoard)
                       ) {
                           sumitoTernaryActions.add(Action(
                               setOf(thirdCoordBehind, coordBehind, coord, adjCoord, thirdCoordAhead),
                               direction
                           ))
                       }
                   }
               }
           }
           actions.addAll(sumitoBinaryActions)
           actions.addAll(sumitoTernaryActions)

           return actions
       }

       /**
        * The __Transition Model__.
        *
        * Defined formally in the text as
        * `Result(s,a)` returning the state that results from doing action `a` in state `s`.
        *
        * @param state the state being transitioned from.
        * @param action the action being taken upon the state.
        * @return the state that was transitioned to.
        */
       fun result(state: StateRepresentation, action: Action): StateRepresentation {
           // TODO define the result() function.
           throw NotImplementedError()
       }

       /**
        * The __Goal Test__.
        *
        * @param state the state being tested.
        * @return true if the state is a goal state, false otherwise.
        */
       fun goal(state: StateRepresentation): Boolean {
          // TODO define the goal test function.
           throw NotImplementedError()
       }
   }
}

/**
 * Represents an action available to the agent.
 *
 * @property coordinates the coordinates of the marbles to move.
 * @property direction the direction to move the marbles in.
 */
data class Action(val coordinates: Set<Coordinate>, val direction: MoveDirection)