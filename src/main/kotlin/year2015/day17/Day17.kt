package year2015.day17

import utils.*
import kotlin.math.min

const val dir = "year2015/day17"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 4, bind(25, ::part1))
    verifySolution(testInputPart2, 3, bind(25, ::part2))

    val input = readInput("$dir/Input")

    timed("part1") { part1(150, input).println() }
    timed("part2") { part2(150, input).println() }
}

fun part1(target: Int, input: List<String>): Int {
    return input
        .map(String::toInt)
        .subsets()
        .count { it.sum() == target }
}

fun part2(target: Int, input: List<String>): Int {
    val solutions = input
        .map(String::toInt)
        .subsets()
        .filter { it.sum() == target }

    val minSolution = solutions.minBy { it.size }.size

    return solutions.count { it.size == minSolution }
}

fun <T> List<T>.subsets(): List<List<T>> {
    if (isEmpty()) return listOf(emptyList())
    val element = first()
    val remainingSubsets = (this - element).subsets()
    return remainingSubsets + remainingSubsets.map { it + element }
}