package day18

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import kotlin.math.max
import kotlin.math.min

const val dir = "day18"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test").parse()
    val testInputPart2 = readInput("${dir}/Part02_test").parse()

    verifySolution(testInputPart1, 62, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input").parse()

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

data class DigCommand(
    val direction: Direction,
    val length: Short,
    val color: String
)

fun List<String>.parse(): List<DigCommand> {
    return this.map {
        val (directionStr, lengthStr, colorStr) = it.split(" ")
        DigCommand(
            direction = Direction.fromDir(directionStr),
            length = lengthStr.toShort(),
            color = colorStr.removePrefix("(").removeSuffix(")")
        )
    }
}

fun part1(input: List<DigCommand>): Int {
    val grid = buildGrid(input)

    val seen = mutableSetOf<Coordinate>()
    val queue = ArrayDeque<Coordinate>()
    val rowIndices = grid.indices
    val columnsIndices = grid.first().indices
    queue.addLast(Coordinate(0, 0))
    var outside = 0
    do {
        val current = queue.removeLast()
        val value = grid[current.row][current.column]
        if (value != null)continue

        if (seen.contains(current))continue
        seen.add(current)

        outside++;

        val up = current.move(Direction.UP)
        if (up.row in rowIndices) queue.addLast(up)

        val down = current.move(Direction.DOWN)
        if (down.row in rowIndices) queue.addLast(down)

        val left = current.move(Direction.LEFT)
        if (left.column in columnsIndices) queue.addLast(left)

        val right = current.move(Direction.RIGHT)
        if (right.column in columnsIndices) queue.addLast(right)
    } while (queue.isNotEmpty())

    grid.print()

    val totalSize = grid.size * grid.first().size
    return totalSize - outside
}

fun part2(input: List<DigCommand>): Int {
    return -1
}

fun buildGrid(input: List<DigCommand>): Grid<String?> {
    val grid = SparseGrid<String>()
    var position = Coordinate(0, 0)
    grid.set(position, "???")

    for (command in input) {
        for (i in 0..<command.length) {
            position = position.move(command.direction)
            grid.set(position, command.color)
        }
    }

    return grid.toArrayGrid().padByOne()
}

class MinMax(var min: Int, var max: Int) {
    fun update(value: Int) {
        this.min = min(this.min, value)
        this.max = max(this.max, value)
    }

    fun size() = this.max - this.min + 1
}

class SparseGrid<T> {
    val grid: MutableMap<Int, MutableMap<Int, T>> = mutableMapOf()
    val rowDomain = MinMax(0, 0)
    val columnComain = MinMax(0, 0)

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
    return Array(rowDomain.size()) { row ->
        Array(columnComain.size()) { column ->
            grid[row + rowDomain.min]?.get(column + columnComain.min)
        }
    }
}

inline fun <reified  T> Grid<T?>.padByOne(): Grid<T?> {
    val grid = this
    val rows = grid.size
    val columns = grid.first().size
    val padded = Array(rows + 2) { row ->
        Array(columns + 2) { column ->
            if (row == 0) null
            else if (row > rows) null
            else if (column == 0) null
            else if (column > columns) null
            else grid[row - 1][column - 1]
        }
    }

    return padded
}

data class Coordinate(val row: Int, val column: Int)
typealias Grid<T> = Array<Array<T>>

enum class Direction(val row: Int, val column: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    RIGHT(0, 1),
    LEFT(0, -1);

    companion object {
        fun fromDir(str: String): Direction {
            return when (str) {
                "U" -> Direction.UP
                "D" -> Direction.DOWN
                "R" -> Direction.RIGHT
                "L" -> Direction.LEFT
                else -> error("Unrecognized direction $str")
            }
        }
    }
}

fun Coordinate.move(direction: Direction) = Coordinate(
    this.row + direction.row,
    this.column + direction.column,
)

fun <T : Any> Grid<T?>.print() {
    val grid = this
    buildString {
        for (row in grid) {
            for (column in row) {
                if (column == null) append(".")
                else append("#")
            }
            appendLine()
        }
    }.println()
}