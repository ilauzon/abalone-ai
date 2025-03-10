/**
 * Houses the Coordinate, VerticalCoordinate, and HorizontalCoordinate classes, which all
 * manage the coordinate system of marbles.
 */
package com.bcit.abalone.model

import com.bcit.abalone.Piece
import java.lang.IllegalArgumentException

class Coordinate private constructor(val letter: LetterCoordinate, val number: NumberCoordinate) {

    companion object {
        val offBoard = Coordinate(LetterCoordinate.NULL, NumberCoordinate.NULL)
        private val coordinates: HashMap<Int, Coordinate> = HashMap()

        init {
            for (l: LetterCoordinate in LetterCoordinate.entries) {
                for (n: NumberCoordinate in l.min .. l.max) {
                    coordinates[hash(l, n)] = Coordinate(l, n)
                }
            }
        }

        /**
         * Returns the Coordinate instance with the given letter and number.
         *
         * @param letter the letter coordinate.
         * @param number the number coordinate.
         * @return an existing Coordinate instance.
         */
        fun get(letter: LetterCoordinate, number: NumberCoordinate): Coordinate {
            if (number < letter.min || number > letter.max) {
                throw IllegalArgumentException("Coordinate $letter$number does not exist on the board")
            }
            return coordinates[hash(letter, number)]!!
        }

        /**
         * Returns a hash value of a letter and number.
         *
         * @param letter the letter coordinate.
         * @param number the number coordinate.
         * @return the hash value.
         */
        private fun hash(letter: LetterCoordinate, number: NumberCoordinate): Int {
            return letter.ordinal * 16 + number.ordinal
        }
    }

    /**
     * Provides the adjacent coordinates, i.e. the immediate neighbours of the coordinate.
     *
     * @return adjacent coordinates.
     */
    fun adjacentCoordinates(): Array<Pair<Coordinate, MoveDirection>> {
        val adjacent = listOf(
            move(MoveDirection.PosX) to MoveDirection.PosX,
            move(MoveDirection.NegX) to MoveDirection.NegX,
            move(MoveDirection.PosY) to MoveDirection.PosY,
            move(MoveDirection.NegY) to MoveDirection.NegY,
            move(MoveDirection.PosZ) to MoveDirection.PosZ,
            move(MoveDirection.NegZ) to MoveDirection.NegZ,
        )
        val onBoard = adjacent.filter { it.first != offBoard }
        return onBoard.toTypedArray()
    }

    fun move(direction: MoveDirection): Coordinate {
        val newCoordinate = when(direction) {
            MoveDirection.PosX -> moveX(1)
            MoveDirection.NegX -> moveX(-1)
            MoveDirection.PosY -> moveY(1)
            MoveDirection.NegY -> moveY(-1)
            MoveDirection.PosZ -> moveZ(1)
            MoveDirection.NegZ -> moveZ(-1)
        }
        return newCoordinate
    }

    private fun moveX(amount: Int): Coordinate {
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

    private fun moveY(amount: Int): Coordinate {
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

    private fun moveZ(amount: Int): Coordinate {
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

    override fun toString(): String = "$letter$number"

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Coordinate) return false
        return other.letter == letter && other.number == number
    }

    override fun hashCode(): Int {
        return hash(letter, number)
    }
}

enum class NumberCoordinate {
    NULL, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

