package dayXX

import println
import readInput
import timed
import verifySolution

const val dir = "dayXX"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, -1, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return -1
}

fun part2(input: List<String>): Int {
    return -1
}