package year2024.day02

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import kotlin.math.abs

const val dir = "year2024/day02"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 2, ::part1)
    verifySolution(testInputPart2, 4, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input
        .map(String::parseLine)
        .filter(::isSafe)
        .size
}

fun String.parseLine(): IntArray = this
    .split(" ")
    .map(String::toInt)
    .toIntArray()

fun isSafe(readings: IntArray): Boolean {
    val differences = IntArray(readings.size - 1)
    for (index in 0 until readings.size - 1) {
        differences[index] = readings[index] - readings[index + 1]
    }

    val sign = differences[0].sign()
    if (sign == 0) return false
    val sameSign = differences.all { it.sign() == sign }
    val smallMagnitude = differences.all { abs(it) <= 3 }

    return sameSign and smallMagnitude
}

fun isSemiSafe(readings: IntArray): Boolean {
    if (isSafe(readings)) return true
    return readings.indices.any { isSafe(readings.withoutIndex(it)) }
}

fun part2(input: List<String>): Int {
    return input
        .map(String::parseLine)
        .filter(::isSemiSafe)
        .size
}

fun Int.sign(): Int {
    if (this < 0) return -1
    else if (this > 0) return 1;
    return 0;
}

fun IntArray.withoutIndex(index: Int): IntArray {
    return (this.sliceArray(0 until index) + this.sliceArray(index + 1 until this.size))
}