package year2015.day10

import utils.*

const val dir = "year2015/day10"
fun main() {
    verifySolution("1", "312211", bind(5, ::part1Solver))

    val input = readInput("$dir/Input")

    timed("part1") { part1(40, "1113122113").println() }
    timed("part2") { part1(50, "1113122113").println() }
}

fun part1(cycles: Int, input: String): Int {
    return part1Solver(cycles, input).length
}

fun part1Solver(cycles: Int, input: String): String {
    var current: String = input
    repeat(cycles) {
        current = lookNSay(current)
    }
    return current
}

fun part2(input: List<String>): Int {
    return -1
}

fun lookNSay(input: String): String {
    var current: Char? = null
    var count: Int = 0

    return buildString {
        for (ch in input.toCharArray()) {
            if (current == null) {
                current = ch
                count = 1
            } else if (current == ch) {
                count++
            } else {
                append(count)
                append(current)

                current = ch
                count = 1
            }
        }
        if (count > 0) {
            append(count)
            append(current)
        }
    }
}