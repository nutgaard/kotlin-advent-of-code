package utils

import kotlin.math.abs

interface Coordinate {
    val row: Long
    val column: Long

    fun <T> getValue(grid: Grid<T>): T {
        return grid.getValue(this)
    }

    fun <T> getValue(grid: SparseGrid<T>): T {
        return requireNotNull(grid.getOrNull(this))
    }

    fun <T> setValue(grid: Grid<T>, value: T) {
        grid.setValue(this, value)
    }

    fun <T> setValue(grid: SparseGrid<T>, value: T) {
        grid.set(this, value)
    }

    infix fun withinBoundsOf(grid: Grid<*>): Boolean {
        return row in grid.rowIndices && column in grid.columnIndices
    }

    infix fun notWithinBoundsOf(grid: Grid<*>): Boolean {
        return !this.withinBoundsOf(grid)
    }

    fun up() = of(row - 1, column)
    fun down() = of(row + 1, column)
    fun left() = of(row, column - 1)
    fun right() = of(row, column + 1)

    fun gridStepsBetween(other: Coordinate): Long {
        return abs(this.row - other.row) + abs(this.column - other.column)
    }

    operator fun component1(): Long = row
    operator fun component2(): Long = column
    operator fun plus(vector: Vector): Coordinate {
        return of(
            row = this.row + vector.y,
            column = this.column + vector.x
        )
    }

    companion object {
        fun of(row: Int, column: Int): Coordinate = CoordinateImpl(row, column)
        fun of(row: Long, column: Long): Coordinate = CoordinateImpl(row, column)

        fun fromArrayIndex(index: Int, columnCount: Int) = of(
            row = index / columnCount,
            column = index % columnCount
        )
    }

    fun asCopy(): Coordinate
}

data class CoordinateImpl(
    override val row: Long,
    override val column: Long,
): Coordinate {
    companion object {
        operator fun invoke(row: Int, column: Int) = CoordinateImpl(row.toLong(), column.toLong())
    }

    override fun asCopy(): Coordinate {
        return this.copy()
    }
}