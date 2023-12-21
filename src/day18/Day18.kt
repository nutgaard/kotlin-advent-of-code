package day18

import utils.*

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
    queue.addLast(Coordinate(0, 0))
    var outside = 0
    do {
        val current = queue.removeLast()
        val value = current.getValue(grid)
        if (value != null)continue

        if (seen.contains(current))continue
        seen.add(current)

        outside++;

        val up = current.move(Direction.UP)
        if (up.row in grid.rowIndices) queue.addLast(up)

        val down = current.move(Direction.DOWN)
        if (down.row in grid.rowIndices) queue.addLast(down)

        val left = current.move(Direction.LEFT)
        if (left.column in grid.columnIndices) queue.addLast(left)

        val right = current.move(Direction.RIGHT)
        if (right.column in grid.columnIndices) queue.addLast(right)
    } while (queue.isNotEmpty())

    println(grid.asString())

    val totalSize = grid.dimension.height * grid.dimension.width
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

inline fun <reified  T> Grid<T?>.padByOne(): Grid<T?> {
    val grid = this
    val rows = grid.dimension.height
    val columns = grid.dimension.width
    val padded = Array(rows + 2) { row ->
        Array(columns + 2) { column ->
            if (row == 0) null
            else if (row > rows) null
            else if (column == 0) null
            else if (column > columns) null
            else grid.getValue(Coordinate(row - 1, column - 1))
        }
    }

    return ArrayGrid(padded)
}

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