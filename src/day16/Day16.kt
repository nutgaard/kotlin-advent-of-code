package day16

import utils.*
import kotlin.experimental.and
import kotlin.experimental.or

const val dir = "day16"
fun main() {
    val testInputPart1 = readInput("$dir/Part01_test").toCharGrid()
    val testInputPart2 = readInput("$dir/Part02_test").toCharGrid()

    verifySolution(testInputPart1, 46, ::part1)
    verifySolution(testInputPart2, 51, ::part2)

    val input = readInput("$dir/Input").toCharGrid()

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(grid: ArrayGrid<Char>): Int {
    return grid.simulate(Coordinate(0, -1) to Direction.RIGHT)
}

fun part2(grid: ArrayGrid<Char>): Int {
    val (rows, columns) = grid.dimension
    val beams = buildSet {
        for (row in 0..<rows) {
            add(Coordinate(row, -1) to Direction.RIGHT)
            add(Coordinate(row, columns) to Direction.LEFT)
        }
        for (column in 0..<columns) {
            add(Coordinate(-1, column) to Direction.DOWN)
            add(Coordinate(rows, column) to Direction.UP)
        }
    }
    return beams.maxOf { grid.simulate(it) }
}

fun ArrayGrid<Char>.simulate(beam: Beam): Int {
    val seenGrid: ArrayGrid<Byte> = ArrayGrid(
        Array(this.dimension.height) {
            Array(this.dimension.width) { 0b0000 }
        }
    )
    val beams = ArrayDeque<Beam>()
    beams.add(beam)
    while (beams.isNotEmpty()) {
        val (position, direction) = beams.removeLast()
        val nextBeams = findNext(this, seenGrid, position, direction)
        beams.addAll(nextBeams)
    }

    return seenGrid.grid.sumOf { row -> row.filter { it > 0 }.size }
}

fun findNext(
    grid: ArrayGrid<Char>,
    seenGrid: ArrayGrid<Byte>,
    position: Coordinate,
    direction: Direction
): List<Beam> {
    val newPosition = position.move(direction)

    // Out of bounds.
    if (newPosition.row < 0 || newPosition.row >= grid.dimension.height) return emptyList()
    if (newPosition.column < 0 || newPosition.column >= grid.dimension.width) return emptyList()

    val value = newPosition.getValue(grid)
    val seenValue = newPosition.getValue(seenGrid)

    // Bitmask to check if we've entered this position from same direction earlier
    if (seenValue and direction.mask > 0) return emptyList()
    // Update bitmask
    newPosition.setValue(seenGrid, seenValue or direction.mask)

    return when (value) {
        '.' -> listOf(newPosition to direction)
        '|' -> {
            if (direction.column == 0) {
                listOf(newPosition to direction)
            } else {
                listOf(
                    newPosition to Direction.DOWN,
                    newPosition to Direction.UP
                )
            }
        }

        '-' -> {
            if (direction.row == 0) {
                listOf(newPosition to direction)
            } else {
                listOf(
                    newPosition to Direction.LEFT,
                    newPosition to Direction.RIGHT
                )
            }
        }

        '\\' -> when (direction) {
            Direction.DOWN -> listOf(newPosition to Direction.RIGHT)
            Direction.UP -> listOf(newPosition to Direction.LEFT)
            Direction.LEFT -> listOf(newPosition to Direction.UP)
            Direction.RIGHT -> listOf(newPosition to Direction.DOWN)
        }

        '/' -> when (direction) {
            Direction.DOWN -> listOf(newPosition to Direction.LEFT)
            Direction.UP -> listOf(newPosition to Direction.RIGHT)
            Direction.LEFT -> listOf(newPosition to Direction.DOWN)
            Direction.RIGHT -> listOf(newPosition to Direction.UP)
        }

        else -> emptyList()
    }
}


typealias Beam = Pair<Coordinate, Direction>
enum class Direction(var row: Int, var column: Int, val mask: Byte) {
    UP(-1, 0, 0b0001),
    DOWN(1, 0, 0b0010),
    RIGHT(0, 1, 0b0100),
    LEFT(0, -1, 0b1000)
}

fun Coordinate.move(direction: Direction) = Coordinate(
    this.row + direction.row,
    this.column + direction.column,
)