    val min: LetterCoordinate get() = minMaxMapping[this]!!.first
    val max: LetterCoordinate get() = minMaxMapping[this]!!.second
    companion object {
        private val minMaxMapping = mapOf(
            NULL to Pair(LetterCoordinate.NULL, LetterCoordinate.NULL),
            ONE to Pair(LetterCoordinate.A, LetterCoordinate.E),
            TWO to Pair(LetterCoordinate.A,LetterCoordinate.F),
            THREE to Pair(LetterCoordinate.A,LetterCoordinate.G),
            FOUR to Pair(LetterCoordinate.A,LetterCoordinate.H),
            FIVE to Pair(LetterCoordinate.A,LetterCoordinate.I),
            SIX to Pair(LetterCoordinate.B,LetterCoordinate.I),
            SEVEN to Pair(LetterCoordinate.C,LetterCoordinate.I),
            EIGHT to Pair(LetterCoordinate.D,LetterCoordinate.I),
            NINE to Pair(LetterCoordinate.E, LetterCoordinate.I),
        )

        fun convertNumber(letter: String): NumberCoordinate {
            val numberCoordinate = when (letter) {
                "1" -> ONE
                "2" -> TWO
                "3" -> THREE
                "4" -> FOUR
                "5" -> FIVE
                "6" -> SIX
                "7" -> SEVEN
                "8" -> EIGHT
                "9" -> NINE
                else -> NULL
            }
            return numberCoordinate
        }
    }

    operator fun plus(amount: Int): NumberCoordinate {
        val sum = ordinal + amount
        if (sum < ONE.ordinal || sum > NINE.ordinal) return NULL
        return entries[sum]
    }

    operator fun minus(amount: Int): NumberCoordinate {
        return plus(-amount)
    }

    operator fun rangeTo(that: NumberCoordinate): Iterable<NumberCoordinate> {
        val newRange = (this.ordinal .. that.ordinal).map { enumValues<NumberCoordinate>()[it] }
        return newRange.asIterable()
    }

    infix fun downTo(that: NumberCoordinate): Iterable<NumberCoordinate> {
        val newRange = (this.ordinal downTo  that.ordinal).map { enumValues<NumberCoordinate>()[it] }
        return newRange.asIterable()
    }

    override fun toString(): String = ordinal.toString()
}

enum class LetterCoordinate {
    NULL, A, B, C, D, E, F, G, H, I;

    val min: NumberCoordinate get() = minMaxMapping[this]!!.first
    val max: NumberCoordinate get() = minMaxMapping[this]!!.second
    companion object {
        private val minMaxMapping = mapOf(
            NULL to Pair(NumberCoordinate.NULL, NumberCoordinate.NULL),
            A to Pair(NumberCoordinate.ONE, NumberCoordinate.FIVE),
            B to Pair(NumberCoordinate.ONE, NumberCoordinate.SIX),
            C to Pair(NumberCoordinate.ONE, NumberCoordinate.SEVEN),
            D to Pair(NumberCoordinate.ONE, NumberCoordinate.EIGHT),
            E to Pair(NumberCoordinate.ONE, NumberCoordinate.NINE),
            F to Pair(NumberCoordinate.TWO, NumberCoordinate.NINE),
            G to Pair(NumberCoordinate.THREE, NumberCoordinate.NINE),
            H to Pair(NumberCoordinate.FOUR, NumberCoordinate.NINE),
            I to Pair(NumberCoordinate.FIVE, NumberCoordinate.NINE),
        )
        fun convertLetter(letter: String): LetterCoordinate {
            val letterCoordinate = when (letter) {
                "A" -> A
                "B" -> B
                "C" -> C
                "D" -> D
                "E" -> E
                "F" -> F
                "G" -> G
                "H" -> H
                "I" -> I
                else -> NULL
            }
            return letterCoordinate
        }
    }

    operator fun plus(amount: Int): LetterCoordinate {
        val sum = ordinal + amount
        if (sum < A.ordinal || sum > I.ordinal) return NULL
        return entries[sum]
    }

    operator fun minus(amount: Int): LetterCoordinate {
        return plus(-amount)
    }

    operator fun rangeTo(that: LetterCoordinate): Iterable<LetterCoordinate> {
        val newRange = (this.ordinal .. that.ordinal).map { enumValues<LetterCoordinate>()[it] }
        return newRange.asIterable()
    }

    infix fun downTo(that: LetterCoordinate): Iterable<LetterCoordinate> {
        val newRange = (this.ordinal downTo  that.ordinal).map { enumValues<LetterCoordinate>()[it] }
        return newRange.asIterable()
    }

    override fun toString(): String = name
}

