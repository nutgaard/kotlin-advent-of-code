package day01

import println
import readInput
import timed
import verifySolution

const val dir = "day01"

fun main() {
    val testInputPart1 = readInput("$dir/Part01_test")
    val testInputPart2 = readInput("$dir/Part02_test")

    verifySolution("zoneight", "18", ::decodeDigits)
    verifySolution("zoneight234", "18234", ::decodeDigits)
    verifySolution(testInputPart1, 142, ::part1)
    verifySolution(testInputPart2, 281, ::part2)

    val input = readInput("$dir/Input")
    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input.map(::calibrationValue).sum()
}

fun part2(input: List<String>): Int {
    return input.map(::decodeDigits).map(::calibrationValue).sum()
}

fun calibrationValue(line: String): Int {
    val first = line.first { it.isDigit() }
    val last = line.last { it.isDigit() }

    return "$first$last".toInt()
}

val digitMap = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)
val digitMapRegexp = Regex("^(?:${digitMap.keys.joinToString("|")})")

fun decodeDigits(line: String): String {
    return buildString {
        line.forEachIndexed { index, c ->
            if (c.isDigit()) {
                append(c)
            } else {
                val substring = line.substring(index)
                val digitMatch = digitMapRegexp.find(substring, 0)?.value
                if (digitMatch != null) {
                    append(digitMap[digitMatch])
                }
            }
        }
    }
}