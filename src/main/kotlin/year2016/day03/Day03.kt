package year2016.day03

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2016/day03"
fun main() {
    verifySolution(Triple(5, 10, 25), false, ::isValidTriangle)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input
        .map {
            val (a, b, c) = it
                .split(Regex("\\s+"))
                .filter(String::isNotBlank)
                .map(String::toInt)
            Triple(a, b, c)
        }
        .count(::isValidTriangle)
}

fun part2(input: List<String>): Int {
    return input
        .map {
            val (a, b, c) = it
                .split(Regex("\\s+"))
                .filter(String::isNotBlank)
                .map(String::toInt)
            Triple(a, b, c)
        }
        .chunked(3)
        .flatMap { (a, b, c) ->
            listOf(
                Triple(a.first, b.first, c.first),
                Triple(a.second, b.second, c.second),
                Triple(a.third, b.third, c.third),
            )
        }
        .count(::isValidTriangle)
}

fun isValidTriangle(triple: Triple<Int, Int, Int>): Boolean {
    if (triple.third + triple.second <= triple.first) return false
    if (triple.first + triple.third <= triple.second) return false
    if (triple.first + triple.second <= triple.third) return false
    return true
}