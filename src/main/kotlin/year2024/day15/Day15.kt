package year2024.day15

import utils.*

const val debugPart1 = false
const val debugPart2 = false
const val dir = "year2024/day15"

fun main() {
    val testInputPart1Easy = readInput("${dir}/Part01_easy_test")
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")
    val repodInputPart2 = readInput("${dir}/Part02_repo_test")

    part1(testInputPart1Easy)
    verifySolution(testInputPart1, 10092, ::part1)
    part2(repodInputPart2)

    verifySolution(testInputPart2, 9021, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    val state = parseInput(input)

    if (debugPart1) {
        println("Initial state:")
        println(state.grid.asString())
        println()
    }

    for (move in state.moves) {
        state.move(move)
        if (debugPart1) {
            println("Move $move:")
            println(state.grid.asString())
            println()
        }
    }

    return state.grid.reduce(0L) { acc, value, coordinate ->
        if (value != 'O') acc
        else acc + (100 * coordinate.row) + coordinate.column
    }
}

fun part2(input: List<String>): Long {
    val state = parseInput(input).let {
        State(
            grid = scaleUp(it.grid),
            moves = it.moves,
        )
    }

    if (debugPart2) {
        println("Initial state:")
        println(state.grid.asString())
    }

    var i = 0
    for (move in state.moves) {
        state.movePart2(move)
        if (debugPart2) {
            println("Move $move (${i++}):")
            println(state.grid.asString())
        }
    }

    return state.grid.reduce(0L) { acc, value, coordinate ->
        if (value != '[') acc
        else acc + (100 * coordinate.row) + coordinate.column
    }
}

data class State(
    val grid: ArrayGrid<Char>,
    val moves: List<Direction>
) {
    var robot: Coordinate = requireNotNull(grid.findCoordinate { it == '@' })

    fun move(direction: Direction) {
        val nextPosition = robot.move(direction)
        if (grid.getValue(nextPosition) == '#') {
            return
        } else if (grid.getValue(nextPosition) == '.') {
            grid.setValue(robot, '.')
            grid.setValue(nextPosition, '@')
            robot = nextPosition
        } else {
            var scanPosition = nextPosition.asCopy()
            var hasFreeSpace = false
            val mustBeUpdated = mutableListOf(scanPosition)
            do {
                scanPosition = scanPosition.move(direction)
                if (grid.getValue(scanPosition) == '#') {
                    break
                } else if (grid.getValue(scanPosition) == '.') {
                    hasFreeSpace = true
                    break
                } else {
                    mustBeUpdated.add(scanPosition)
                }
            } while (true)

            if (!hasFreeSpace) return

            grid.setValue(robot, '.')
            grid.setValue(nextPosition, '@')
            grid.setValue(scanPosition, 'O')
            robot = nextPosition
        }
    }

    val shiftLeft = Vector(-1, 0)
    val shiftRight = Vector(1, 0)

    fun movePart2(direction: Direction) {
        val nextPosition = robot.move(direction)
        if (grid.getValue(nextPosition) == '#') {
            return
        } else if (grid.getValue(nextPosition) == '.') {
            grid.setValue(robot, '.')
            grid.setValue(nextPosition, '@')
            robot = nextPosition
        } else if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            var scanPosition = nextPosition.asCopy()
            var hasFreeSpace = false
            val mustBeUpdated = mutableListOf(scanPosition)
            do {
                scanPosition = scanPosition.move(direction)
                if (grid.getValue(scanPosition) == '#') {
                    break
                } else if (grid.getValue(scanPosition) == '.') {
                    hasFreeSpace = true
                    break
                } else {
                    mustBeUpdated.add(scanPosition)
                }
            } while (true)

            if (!hasFreeSpace) return

            for (coordinate in mustBeUpdated.distinct().reversed()) {
                val value = grid.getValue(coordinate)
                grid.setValue(coordinate.move(direction), value)
            }
            grid.setValue(robot, '.')
            grid.setValue(nextPosition, '@')
            robot = nextPosition
        } else {
            // UP/DOWN can push multiple boxes
            val scanPosition = nextPosition.asCopy()

            val leftside = if (grid.getValue(scanPosition) == '[') {
                scanPosition
            } else {
                scanPosition + shiftLeft
            }

            val (hasFreeSpace, mustBeUpdated) = canMove(leftside, direction)
            if (!hasFreeSpace) return

            val updateOrder = mustBeUpdated
                .distinct()
                .sortedWith { a, b ->
                    if (direction == Direction.DOWN) {
                        (b.row - a.row).toInt()
                    } else {
                        (a.row - b.row).toInt()
                    }
                }

            for (leftSide in updateOrder) {
                val rightSide = leftSide + shiftRight
                val leftValue = grid.getValue(leftSide)
                val rightValue = grid.getValue(rightSide)
                grid.setValue(leftSide, '.')
                grid.setValue(leftSide.move(direction), leftValue)
                grid.setValue(rightSide, '.')
                grid.setValue(rightSide.move(direction), rightValue)
            }
            grid.setValue(robot, '.')
            grid.setValue(nextPosition, '@')
            robot = nextPosition
        }
    }

    fun canMove(
        coordinate: Coordinate,
        direction: Direction,
        aggregate: MutableList<Coordinate> = mutableListOf()
    ): Pair<Boolean, List<Coordinate>> {
        if (grid.getValue(coordinate) == '[') {
            aggregate.add(coordinate)
        }

        val nextLeftPosition = coordinate.move(direction)
        val leftValue = grid.getValue(nextLeftPosition)
        if (leftValue == '#') {
            return false to aggregate
        }

        val nextRightPosition = nextLeftPosition + shiftRight
        val rightValue = grid.getValue(nextRightPosition)
        if (rightValue == '#') {
            return false to aggregate
        }



        if (leftValue == '.' && rightValue == '.') {
            return true to aggregate
        }


        val left = when (leftValue) {
            '[' -> canMove(nextLeftPosition, direction, aggregate)
            '.' -> true to aggregate
            else -> canMove(nextLeftPosition + shiftLeft, direction, aggregate)
        }
        val right = when (rightValue) {
            '[' -> canMove(nextRightPosition, direction, aggregate)
            '.' -> true to aggregate
            else -> canMove(nextRightPosition + shiftLeft, direction, aggregate)
        }

        return Pair(
            left.first && right.first,
            (left.second + right.second).distinct()
        )
    }
}

