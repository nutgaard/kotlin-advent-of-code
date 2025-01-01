package year2016.day01

import utils.*
import kotlin.math.abs

const val dir = "year2016/day01"
fun main() {
    verifySolution("R2, L3", 5, ::part1)
    verifySolution("R2, R2, R2", 2, ::part1)
    verifySolution("R5, L5, R5, R3", 12, ::part1)
    verifySolution("R8, R4, R4, R8", 4, ::part2)

    val input = readInput("$dir/Input").first()

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: String): Long {
    val origin = Coordinate.of(0, 0)
    var current = origin.asCopy()
    var direction = Direction.UP

    input.split(",").map(String::trim)
        .forEach {
            val left = it.first() == 'L'
            val block = it.drop(1).toInt()

            if (left) {
                direction = direction.rotateCounterClockwise()
            } else {
                direction = direction.rotateClockwise()
            }
            current += direction.withMagnitude(block)
        }

    return current.manhattenDistanceTo(origin)
}

fun part2(input: String): Long {
    val origin = Coordinate.of(0, 0)
    var current = origin.asCopy()
    var direction = Direction.UP
    val visited = SparseGrid<Boolean>()

    val moves = input.split(",").map(String::trim)
    for (move in moves) {
        val left = move.first() == 'L'
        val block = move.drop(1).toInt()

        if (left) {
            direction = direction.rotateCounterClockwise()
        } else {
            direction = direction.rotateClockwise()
        }

        repeat(block) {
            current = current.move(direction)
            if (visited.getOrDefault(current, false)) {
                return current.manhattenDistanceTo(origin)
            }

            visited.set(current, true)
        }
    }

    return current.manhattenDistanceTo(origin)
}

fun Coordinate.manhattenDistanceTo(other: Coordinate): Long {
    return abs(this.row - other.row) + abs(this.column - other.column)
}