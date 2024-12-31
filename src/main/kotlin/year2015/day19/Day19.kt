package year2015.day19

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2015/day19"
fun main() {
    val testInputPart101 = readInput("${dir}/Part0101_test")
    val testInputPart102 = readInput("${dir}/Part0102_test")
    val testInputPart103 = readInput("${dir}/Part0103_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart101, 4, ::part1)
    verifySolution(testInputPart102, 7, ::part1)
    verifySolution(testInputPart103, 3, ::part1)
    //verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val rules = input.takeWhile { it.isNotBlank() }
        .map {
            val words = it.split(" => ")
            words[0] to words[1]
        }.groupBy(
            { it.first },
            { it.second },
        )

    val value = input.last()
    return expand(value, rules).size
}

fun part2(input: List<String>): Int {
    val rules = input.takeWhile { it.isNotBlank() }
        .map {
            val words = it.split(" => ")
            words[0] to words[1]
        }.groupBy(
            { it.first },
            { it.second },
        )

    val value = input.last()

    /**
     * https://www.reddit.com/r/adventofcode/comments/3xflz8/comment/cy4h7ji/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
     */
    val res = value.count { it.isUpperCase() } - value.occurrences("Rn") - value.occurrences("Ar") - 2 * value.occurrences("Y") - 1

    return res
}

fun String.occurrences(value: String): Int {
    if (this.isEmpty()) return 0
    var count = 0
    var index = 0

    while (true) {
        index = this.indexOf(string = value, startIndex = index)
        if (index == -1) break
        count++
        index += value.length
    }

    return count
}

fun expand(input: String, rules: Map<String, List<String>>): Set<String> {
    val seen = mutableSetOf<String>()

    for (idx in input.indices) {
        for ((pattern, replacements) in rules) {
            val match = input.startsWith(prefix = pattern, startIndex = idx)
            if (match) {
                seen.addAll(
                    replacements.map {
                        input.replaceRange(
                            startIndex = idx,
                            endIndex = idx + pattern.length,
                            replacement = it
                        )
                    }
                )
            }
        }
    }

    return seen
}