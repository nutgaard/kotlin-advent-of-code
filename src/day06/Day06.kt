package day06

import println
import readInput
import timed
import verifySolution

const val dir = "day06"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 288, ::part1)
    verifySolution(testInputPart2, 71503, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    val times = input.getLine("Time:")
    val distances = input.getLine("Distance:")
    return times.zip(distances)
        .map(::simulateRace)
        .product()
}

fun part2(input: List<String>): Long {
    val time = input.getLineWithKerningFix("Time:")
    val distance = input.getLineWithKerningFix("Distance:")
    return simulateRace(time to distance)
}

fun simulateRace(raceSpec: Pair<Long, Long>): Long {
    val (time, recordDistance) = raceSpec
    var recordBreaking = 0L
    for (btnHold in 0..time) {
        val speed = btnHold
        val timeLeft = time - btnHold
        val distance = speed * timeLeft
        if (distance > recordDistance) {
            recordBreaking++
        }
    }

    return recordBreaking
}

fun List<String>.getLine(prefix: String): List<Long> {
    return this
        .find { it.startsWith(prefix) }
        ?.removePrefix(prefix)
        ?.split(Regex("\\s+"))
        ?.map(String::trim)
        ?.filter { it.isNotEmpty() }
        ?.map(String::toLong)
        ?: emptyList()
}

fun List<String>.getLineWithKerningFix(prefix: String): Long {
    return this
        .find { it.startsWith(prefix) }
        ?.removePrefix(prefix)
        ?.replace(" ", "")
        ?.let(String::toLong)
        ?: 0
}

fun Iterable<Long>.product(): Long {
    var product: Long = 1
    for (element in this) {
        product *= element
    }
    return product
}