fun parseInput(input: List<String>): State {
    val grid = input
        .asSequence()
        .takeWhile { it.startsWith("#") }
        .toList()

    val moves = input
        .asSequence()
        .dropWhile { it.startsWith("#") }
        .flatMap { it.split("") }
        .filter { it.isNotBlank() }
        .map(Direction::fromDir)
        .toList()

    return State(
        grid = grid.toCharGrid(),
        moves = moves
    )
}

fun scaleUp(grid: ArrayGrid<Char>): ArrayGrid<Char> {
    val scaledUp = ArrayGrid.create(
        Dimension(grid.dimension.height, grid.dimension.width * 2)
    ) { '.' }

    for (rowIdx in grid.rowIndices) {
        val row = grid.grid[rowIdx]
        for (colIdx in grid.columnIndices) {
            val value = row[colIdx]

            when (value) {
                '#' -> {
                    scaledUp.grid[rowIdx][colIdx * 2] = '#'
                    scaledUp.grid[rowIdx][colIdx * 2 + 1] = '#'
                }

                'O' -> {
                    scaledUp.grid[rowIdx][colIdx * 2] = '['
                    scaledUp.grid[rowIdx][colIdx * 2 + 1] = ']'
                }

                '.' -> {
                    scaledUp.grid[rowIdx][colIdx * 2] = '.'
                    scaledUp.grid[rowIdx][colIdx * 2 + 1] = '.'
                }

                '@' -> {
                    scaledUp.grid[rowIdx][colIdx * 2] = '@'
                    scaledUp.grid[rowIdx][colIdx * 2 + 1] = '.'
                }
            }
        }
    }
    return scaledUp
}