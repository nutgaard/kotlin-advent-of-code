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
}

inline fun <reified T> SparseGrid<T>.toArrayGrid(): Grid<T?> {
    val grid = Array(rowDomain.size().toInt()) { row ->
        Array(columnComain.size().toInt()) { column ->
            grid[row + rowDomain.min]?.get(column + columnComain.min)
        }
    }
    return ArrayGrid(grid)
}

data class Dimensjon(val height: Int, val width: Int)
interface Grid<T> {
    val dimension: Dimensjon
    val rowIndices: IntRange
    val columnIndices: IntRange

    fun getValue(coordinate: Coordinate): T
    fun setValue(coordinate: Coordinate, value: T)

    fun asString(fn: (Int, Int, T) -> String = { _, _, it -> it.toString() }): String = buildString {
        for (row in 0 until dimension.height) {
            for (column in 0 until dimension.width) {
                append(fn(row, column, getValue(Coordinate(row, column))))
            }
            appendLine()
        }
    }

    fun findCoordinate(predicate: (T) -> Boolean): Coordinate?
    fun flattenToList(): List<T>
}

data class ArrayGrid<T>(val grid: Array<Array<T>>) : Grid<T> {
    override val dimension = Dimensjon(grid.size, grid.first().size)
    override val rowIndices: IntRange = grid.indices
    override val columnIndices: IntRange = grid.first().indices


    override fun getValue(coordinate: Coordinate): T {
        return grid[coordinate.row.toInt()][coordinate.column.toInt()]
    }

    override fun setValue(coordinate: Coordinate, value: T) {
        grid[coordinate.row.toInt()][coordinate.column.toInt()] = value
    }

    override fun findCoordinate(predicate: (T) -> Boolean): Coordinate? {
        for (row in rowIndices) {
            for (column in columnIndices) {
                if (predicate(grid[row][column])) return Coordinate(row, column)
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
}

inline fun <reified T, reified R> ArrayGrid<T>.map(transform: (T) -> R): Grid<R> {
    return this.mapIndexed { _, _, v -> transform(v) }
}

inline fun <reified T, reified R> ArrayGrid<T>.mapIndexed(transform: (row: Int, column: Int, T) -> R): Grid<R> {
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
    override val dimension = Dimensjon(grid.size, grid.first().nbits)
    override val rowIndices: IntRange = grid.indices
    override val columnIndices: IntRange = 0 until grid.first().nbits


    override fun getValue(coordinate: Coordinate): Boolean {
        return grid[coordinate.row.toInt()][coordinate.column.toInt()]
    }

    override fun setValue(coordinate: Coordinate, value: Boolean) {
        grid[coordinate.row.toInt()][coordinate.column.toInt()] = value
    }

    override fun toString(): String {
        return asString { _, _, it -> if (it) "1" else "0" }
    }

    override fun findCoordinate(predicate: (Boolean) -> Boolean): Coordinate? {
        for (row in rowIndices) {
            for (column in columnIndices) {
                if (predicate(grid[row][column])) return Coordinate(row, column)
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
}

class SizeAwareBitSet(val nbits: Int) : BitSet(nbits) {
    override fun clone(): BitSet = super.clone() as BitSet
}