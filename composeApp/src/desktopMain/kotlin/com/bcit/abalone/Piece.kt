package com.bcit.abalone

enum class Piece {
    Empty, Black, White, OffBoard;
    fun opposite() = when(this) {
        Empty -> Empty
        Black -> White
        White -> Black
        OffBoard -> OffBoard
    }
}