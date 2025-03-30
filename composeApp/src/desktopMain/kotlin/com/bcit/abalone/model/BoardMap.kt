package com.bcit.abalone.model

import com.bcit.abalone.Piece
import kotlin.random.Random
import com.bcit.abalone.model.LetterCoordinate as L
import com.bcit.abalone.model.NumberCoordinate as N

/**
 * A map to be used only for the Abalone board. Contains only get and set methods
 * for the fixed number of Coordinates to be used as keys. This avoids dealing with nullable
 * types when accessing map values via a key.
 *
 */
class BoardMap : Map<Coordinate, Piece> {

    companion object {
        val middle = Coordinate.get(L.E, N.FIVE)
        private val setupKeys: MutableSet<Coordinate> = mutableSetOf()
        private val refKeys: Set<Coordinate>
        private val zobristTable: IntArray = IntArray(61 * 3)
        private val pieceMapping = arrayOf(
            Piece.Empty,
            Piece.Black,
            Piece.White,
            Piece.OffBoard,
        )


        init {
            var counter = 0
            for (l: L in L.entries.drop(1)) {
                for (n: N in N.entries.slice(l.min.ordinal..l.max.ordinal)) {
                    val coord = Coordinate.get(l, n)
                    setupKeys.add(coord)
                    counter++
                }
            }
            if (counter != 61) throw Exception("Invalid number of pieces generated.")
            setupKeys.add(Coordinate.offBoard)
            refKeys = setupKeys.toSet()

            initZobrist()
        }

        private fun initZobrist() {
            val random = Random(0)
            for (i in 0 until 61) {
                for (j in 0 .. 2) {
                    zobristTable[i * 3 + j] = random.nextInt()
                }
            }
        }
    }

    override val size: Int = 62
    override val keys: Set<Coordinate> = refKeys
    private val pieceMap: ByteArray

    constructor() {
       pieceMap = ByteArray( L.I.ordinal * 16 + N.NINE.ordinal + 1 )
    }

    /**
     * Constructs a BoardMap using a ByteArray. For testing purposes only when used outside of the class!
     */
    constructor(pieceMap: ByteArray) {
        this.pieceMap = pieceMap.clone()
    }

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

    override operator fun get(key: Coordinate): Piece {
        val offset = key.hashCode()
        val piece = pieceMap[offset].toInt()
        return pieceMapping[piece]
    }

    operator fun set(key: Coordinate, value: Piece) {
        val offset = key.hashCode()
        pieceMap[offset] = value.ordinal.toByte()
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

        pieceMap.forEachIndexed { index, item ->
            if (other.pieceMap[index] != item) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var hash = 0
        for (i in 0 until 61) {
            hash = hash xor zobristTable[i * 3 + pieceMap[i]]
        }
        return hash
    }

    override fun isEmpty(): Boolean {
        return false
    }

    fun clone(): BoardMap = BoardMap(pieceMap)
}