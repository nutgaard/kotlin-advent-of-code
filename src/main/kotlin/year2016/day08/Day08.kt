package year2016.day08

import utils.*

const val dir = "year2016/day08"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    //verifySolution(testInputPart1, 6, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val grid = ArrayGrid.create(6, 50) { '.' }
    for (move in input) {
        if (move.startsWith("rect")) {
            val (a, b) = move.removePrefix("rect ").split("x").map(String::toInt)
            for (r in 0..<b) {
                for (c in 0 ..<a) {
                    grid.setValue(Coordinate.Companion.of(r, c), '#')
                }
            }
        } else if (move.startsWith("rotate column")) {
            val column = move.removePrefix("rotate column x=").takeWhile { it.isDigit() }.toInt()
            val amount = move.takeLastWhile { it.isDigit() }.toInt()
            val copy = grid.rowIndices.map {
                grid.getValue(it, column)
            }
            grid.rowIndices.forEach {
                val shiftIndex = cyclic(it - amount, grid.dimension.height)
                grid.setValue(Coordinate.of(it, column), copy.get(shiftIndex))
            }
        } else if (move.startsWith("rotate row")) {
            val row = move.removePrefix("rotate row y=").takeWhile { it.isDigit() }.toInt()
            val amount = move.takeLastWhile { it.isDigit() }.toInt()
            val copy = grid.columnIndices.map {
                grid.getValue(row, it)
            }
            grid.columnIndices.forEach {
                val shiftIndex = cyclic(it - amount, grid.dimension.width)
                grid.setValue(Coordinate.of(row, it), copy.get(shiftIndex))
            }
        } else {
            error("Invalid move: $move")
        }
        grid.asString().println()
        println()
    }

    return grid.reduce(0) { acc, value, coordinate ->
        if (value == '#') acc + 1
        else acc
    }
}

fun part2(input: List<String>): Int {
    /**
     * Manually inspected grid
     */
    return -1
}

fun cyclic(i: Int, group: Int): Int {
    if (i < 0) return i + group
    return i % group
}