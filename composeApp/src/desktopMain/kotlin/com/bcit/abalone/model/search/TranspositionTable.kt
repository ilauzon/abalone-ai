package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.Action
import com.bcit.abalone.model.BoardMap
import java.util.LinkedHashMap
import kotlin.random.Random

/**
 * A least-recently-used cache to be used as a transposition table.
 *
 * Source: https://medium.com/@fasilt/understanding-lru-least-recently-used-cache-in-kotlin-b54c7060e752
 */
class TranspositionTable (
    private val capacity: Int
) : LinkedHashMap<TranspositionTable.Key, TranspositionTable.Entry>(
    capacity,
    0.75f,
    true
) {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Key, Entry>?): Boolean {
        return size > capacity
    }

    data class Key(
        val board: BoardMap,
        val move: Piece,
    ) {
        companion object {
            private val turnHash = IntArray(4)
            private val generatedHashCodes: HashMap<Int, Key> = hashMapOf()
            var collisions: Int = 0

            init {
                val random = Random(1)
                turnHash[0] = random.nextInt()
                turnHash[1] = random.nextInt()
                turnHash[2] = random.nextInt()
                turnHash[3] = random.nextInt()
            }
        }

        override fun hashCode(): Int {
            val hash = board.hashCode() xor turnHash[move.ordinal]
            if (generatedHashCodes[hash] != null && generatedHashCodes[hash] != this) {
                collisions++
            } else {
                generatedHashCodes[hash] = this
            }
            return hash
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Key) return false
            return board == other.board && move == other.move
        }
    }

    data class Entry(
        val value: Double,
        val action: Action,
        val depth: Int,
        )
}
