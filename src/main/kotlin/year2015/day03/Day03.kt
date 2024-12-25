package year2015.day03

import utils.*

const val dir = "year2015/day03"
fun main() {
    verifySolution(">", 2, ::part1)
    verifySolution("^>v<", 4, ::part1)
    verifySolution("^v^v^v^v^v", 2, ::part1)
    verifySolution("^v", 3, ::part2)
    verifySolution("^>v<", 3, ::part2)
    verifySolution("^v^v^v^v^v", 11, ::part2)

    val input = readInput("$dir/Input").first()

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: String): Int {
    val grid = SparseGrid<Int>()
    val moves = input.toCharArray().map(Direction::fromDir)
    var position = Coordinate.of(0, 0)

    grid.set(position, grid.getOrDefault(position, 0) + 1)

    for (move in moves) {
        position = position.move(move)
        grid.set(position, grid.getOrDefault(position, 0) + 1)
    }

    return grid.reduce(0) { acc: Int, value: Int, coordinate: Coordinate ->
        if (value > 0) {
            acc + 1
        } else {
            acc
        }
    }
}

fun part2(input: String): Int {
    val grid = SparseGrid<Int>()
    val moves = input.toCharArray().map(Direction::fromDir)
    var santaPosition = Coordinate.of(0, 0)
    var robotPosition = Coordinate.of(0, 0)

    grid.set(santaPosition, grid.getOrDefault(santaPosition, 0) + 1)
    grid.set(robotPosition, grid.getOrDefault(robotPosition, 0) + 1)

    var idx = 0

    for (move in moves) {
        if (idx++ % 2 == 0) {
            santaPosition = santaPosition.move(move)
            grid.set(santaPosition, grid.getOrDefault(santaPosition, 0) + 1)
        } else {
            robotPosition = robotPosition.move(move)
            grid.set(robotPosition, grid.getOrDefault(robotPosition, 0) + 1)
        }
    }

    return grid.reduce(0) { acc: Int, value: Int, coordinate: Coordinate ->
        if (value > 0) {
            acc + 1
        } else {
            acc
        }
    }
}