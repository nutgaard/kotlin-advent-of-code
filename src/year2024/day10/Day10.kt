package year2024.day10

import utils.*

const val dir = "year2024/day10"
fun main() {
    val testInputPart1_1 = readInput("${dir}/Part01_1_test")
    val testInputPart1_2 = readInput("${dir}/Part01_2_test")
    val testInputPart2_1 = readInput("${dir}/Part02_1_test")
    val testInputPart2_2 = readInput("${dir}/Part02_2_test")

    verifySolution(testInputPart1_1, 2, ::part1)
    verifySolution(testInputPart1_2, 36, ::part1)
    verifySolution(testInputPart2_1, 3, ::part2)
    verifySolution(testInputPart2_2, 81, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val grid = input.toIntGrid()
    val trailheads = grid.reduce(mutableListOf<Coordinate>()) { acc, value, coordinate ->
        if (value == 0) acc.add(coordinate)
        acc
    }
    return trailheads.sumOf { findValidTrails(grid, it, false) }
}

fun part2(input: List<String>): Int {
    val grid = input.toIntGrid()
    val trailheads = grid.reduce(mutableListOf<Coordinate>()) { acc, value, coordinate ->
        if (value == 0) acc.add(coordinate)
        acc
    }
    return trailheads.sumOf { findValidTrails(grid, it, true) }
}

fun findValidTrails(grid: ArrayGrid<Int>, trailhead: Coordinate, allowReuse: Boolean): Int {
    var trailsFound: Int = 0
    val visited = mutableSetOf<Coordinate>()
    val stack = ConfigurableDeque.Stack<Coordinate>()
    stack.push(trailhead)

    do {
        val current = stack.pop()
        val currentValue = grid.getValue(current)

        visited.add(current)
        if (currentValue == 9) trailsFound++

        for (direction in Direction.entries) {
            val newPosition = current.move(direction)

            if (newPosition notWithinBoundsOf grid) continue
            if (grid.getValue(newPosition) != currentValue + 1) continue
            if (!allowReuse && newPosition in visited) continue

            stack.push(newPosition)
        }
    } while (stack.isNotEmpty())
    
    return trailsFound
}