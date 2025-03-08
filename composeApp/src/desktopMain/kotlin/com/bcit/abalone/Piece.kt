package com.bcit.abalone

enum class Piece {
    Empty, Blue, Red, OffBoard;
    fun opposite() = when(this) {
        Empty -> Empty
        Blue -> Red
        Red -> Blue
        OffBoard -> OffBoard
    }
}