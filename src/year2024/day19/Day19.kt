package year2024.day19

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2024/day19"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 6, ::part1)
    verifySolution(testInputPart2, 16, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val data = Day19Input.parse(input)
    val solutions = data.targetPatterns
        .map {
            solve(it, data.availablePatterns, mutableMapOf())
        }

    return solutions.count { it > 0 }
}

fun part2(input: List<String>): Long {
    val data = Day19Input.parse(input)
    val solutions = data.targetPatterns
        .map {
            solve(it, data.availablePatterns, mutableMapOf())
        }

    return solutions.sum()
}

fun solve(
    target: String,
    patterns: List<String>,
    memo: MutableMap<String, Long>
): Long {
    if (memo[target] != null) return memo[target]!!
    if (target.isEmpty()) return 1

    val result = patterns
        .filter { target.startsWith(it) }
        .sumOf { solve(target.substring(it.length), patterns, memo) }
    memo[target] = result

    return result
}

data class Day19Input(
    val availablePatterns: List<String>,
    var targetPatterns: List<String>,
) {
    companion object {
        fun parse(input: List<String>): Day19Input {
            val availablePatterns = input.first().split(",").map(String::trim)
            val targetPatterns = input.drop(2)
            return Day19Input(availablePatterns, targetPatterns)
        }
    }
}