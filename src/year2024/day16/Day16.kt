package year2024.day16

import utils.*
import kotlin.math.min

const val dir = "year2024/day16"
fun main() {
    val testInputPart1x1 = readInput("${dir}/Part01_1_test")
    val testInputPart1x2 = readInput("${dir}/Part01_2_test")

    verifySolution(testInputPart1x1, Result(7036, 45), ::part2)
    verifySolution(testInputPart1x2, Result(11048, 64), ::part2)

    val input = readInput("$dir/Input")

    timed("part2") { part2(input).println() }
}

data class RecursionState(
    val position: Coordinate,
    val direction: Direction,
)

data class CostChain(
    val previous: Coordinate,
    val cost: Long,
)

data class Day16Context(
    val grid: ArrayGrid<Char>,
    val costGrid: ArrayGrid<CostChain?>,
    val startingPosition: Coordinate,
    val endingPosition: Coordinate,
    val stack: ConfigurableDeque<RecursionState>,
)

data class Result(
    val minimalScore: Long,
    val numberOfMoves: Int,
)

fun part2(input: List<String>): Result {
    val grid: ArrayGrid<Char> = input.toCharGrid()
    val ctx = Day16Context(
        grid = grid,
        costGrid = grid.map { null },
        startingPosition = requireNotNull(grid.findCoordinate { it == 'S' }),
        endingPosition = requireNotNull(grid.findCoordinate { it == 'E' }),
        stack = ConfigurableDeque.Stack(),
    )

    val paths = findPaths(
        from = ctx.startingPosition,
        to = ctx.endingPosition,
        grid = grid,
    )

    val minimalScore = paths.minOf { it.second }
    val minimalScorePaths = paths.filter { it.second == minimalScore }
    val coordinates = minimalScorePaths
        .flatMap { it.first }
        .toSet()

    return Result(minimalScore, coordinates.size)
}

data class StackCtx(
    val path: List<Coordinate>,
    val direction: Direction,
    val score: Long
)

fun findPaths(from: Coordinate, to: Coordinate, grid: ArrayGrid<Char>): List<Pair<List<Coordinate>, Long>> {
    val minvalueGrid = grid.map {
        mutableMapOf(
            Direction.LEFT to Long.MAX_VALUE,
            Direction.RIGHT to Long.MAX_VALUE,
            Direction.UP to Long.MAX_VALUE,
            Direction.DOWN to Long.MAX_VALUE,
        )
    }

    var minScore: Long = Long.MAX_VALUE
    val paths = mutableListOf<Pair<List<Coordinate>, Long>>()
    val stack = ConfigurableDeque.Stack<StackCtx>()
    stack.push(StackCtx(
        path = listOf(from),
        direction = Direction.RIGHT,
        score = 0
    ))

    while (stack.isNotEmpty()) {
        val current = stack.pop()
        val position = current.path.last()

        val directions = listOf(
            current.direction,
            current.direction.rotateClockwise(),
            current.direction.rotateCounterClockwise(),
        )

        for (direction in directions) {
            val newPosition = position.move(direction)

            if (grid.getValue(newPosition) == '#') continue
            if (newPosition in current.path) continue

            val marginalCost = if (direction == current.direction) 1L else 1001L
            val newScore = current.score + marginalCost

            val minValueCell = minvalueGrid.getValue(newPosition)
            if (minValueCell[direction]!! < newScore) continue
            minValueCell[direction] = min(minValueCell[direction]!!, newScore)

            if (newPosition == to) {
                val fullPath = current.path + newPosition
                paths.add(fullPath to newScore)
                minScore = min(minScore, newScore)
                continue
            }

            if (newScore > minScore) continue
            stack.push(StackCtx(
                path = current.path + newPosition,
                direction = direction,
                score = newScore
            ))
        }
    }
    return paths
}