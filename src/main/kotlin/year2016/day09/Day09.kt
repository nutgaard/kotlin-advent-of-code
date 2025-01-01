package year2016.day09

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2016/day09"
fun main() {
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution("ADVENT", 6, ::decompressV1)
    verifySolution("A(1x5)BC", 7, ::decompressV1)
    verifySolution("(3x3)XYZ", 9, ::decompressV1)
    verifySolution("A(2x2)BCD(2x2)EFG", 11, ::decompressV1)
    verifySolution("(6x1)(1x3)A", 6, ::decompressV1)
    verifySolution("X(8x2)(3x3)ABCY", 18, ::decompressV1)

    verifySolution("X(8x2)(3x3)ABCY", 20, ::decompressV2)
    verifySolution("(3x3)XYZ", 9, ::decompressV2)
    verifySolution("(27x12)(20x12)(13x14)(7x10)(1x12)A", 241920, ::decompressV2)
    verifySolution("(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN", 445, ::decompressV2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    return input.map(::decompressV1).sum()
}

fun part2(input: List<String>): Long {
    return input.map(::decompressV2).sum()
}

fun decompressV1(input: String, v2: Boolean = false): Long {
    val markerPattern = Regex("\\((\\d+)x(\\d+)\\)")

    val markers = markerPattern.findAll(input).toList()

    if (markers.isEmpty()) return input.length.toLong()

    var length = 0L
    var current = 0L

    for (marker in markers) {
        if (marker.range.last < current) continue
        length += marker.range.first - current
        current = marker.range.last.toLong() + 1

        val (_, ch, repeat)  = marker.groupValues
        if (v2) {
            val chLen = ch.toInt()
            val substr = input.substring(marker.range.last + 1, marker.range.last + 1 + chLen)
            val nestedDecomp = decompressV1(substr, true)

            length += nestedDecomp * repeat.toLong()
            current += ch.toInt()
        } else {
            length += ch.toInt() * repeat.toLong()
            current += ch.toInt()
        }
    }
    length += input.length - current

    return length
}

fun decompressV2(input: String): Long {
    return decompressV1(input, true)
}