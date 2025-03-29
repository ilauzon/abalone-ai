package com.bcit.abalone.model.search

import com.bcit.abalone.Piece
import com.bcit.abalone.model.Action
import com.bcit.abalone.model.BoardMap
import com.bcit.abalone.model.BoardState
import java.util.LinkedHashMap

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
        val currentPlayer: BoardMap,
        val move: Piece,
    )

    data class Entry(
        val value: Double,
        val action: Action,
        val depth: Int,
        )
}
