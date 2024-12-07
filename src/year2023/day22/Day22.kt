package year2023.day22

import utils.*

const val dir = "day22"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, -1, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")
    input.parse()

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return -1
}

fun part2(input: List<String>): Int {
    return -1
}

fun List<String>.parse() {
    val xMinMax = MutableMinMax()
    val yMinMax = MutableMinMax()
    val zMinMax = MutableMinMax()

    for (line in this) {
        val (start, end) = line.split("~")

        with(start) {
            val (x, y, z) = split(",").map { it.toLong() }
            xMinMax.update(x)
            yMinMax.update(y)
            zMinMax.update(z)
        }

        with(end) {
            val (x, y, z) = split(",").map { it.toLong() }
            xMinMax.update(x)
            yMinMax.update(y)
            zMinMax.update(z)
        }
    }

    println("X: $xMinMax")
    println("Y: $yMinMax")
    println("Z: $zMinMax")
}

class Plane : Grid<Int> by ArrayGrid(
    Array(10) {
        Array(10) {
            0
        }
    }
)