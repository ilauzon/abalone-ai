package com.bcit.abalone.model

import com.bcit.abalone.Piece
import com.bcit.abalone.model.LetterCoordinate as L
import com.bcit.abalone.model.NumberCoordinate as N

/**
 * A map to be used only for the Abalone board. Contains only get and set methods
 * for the fixed number of Coordinates to be used as keys. This avoids dealing with nullable
 * types when accessing map values via a key.
 *
 */
class BoardMap(
    override val size: Int = 62,
    override val keys: Set<Coordinate> = refKeys,
) : Map<Coordinate, Piece> {

    override val values: Collection<Piece>
        get() {
            val vals: MutableCollection<Piece> = mutableListOf()
            for (key in keys) {
                vals.add(this[key])
            }
            return vals.toList()
        }

    override val entries: Set<Map.Entry<Coordinate, Piece>>
        get() {
            val kvPairs: MutableMap<Coordinate, Piece> = mutableMapOf()
            for (key in keys) {
                kvPairs[key] = this[key]
            }
            return kvPairs.entries
        }

    /**
     * Two longs, with two bits representing each piece for 4 states / piece.
     */
    private val pieces: Array<Long> = arrayOf(0L, 0L)

    companion object {
        private val coordMapping: HashMap<Coordinate, Int> = hashMapOf()
        private val setupKeys: MutableSet<Coordinate> = mutableSetOf()
        private val refKeys: Set<Coordinate>

        init {
            var counter = 0
            for (l: L in L.entries.drop(1)) {
                for (n: N in N.entries.slice(l.min.ordinal..l.max.ordinal)) {
                    val coord = Coordinate.get(l, n)
                    setupKeys.add(coord)
                    coordMapping[coord] = counter
                    counter += 2
                }
            }
            if (counter != 61 * 2) throw Exception("Invalid number of pieces generated.")
            setupKeys.add(Coordinate.offBoard)
            coordMapping[Coordinate.offBoard] = counter
            refKeys = setupKeys.toSet()
        }
    }

    override operator fun get(key: Coordinate): Piece {
        var offset = coordMapping[key]!!
        val mem = if (offset < 64) {
                pieces[0]
            } else {
                offset -= 64
                pieces[1]
        }

        val piece: Long = (mem and (0b11L shl offset)) ushr (offset)
        val pieceInt = piece.toInt()
        return Piece.entries[pieceInt]
    }

    operator fun set(key: Coordinate, value: Piece) {
        var offset = coordMapping[key]!!
        var longOffset = 0
        val mem = if (offset < 64) {
            pieces[0]
        } else {
            offset -= 64
            longOffset = 1
            pieces[1]
        }

        val piece: Long = value.ordinal.toLong()
        val newLong = mem or (piece shl offset)
        pieces[longOffset] = newLong
    }

    override fun containsKey(key: Coordinate): Boolean {
        return true
    }

    override fun containsValue(value: Piece): Boolean {
        for (key in keys) {
            if (this[key] == value) return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoardMap) return false

        return this.pieces[0] == other.pieces[0] && this.pieces[1] == other.pieces[1]
    }

    override fun hashCode(): Int {
        return pieces.contentHashCode()
    }

    override fun isEmpty(): Boolean {
        return false
    }
}