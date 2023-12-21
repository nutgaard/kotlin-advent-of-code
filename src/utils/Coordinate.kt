package utils

import kotlin.math.abs

data class Coordinate(
    val row: Long,
    val column: Long,
) {
    companion object {
        operator fun invoke(row: Int, column: Int) = Coordinate(row.toLong(), column.toLong())
    }

    fun <T> getValue(grid: Grid<T>): T {
        return grid.getValue(this)
    }

    fun <T> getValue(grid: SparseGrid<T>): T {
        return requireNotNull(grid.get(this))
    }

    fun <T> setValue(grid: Grid<T>, value: T) {
        grid.setValue(this, value)
    }

    fun <T> setValue(grid: SparseGrid<T>, value: T) {
        grid.set(this, value)
    }

    fun up() = Coordinate(row - 1, column)
    fun down() = Coordinate(row + 1, column)
    fun left() = Coordinate(row, column - 1)
    fun right() = Coordinate(row, column + 1)

    fun gridStepsBetween(other: Coordinate): Long {
        return abs(this.row - other.row) + abs(this.column - other.column)
    }

    fun withinBounds(grid: Grid<*>): Boolean {
        return row in grid.rowIndices && column in grid.columnIndices
    }
}