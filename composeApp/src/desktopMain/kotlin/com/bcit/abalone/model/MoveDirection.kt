package com.bcit.abalone.model

/**
 * Represents all possible directions of movement.
 *
 */
enum class MoveDirection {
    PosX, NegX, PosY, NegY, PosZ, NegZ
    ;

    fun opposite(): MoveDirection {
        val ord = ordinal
        val oppositeOrd = if (ordinal % 2 == 0) ord + 1 else ord - 1
        return MoveDirection.entries[oppositeOrd]
    }
}
