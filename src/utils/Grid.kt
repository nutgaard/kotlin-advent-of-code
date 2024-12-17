package utils

import java.util.*


class SparseGrid<T> {
    val grid: MutableMap<Long, MutableMap<Long, T>> = mutableMapOf()
    val rowDomain = MutableMinMax(0, 0)
    val columnComain = MutableMinMax(0, 0)

    fun set(coordinate: Coordinate, value: T) {
        grid.computeIfAbsent(coordinate.row) { mutableMapOf() }[coordinate.column] = value
        rowDomain.update(coordinate.row)
        columnComain.update(coordinate.column)
    }

    fun get(coordinate: Coordinate): T? {
        return grid[coordinate.row]?.get(coordinate.column)
    }

    fun <TAcc> reduce(initialValue: TAcc, fn: (acc: TAcc, value: T, coordinate: Coordinate) -> TAcc): TAcc {
        var acc = initialValue
        for (rowKey in grid.keys) {
            val row = grid[rowKey]!!
            for (columnKey in row.keys) {
                val value = row[columnKey]!!
                acc = fn(acc, value, Coordinate.of(rowKey, columnKey))
            }
        }
        return acc
    }
}

inline fun <reified T> SparseGrid<T>.toArrayGrid(): Grid<T?> {
    val grid = Array(rowDomain.size().toInt()) { row ->
        Array(columnComain.size().toInt()) { column ->
            grid[row + rowDomain.min]?.get(column + columnComain.min)
        }
    }
    return ArrayGrid(grid)
}

data class Dimension(val height: Int, val width: Int) {
    val area: Int = height * width
}
interface Grid<T> {
    val dimension: Dimension
    val rowIndices: IntRange
    val columnIndices: IntRange

    fun getValue(coordinate: Coordinate): T
    fun getRow(row: Int): Array<T>
    fun setValue(coordinate: Coordinate, value: T)

    fun asString(fn: (Int, Int, T) -> String = { _, _, it -> it.toString() }): String = buildString {
        for (row in 0 until dimension.height) {
            for (column in 0 until dimension.width) {
                append(fn(row, column, getValue(CoordinateImpl(row, column))))
            }
            appendLine()
        }
    }

    fun findCoordinate(predicate: (T) -> Boolean): Coordinate?
    fun flattenToList(): List<T>

    fun copyOf(): Grid<T>

    fun <TAcc> reduce(initialValue: TAcc, fn: (acc: TAcc, value: T, coordinate: Coordinate) -> TAcc): TAcc {
        var acc = initialValue
        for (rowIndex in rowIndices) {
            for (columnIndex in columnIndices) {
                val coordinate = Coordinate.of(rowIndex, columnIndex)
                acc = fn(acc, getValue(coordinate), coordinate)
            }
        }
        return acc
    }

    fun indicies(): Iterable<Coordinate> {
        val dimension = this.dimension
        val grid = this
        return object : Iterable<Coordinate> {
            override fun iterator(): Iterator<Coordinate> {
                var maybeCurrent: Coordinate? = if (dimension.area == 0) null else Coordinate.of(0, 0)
                return object : Iterator<Coordinate> {
                    override fun hasNext(): Boolean {
                        return maybeCurrent != null
                    }

                    override fun next(): Coordinate {
                        val current = requireNotNull(maybeCurrent) { "Iterator has finished" }
                        val candidates = arrayOf(
                            current.move(Direction.RIGHT),
                            Coordinate.of(current.row + 1, 0),
                        )
                        maybeCurrent = candidates.firstOrNull { it withinBoundsOf grid }
                        return current
                    }
                }
            }
        }
    }
}

data class ArrayGrid<T>(val grid: Array<Array<T>>) : Grid<T> {
    override val dimension = Dimension(grid.size, grid.first().size)
    override val rowIndices: IntRange = grid.indices
    override val columnIndices: IntRange = grid.first().indices

    override fun getValue(coordinate: Coordinate): T {
        return grid[coordinate.row.toInt()][coordinate.column.toInt()]
    }

    override fun setValue(coordinate: Coordinate, value: T) {
        grid[coordinate.row.toInt()][coordinate.column.toInt()] = value
    }

    override fun getRow(row: Int): Array<T> {
        return grid[row].copyOf()
    }

