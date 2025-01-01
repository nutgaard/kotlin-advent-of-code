package year2016.day06

import utils.*

const val dir = "year2016/day06"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, "easter", ::part1)
    verifySolution(testInputPart2, "advent", ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): String {
    val grid = input.toCharGrid().transpose('.')
    return buildString {
        for (row in grid.grid) {
            append(row.mostCommon())
        }
    }
}

fun part2(input: List<String>): String {
    val grid = input.toCharGrid().transpose('.')
    return buildString {
        for (row in grid.grid) {
            append(row.leastCommon())
        }
    }
}

fun <T> Array<T>.mostCommon(): T? {
    val counts = this.groupingBy { it }.eachCount()
    val max = counts.maxByOrNull { it.value }
    return max?.key
}

fun <T> Array<T>.leastCommon(): T? {
    val counts = this.groupingBy { it }.eachCount()
    val max = counts.minByOrNull { it.value }
    return max?.key
}