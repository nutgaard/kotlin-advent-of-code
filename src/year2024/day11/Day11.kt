package year2024.day11

import utils.*
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.log10

const val dir = "year2024/day11"
fun main() {
    val testInputPart1_1 = readInput("${dir}/Part01_1_test")
    val testInputPart1_2 = readInput("${dir}/Part01_2_test")

    verifySolution(testInputPart1_1, 7, bind(1, ::part1))
    verifySolution(testInputPart1_2, 3, bind(1, ::part1))
    verifySolution(testInputPart1_2, 4, bind(2, ::part1))
    verifySolution(testInputPart1_2, 5, bind(3, ::part1))
    verifySolution(testInputPart1_2, 9, bind(4, ::part1))
    verifySolution(testInputPart1_2, 13, bind(5, ::part1))
    verifySolution(testInputPart1_2, 22, bind(6, ::part1))
    verifySolution(testInputPart1_2, 55312, bind(25, ::part1))

    val input = readInput("$dir/Input")

    timed("part1") { part1(25, input).println() }
    timed("part2") { part1(75, input).println() }
}

fun part1(blinkCount: Int, input: List<String>): Long {
    val row = input[0].split(" ").map { it.toLong() }
    return row
        .sumOf { process(blinkCount, it, 0) }
}

val memoTable = mutableMapOf<String, Long>()
fun process(maxStep: Int, value: Long, step: Int): Long {
    if (step == maxStep) return 1

    val valueStr = value.toString()
    val key = "$maxStep $valueStr $step"
    if (memoTable.contains(key)) return memoTable[key]!!

    val expandedValue: Long = when {
        value == 0L -> process(maxStep,1, step + 1)
        valueStr.length and 1 == 0 -> {
            val halfPoint = valueStr.length shr 1
            val first = valueStr.substring(0, halfPoint).toLong()
            val second = valueStr.substring(halfPoint).toLong()
            process(maxStep, first, step + 1) + process(maxStep, second, step + 1)
        }
        else -> process(maxStep,value * 2024, step + 1)
    }
    memoTable[key] = expandedValue

    return expandedValue
}

fun Long.length(): Int {
    return when (this) {
        0L -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }
}