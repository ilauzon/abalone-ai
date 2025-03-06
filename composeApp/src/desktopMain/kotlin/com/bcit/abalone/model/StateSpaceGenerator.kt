/**
 * Houses the state space generator.
 */

package com.bcit.abalone.model

class StateSpaceGenerator {
   companion object {
       /**
        * The __Actions__ available to the agent from a given state.
        *
        * Defined formally in the text as
        * `Actions(s)` returning a finite set of actions that can be executed in `s`.
        *
        * @param state the state representation.
        * @return a set of __valid__ actions to take from the given state.
        */
       fun actions(state: StateRepresentation): Set<Action> {
           // TODO define the actions() function.
           throw NotImplementedError()
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