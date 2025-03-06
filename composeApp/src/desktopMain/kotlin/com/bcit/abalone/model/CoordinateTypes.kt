/**
 * Houses the Coordinate, VerticalCoordinate, and HorizontalCoordinate classes, which all
 * manage the coordinate system of marbles.
 */
package com.bcit.abalone.model

class Coordinate(
    val letter: LetterCoordinate,
    val number: NumberCoordinate
) {

    companion object {
        val offBoard = Coordinate(LetterCoordinate.NULL, NumberCoordinate.NULL)
    }

    fun moveX(amount: Int): Coordinate {
        val newCoordinate = Coordinate(letter, number + amount)
        if (
            newCoordinate.number == NumberCoordinate.NULL
            || newCoordinate.letter < newCoordinate.number.min
            || newCoordinate.letter > newCoordinate.number.max
        ) {
            return offBoard
        }
        return newCoordinate
    }

    fun moveY(amount: Int): Coordinate {
        val newCoordinate = Coordinate(letter + amount, number)
        if (
            newCoordinate.letter == LetterCoordinate.NULL
            || newCoordinate.number < newCoordinate.letter.min
            || newCoordinate.number > newCoordinate.letter.max
        ) {
            return offBoard
        }
        return newCoordinate
    }

    fun moveZ(amount: Int): Coordinate {
        val newCoordinate = Coordinate(letter + amount, number + amount)
        if (
            newCoordinate.letter == LetterCoordinate.NULL
            || newCoordinate.number == NumberCoordinate.NULL
            || newCoordinate.letter < newCoordinate.number.min
            || newCoordinate.number < newCoordinate.letter.min
            || newCoordinate.letter > newCoordinate.number.max
            || newCoordinate.number > newCoordinate.letter.max
        ) {
            return offBoard
        }
        return newCoordinate
    }

    override fun toString(): String = "$letter + $number"

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Coordinate) return false
        return other.letter == letter && other.number == number
    }

    override fun hashCode(): Int {
        return letter.ordinal * 16 + number.ordinal
    }
}

enum class NumberCoordinate(
    val min: LetterCoordinate,
    val max: LetterCoordinate
) {
    NULL(LetterCoordinate.NULL,LetterCoordinate.NULL),
    ONE(LetterCoordinate.A,LetterCoordinate.E),
    TWO(LetterCoordinate.A,LetterCoordinate.F),
    THREE(LetterCoordinate.A,LetterCoordinate.G),
    FOUR(LetterCoordinate.A,LetterCoordinate.H),
    FIVE(LetterCoordinate.A,LetterCoordinate.I),
    SIX(LetterCoordinate.B,LetterCoordinate.I),
    SEVEN(LetterCoordinate.C,LetterCoordinate.I),
    EIGHT(LetterCoordinate.D,LetterCoordinate.I),
    NINE(LetterCoordinate.E,LetterCoordinate.I);

    operator fun plus(amount: Int): NumberCoordinate {
        val sum = ordinal + amount
        if (sum < ONE.ordinal || sum > NINE.ordinal) return NULL
        return entries[sum];
    }

    operator fun minus(amount: Int): NumberCoordinate {
        return plus(-amount)
    }

    override fun toString(): String = ordinal.toString()
}

enum class LetterCoordinate(
    val min: NumberCoordinate,
    val max: NumberCoordinate
) {
    NULL(NumberCoordinate.NULL, NumberCoordinate.NULL),
    A(NumberCoordinate.ONE, NumberCoordinate.FIVE),
    B(NumberCoordinate.ONE, NumberCoordinate.SIX),
    C(NumberCoordinate.ONE, NumberCoordinate.SEVEN),
    D(NumberCoordinate.ONE, NumberCoordinate.EIGHT),
    E(NumberCoordinate.ONE, NumberCoordinate.NINE),
    F(NumberCoordinate.TWO, NumberCoordinate.NINE),
    G(NumberCoordinate.THREE, NumberCoordinate.NINE),
    H(NumberCoordinate.FOUR, NumberCoordinate.NINE),
    I(NumberCoordinate.FIVE, NumberCoordinate.NINE);

    operator fun plus(amount: Int): LetterCoordinate {
        val sum = ordinal + amount
        if (sum < A.ordinal || sum > I.ordinal) return NULL
        return entries[sum];
    }

    operator fun minus(amount: Int): LetterCoordinate {
        return plus(-amount)
    }

    override fun toString(): String = name
}

