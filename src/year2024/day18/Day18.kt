package year2024.day18

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import utils.*

const val dir = "year2024/day18"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    val part1Config = Config(
        dimension = Dimension(7, 7),
        simulateBytes = 12
    )
    verifySolution(testInputPart1, 22, bind(part1Config, ::part1))
    verifySolution(testInputPart2, "6,1", bind(part1Config, ::part2))

    val input = readInput("$dir/Input")
    val config = Config(
        dimension = Dimension(71, 71),
        simulateBytes = 1024
    )

    timed("part1") { part1(config, input).println() }
    timed("part2") { part2(config, input).println() }
}

data class Config(
    val dimension: Dimension,
    val simulateBytes: Int
)

fun part1(config: Config, input: List<String>): Int {
    val (grid) = parseInputPart1(config, input)
    val start = Coordinate.of(0, 0)
    val target = Coordinate.of(
        row = grid.dimension.height - 1,
        column = grid.dimension.width - 1,
    )

    val solution = solve(
        grid = grid,
        start = start,
        target = target
    ) ?: error("Route not found")

    solution.forEach { grid.setValue(it, 'O') }

    return solution.size - 1
}

fun part2(config: Config, input: List<String>): String {
    val (grid, coordinates) = parseInputPart1(config, input)
    val start = Coordinate.of(0, 0)
    val target = Coordinate.of(
        row = grid.dimension.height - 1,
        column = grid.dimension.width - 1,
    )

    val range: IntRange = config.simulateBytes until coordinates.size
    var solution = requireNotNull(
        solve(
            grid = grid,
            start = start,
            target = target
        )
    )
    for (startIdx in range) {
        val coordinate = coordinates[startIdx]
        grid.setValue(coordinate, '#')
        if (coordinate !in solution) continue

        val updatedSolution = solve(
            grid = grid,
            start = start,
            target = target
        )
        if (updatedSolution != null) {
            solution = updatedSolution
        } else {
            return "${coordinate.column},${coordinate.row}"
        }
    }

    error("All routes solveable")
}

fun solve(
    grid: ArrayGrid<Char>,
    start: Coordinate,
    target: Coordinate
): List<Coordinate>? {
    val costGrid: ArrayGrid<PersistentList<Coordinate>?> = grid.map { null }
    val stack = ConfigurableDeque.Stack<Coordinate>()

    costGrid.setValue(start, persistentListOf(start))
    stack.push(start)

    while (stack.isNotEmpty()) {
        val current = stack.pop()
        val currentPath = costGrid.getValue(current)!!
        val currentCost = currentPath.size

        for (direction in Direction.entries) {
            val newPosition = current.move(direction)
            if (newPosition notWithinBoundsOf grid) continue
            if (grid.getValue(newPosition) == '#') continue

            val previousPath = costGrid.getValue(newPosition)
            val previousCostValue = previousPath?.size ?: Int.MAX_VALUE

            if (currentCost + 1 < previousCostValue) {
                costGrid.setValue(
                    newPosition,
                    currentPath.add(newPosition)
                )
                stack.push(newPosition)
            }
        }
    }

    return costGrid.getValue(target)
}

data class Day18Input(
    val grid: ArrayGrid<Char>,
    val coordinates: Array<Coordinate>,
)

fun parseInputPart1(config: Config, input: List<String>): Day18Input {
    val grid = ArrayGrid.create(config.dimension) { '.' }
    val coordinates = input.map(::parseCoordinate)
    coordinates.take(config.simulateBytes).forEach {
        grid.setValue(it, '#')
    }

    return Day18Input(grid, coordinates.toTypedArray())
}

fun parseCoordinate(line: String): Coordinate {
    val (x, y) = line.split(",").map(String::toInt)
    return Coordinate.of(y, x)
}