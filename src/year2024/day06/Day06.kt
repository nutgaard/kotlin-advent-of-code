package year2024.day06

import utils.*

const val dir = "year2024/day06"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    //verifySolution(testInputPart1, 41, ::part1)
    //verifySolution(testInputPart2, 6, ::part2)
    part2(testInputPart1)

    val input = readInput("$dir/Input")
    timed("solution") {
        part2(input)
    }

    //timed("part1") { part1(input).println() }
    //timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val grid = input.toCharGrid()
    val startingPosition = requireNotNull(
        grid.findCoordinate { "^v<>".contains(it) }
    )
    val startingDirection = Direction.fromDir(grid.getValue(startingPosition))
    grid.setValue(startingPosition, '.')

    val result = walkIt(
        startingPosition = startingPosition,
        startingDirection = startingDirection,
        grid = grid
    )
    if (result !is WalkingResult.OutOfBounds) error("Incorrect return type: $result")
    return result.touchedSquares.size
}

fun part2(input: List<String>): Int {
    val grid = input.toCharGrid()
    val startingPosition = requireNotNull(
        grid.findCoordinate { "^v<>".contains(it) }
    )
    val startingDirection = Direction.fromDir(grid.getValue(startingPosition))
    grid.setValue(startingPosition, '.')

    val originalResult = walkIt(
        startingPosition = startingPosition,
        startingDirection = startingDirection,
        grid = grid
    )
    if (originalResult !is WalkingResult.OutOfBounds) error("Incorrect return type: $originalResult")
    println("Part 1: ${originalResult.touchedSquares.size}")

    val obsticalCandidates = originalResult.touchedSquares.drop(1)

    var counter = 0
    for (candidate in obsticalCandidates) {
        val gridCopy = grid.copyOf()
        gridCopy.setValue(candidate, '#')
        val result = walkIt(
            startingPosition = startingPosition,
            startingDirection = startingDirection,
            grid = gridCopy
        )
        if (result is WalkingResult.Cycle) counter++
    }

    println("Part 2: ${counter}")
    println()
    return counter
}

sealed class WalkingResult {
    class OutOfBounds(val touchedSquares: List<Coordinate>) : WalkingResult()
    data object Cycle : WalkingResult()
}

fun <S, T> MutableMap<S, MutableSet<T>>.addElement(key: S, element: T): MutableMap<S, MutableSet<T>> {
    val elementSet: MutableSet<T> = this.getOrElse(key) { HashSet() }
    elementSet.add(element)
    this[key] = elementSet
    return this
}
fun <S, T> MutableMap<S, MutableSet<T>>.containsElement(key: S, element: T): Boolean {
    return this[key]?.contains(element) ?: false
}

fun walkIt(
    startingPosition: Coordinate,
    startingDirection: Direction,
    grid: ArrayGrid<Char>
): WalkingResult {
    val tracing = HashMap<Coordinate, MutableSet<Direction>>()
    tracing.addElement(startingPosition, startingDirection)

    var position = startingPosition
    var direction = startingDirection

    do {
        val nextPosition = position.move(direction)
        if (nextPosition notWithinBoundsOf grid) {
            break
        }

        val isNextLocationOpen = grid.getValue(nextPosition) == '.'
        if (isNextLocationOpen) {
            position = nextPosition
            if (tracing.containsElement(position, direction)) return WalkingResult.Cycle
            tracing.addElement(position, direction)
        } else {
            direction = direction.rotateClockwise()
        }
    } while (true)

    return WalkingResult.OutOfBounds(tracing.keys.toList())
}
