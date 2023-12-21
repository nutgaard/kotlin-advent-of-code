package day21

import utils.*

const val dir = "day21"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test").toCharGrid()
    val testInputPart2 = readInput("${dir}/Part02_test").toCharGrid()

    verifySolution(testInputPart1, 2, bind(1, ::part1))
    verifySolution(testInputPart1, 4, bind(2, ::part1))
    verifySolution(testInputPart1, 6, bind(3, ::part1))
    verifySolution(testInputPart1, 16, bind(6, ::part1))
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input").toCharGrid()

    timed("part1") { part1(64, input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(maxSteps: Int, grid: ArrayGrid<Char>): Int {
    val seen = grid.map { -1 }
    val start = requireNotNull(grid.findCoordinate { it == 'S' })
    val queue = ArrayDeque<Pair<Coordinate, Int>>()
        .apply { addLast(start to 0) }

    do {
        val (current, step) = queue.removeFirst()
        if (step > maxSteps) continue
        if (seen.getValue(current) >= 0) continue
        if (!current.withinBounds(grid)) continue
        if (grid.getValue(current) == '#') continue

        seen.setValue(current, step)

        queue.addLast(current.up() to step + 1)
        queue.addLast(current.down() to step + 1)
        queue.addLast(current.left() to step + 1)
        queue.addLast(current.right() to step + 1)
    } while (queue.isNotEmpty())

    println(
        seen.asString { row, column, value ->
            val isStart = start.row.toInt() == row && start.column.toInt() == column
            if (value < 0) {
                if (isStart) " #" else " ."
            } else {
                " $value"
            }
        }
    )

    return seen
        .flattenToList()
        .filter { (maxSteps % 2) == (it % 2) }
        .count { it >= 0 }
}

fun part2(input: ArrayGrid<Char>): Int {
    return -1
}