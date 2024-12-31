package year2015.day18

import utils.*

const val dir = "year2015/day18"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 4, bind(4, ::part1))
    verifySolution(testInputPart2, 17, bind(5, ::part2))

    val input = readInput("$dir/Input")

    timed("part1") { part1(100, input).println() }
    timed("part2") { part2(100, input).println() }
}

fun part1(ticks: Int, input: List<String>): Int {
    var grid = input.toCharGrid()

    repeat(ticks) {
        grid = gameOfLife(grid)
    }

    return grid.flattenToList().count { it == '#' }
}

fun part2(ticks: Int, input: List<String>): Int {
    var grid = input.toCharGrid()
    grid.setValue(Coordinate.of(0, 0), '#')
    grid.setValue(Coordinate.of(0, grid.dimension.width - 1), '#')
    grid.setValue(Coordinate.of(grid.dimension.height - 1, 0), '#')
    grid.setValue(Coordinate.of(grid.dimension.height - 1, grid.dimension.width - 1), '#')

    repeat(ticks) {
        grid = gameOfLife(grid)
        grid.setValue(Coordinate.of(0, 0), '#')
        grid.setValue(Coordinate.of(0, grid.dimension.width - 1), '#')
        grid.setValue(Coordinate.of(grid.dimension.height - 1, 0), '#')
        grid.setValue(Coordinate.of(grid.dimension.height - 1, grid.dimension.width - 1), '#')
    }

    return grid.flattenToList().count { it == '#' }
}

fun gameOfLife(grid: ArrayGrid<Char>): ArrayGrid<Char> {
    val kernel = Kernel(listOf(
        Vector(-1, -1),
        Vector(-1, 0),
        Vector(-1, 1),
        Vector(0, -1),
        Vector(0, 1),
        Vector(1, -1),
        Vector(1, 0),
        Vector(1, 1),
    ))

    return grid.mapIndexed { row, column, c ->
        kernel.position = Coordinate.of(row, column)
        val neighbours = kernel.coordinates()
            .filter { it withinBoundsOf grid }
            .count { grid.getValue(it) == '#' }

        if (c == '#') {
            if (neighbours == 2 || neighbours == 3) '#'
            else '.'
        } else {
            if (neighbours == 3) '#'
            else '.'
        }
    }
}

data class Kernel(val vectors: List<Vector>) {
    var position = Coordinate.of(0, 0)

    fun <T> readFrom(grid: Grid<T>): List<T> {
        return vectors
            .map { grid.getValue(position + it) }
    }

    fun coordinates(): List<Coordinate> {
        return vectors.map { position + it }
    }
}



