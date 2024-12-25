package year2015.day08

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2015/day08"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 12, ::part1)
    verifySolution(testInputPart2, 19, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input.map { it.length - actualChars(it) }.sum()
}

fun part2(input: List<String>): Int {
    return input.map { encode(it).length - it.length }.sum()
}

fun actualChars(input: String): Int {
    val chars = input.toCharArray().drop(1).dropLast(1)
    var escaped = 0

    var idx = 0
    while (idx < chars.size - 1) {
        if (chars[idx] == '\\') {
            if (chars[idx + 1] == 'x') {
                escaped += 3
                idx += 4
            } else {
                escaped += 1
                idx += 2
            }
        } else {
            idx += 1
        }
    }

    return chars.size - escaped
}

fun encode(input: String): String {
    val chars = input.toCharArray()
    return buildString {
        append('"')
        for (ch in chars) {
            if (ch == '"') append("""\"""")
            else if (ch == '\\') append("\\\\")
            else append(ch)
        }
        append('"')
    }
}