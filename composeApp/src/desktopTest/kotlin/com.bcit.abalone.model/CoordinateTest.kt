package com.bcit.abalone.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CoordinateTest {

    @Test
    fun testCoordinateHashCode() {
        var count = 0
        var collisions = 0
        val generatedHashCodes = mutableSetOf<Int>()
        for (letter in LetterCoordinate.entries) {
            for (number in letter.min .. letter.max) {
                count++
                val hash = Coordinate.get(letter, number).hashCode()
//                println("$letter$number\t$hash")
                if (generatedHashCodes.contains(hash)) {
                    collisions++
                } else {
                    generatedHashCodes.add(hash)
                }
            }
        }
        assertEquals(collisions, 0)
        assertEquals(count, 61 + 1)
        println("Collisions: $collisions")
        println("Count: $count")
    }
}