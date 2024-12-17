package year2024.day03

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2024/day03"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 161, ::part1)
    verifySolution(testInputPart2, 48, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

val multiplicationPattern = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")
val flagPattern = Regex("(?:do|don't)\\(\\)")
fun part1(input: List<String>): Int {
    return input
        .joinToString()
        .let { multiplicationPattern.findAll(it) }
        .map { (it.groups[1]?.value?.toInt() ?: 0) * (it.groups[2]?.value?.toInt() ?: 0) }
        .sum()
}

data class Multiplication(
    val position: Int,
    val factorA: Int,
    val factorB: Int
) {
    fun product(): Int = factorA * factorB

    fun isEnabled(flags: List<Flag>): Boolean {
        for (flag in flags) {
            if (flag.position < this.position) return flag.enable
        }
        return true
    }
}
data class Flag(
    val position: Int,
    val enable: Boolean
)

fun part2(input: List<String>): Int {
    val all = input.joinToString()
    val multiplications = all
        .let { multiplicationPattern.findAll(it) }
        .map {
            Multiplication(
                it.range.first,
                it.groups[1]?.value?.toInt() ?: 0,
                it.groups[2]?.value?.toInt() ?: 0
            )
        }
        .toList()

    val flags = all
        .let { flagPattern.findAll(it) }
        .map {
            Flag(
                it.range.first,
                it.value == "do()"
            )
        }
        .toList()
        .reversed()

    return multiplications
        .filter { it.isEnabled(flags) }
        .sumOf { it.product() }
}