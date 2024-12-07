package year2023.day04

import utils.println
import utils.readInput
import utils.verifySolution

const val dir = "day04"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 13, ::part1)
    verifySolution(testInputPart2, 30, ::part2)

    val input = readInput("$dir/Input")

    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Int {
    return input.sumOf(::scoreCard)
}

fun part2(input: List<String>): Int {
    val max = input.size-1
    val instances = input.map { 1 }.toMutableList()
    val scores = input.map(::matchingNumbers)

    for (i in input.indices) {
        val score = scores[i]
        for (dup in 1 .. score) {
            if (i + dup > max) break
            instances[i + dup] += instances[i]
        }
    }

    return instances.sum()
}

val space = Regex("\\s+")
fun matchingNumbers(cardString: String): Int {
    val (winningNumbers, yourNumbers) = cardString
        .split(":").last()
        .split("|")
        .map {
            it.trim().split(space).map { it.trim() }
        }

    return yourNumbers.filter { winningNumbers.contains(it) }.size
}

fun scoreCard(cardString: String): Int {
    val matchingNumbers = matchingNumbers(cardString)
    return if (matchingNumbers == 0) 0 else 1 shl (matchingNumbers - 1)
}