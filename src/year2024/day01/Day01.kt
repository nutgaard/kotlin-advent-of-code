package year2024.day01

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import kotlin.math.abs

const val dir = "year2024/day01"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 11, ::part1)
    verifySolution(testInputPart2, 31, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val firstList = mutableListOf<Int>()
    val secondList = mutableListOf<Int>()
    input.forEach {
        val (first, second) = it.split(Regex("\\s+")).map(String::toInt)
        firstList.add(first)
        secondList.add(second)
    }

    firstList.sort()
    secondList.sort()

    return firstList.zip(secondList)
        .map { (first, second) ->
            abs(first - second)
        }.sum()
}

fun part2(input: List<String>): Int {
    val firstList = mutableListOf<Int>()
    val secondList = mutableListOf<Int>()
    input.forEach {
        val (first, second) = it.split(Regex("\\s+")).map(String::toInt)
        firstList.add(first)
        secondList.add(second)
    }

    val counted = secondList
        .groupBy { it }
        .mapValues { it.value.size }

    return firstList.sumOf {
        it * counted.getOrDefault(it, 0)
    }
}