    override fun findCoordinate(predicate: (T) -> Boolean): Coordinate? {
        for (row in rowIndices) {
            for (column in columnIndices) {
                if (predicate(grid[row][column])) return Coordinate.of(row, column)
            }
        }
        return null
    }

    override fun flattenToList(): List<T> {
        val list = kotlin.collections.ArrayList<T>(dimension.height * dimension.width)
        for (row in rowIndices) {
            for (column in columnIndices) {
                list.add(grid[row][column])
            }
        }
        return list
    }

    override fun toString(): String = asString()

    override fun copyOf(): ArrayGrid<T> {
        val grid = this.grid.copyOf()
        for (index in grid.indices) {
            grid[index] = grid[index].copyOf()
        }
        return ArrayGrid(grid)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArrayGrid<*>

        return grid.contentDeepEquals(other.grid)
    }

    override fun hashCode(): Int {
        return grid.contentDeepHashCode()
    }

    companion object {
        inline fun <reified T> create(dimension: Dimension, fn: (coordinate: Coordinate) -> T): ArrayGrid<T> {
            val grid = Array(dimension.height) { row ->
                Array(dimension.width) { column ->
                    fn(Coordinate.of(row, column))
                }
            }
            return ArrayGrid(grid)
        }
    }
}

inline fun <reified T, reified R> ArrayGrid<T>.map(transform: (T) -> R): ArrayGrid<R> {
    return this.mapIndexed { _, _, v -> transform(v) }
}

inline fun <reified T, reified R> ArrayGrid<T>.mapIndexed(transform: (row: Int, column: Int, T) -> R): ArrayGrid<R> {
    val grid = Array(dimension.height) { row ->
        Array(dimension.width) { column ->
            transform(row, column, grid[row][column])
        }
    }
    return ArrayGrid(grid)
}

inline fun <reified T> ArrayGrid<T>.transpose(defaultValue: T): ArrayGrid<T> {
    val transposed = Array(dimension.width) {
        Array(dimension.height) {
            defaultValue
        }
    }

    for (i in 0..<dimension.height) {
        for (j in 0..<dimension.width) {
            transposed[j][i] = this.grid[i][j]
        }
    }

    return ArrayGrid(transposed)
}

class BitGrid(val grid: Array<SizeAwareBitSet>) : Grid<Boolean> {
    override val dimension = Dimension(grid.size, grid.first().nbits)
    override val rowIndices: IntRange = grid.indices
    override val columnIndices: IntRange = 0 until grid.first().nbits


    override fun getValue(coordinate: Coordinate): Boolean {
        return grid[coordinate.row.toInt()][coordinate.column.toInt()]
    }

    override fun setValue(coordinate: Coordinate, value: Boolean) {
        grid[coordinate.row.toInt()][coordinate.column.toInt()] = value
    }

    override fun getRow(row: Int): Array<Boolean> {
        val rowBits = grid[row]
        return Array(size = rowBits.nbits) { rowBits.get(it) }
    }

    override fun toString(): String {
        return asString { _, _, it -> if (it) "1" else "0" }
    }

    override fun findCoordinate(predicate: (Boolean) -> Boolean): Coordinate? {
        for (row in rowIndices) {
            for (column in columnIndices) {
                if (predicate(grid[row][column])) return Coordinate.of(row, column)
            }
        }
        return null
    }

    override fun flattenToList(): List<Boolean> {
        val list = kotlin.collections.ArrayList<Boolean>(dimension.height * dimension.width)
        var index = 0
        for (row in rowIndices) {
            for (column in columnIndices) {
                list[index++] = grid[row][column]
            }
        }
        return list
    }

    fun transpose(): BitGrid {
        val transposed = Array(dimension.width) {
            SizeAwareBitSet(dimension.height)
        }
        for (i in 0..<dimension.height) {
            for (j in 0..<dimension.width) {
                transposed[j][i] = grid[i][j]
            }
        }

        return BitGrid(transposed)
    }

    override fun copyOf(): BitGrid {
        TODO("Not yet implemented")
    }
}

class SizeAwareBitSet(val nbits: Int) : BitSet(nbits) {
    override fun clone(): BitSet = super.clone() as BitSet
}