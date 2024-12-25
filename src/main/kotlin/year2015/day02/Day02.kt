package year2015.day02

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2015/day02"
fun main() {
    verifySolution("2x3x4", 58, ::wrappingSize)
    verifySolution("1x1x10", 43, ::wrappingSize)
    verifySolution("2x3x4", 34, ::ribbonLength)
    verifySolution("1x1x10", 14, ::ribbonLength)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

data class Box(
    val l: Int,
    val w: Int,
    val h: Int,
) {
    companion object {
        fun parse(value: String): Box {
            val (l, w, h) = value.split("x").map(String::toInt)
            return Box(l = l, w = w, h = h)
        }
    }
    val sideAreas: List<Int> get() = listOf(l*w, w*h, h*l)
    val surfaceArea: Int get() = sideAreas.map { it * 2 }.sum()
    val volume: Int = l*w*h
    val wrappingSlack: Int get() = sideAreas.min()
    val perimeters: List<Int> get() = listOf(
        2*l + 2*w,
        2*l + 2*h,
        2*w + 2*h,
    )
}

fun part1(input: List<String>): Int {
    return input.map(::wrappingSize).sum()
}

fun wrappingSize(value: String): Int {
    val box = Box.parse(value)
    return box.surfaceArea + box.wrappingSlack
}

fun ribbonLength(value: String): Int {
    val box = Box.parse(value)
    return box.volume + box.perimeters.min()
}

fun part2(input: List<String>): Int {
    return input.map(::ribbonLength).sum()
}