package year2024.day14

import utils.*
import kotlin.math.max

const val dir = "year2024/day14"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    val testConfig = Config(
        cycles = 100,
        dimension = Dimension(7, 11),
        debug = false
    )

    verifySolution(testInputPart1, 12, bind(testConfig, ::part1))
//    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    val config = Config(100, Dimension(103, 101), debug = false)
    timed("part1") { part1(config, input).println() }
    timed("part2") { part2(config, input).println() }
}

data class Config(
    val cycles: Int,
    val dimension: Dimension,
    val debug: Boolean = false
)
fun part1(config: Config, input: List<String>): Int {
    val robots = input.map(::parseRobot)

    debug("Initial state:", config, robots)

    repeat(config.cycles) { cycle ->
        robots.forEach { it.move(config.dimension) }
        debug("After ${cycle + 1} seconds:", config, robots)
    }

    val wHalf = config.dimension.width / 2
    val hHalf = config.dimension.height / 2


    val topLeft = Quadrant(
        x = 0 until wHalf,
        y = 0 until hHalf,
    )
    val topRight = Quadrant(
        x = wHalf + 1 until config.dimension.width,
        y = 0 until hHalf,
    )
    val bottomLeft = Quadrant(
        x = 0 until wHalf,
        y = hHalf + 1 until config.dimension.height,
    )
    val bottomRight = Quadrant(
        x = wHalf + 1 until config.dimension.width,
        y = hHalf + 1 until config.dimension.height,
    )
    val quadrants = listOf(
        topLeft,
        topRight,
        bottomLeft,
        bottomRight,
    )

    return robots
        .fold(mutableMapOf<Quadrant, Int>()) { acc, robot ->
            val quads = quadrants.filter { it.contains(robot.position) }
            quads.forEach {
                acc[it] = acc.getOrDefault(it, 0) + 1
            }
            acc
        }
        .values
        .reduce { a, b -> a * b }
}

class Quadrant(
    val x: IntRange,
    val y: IntRange,
) {
    fun contains(coordinate: Coordinate): Boolean {
        return coordinate.column in x && coordinate.row in y
    }
}

fun part2(config: Config, input: List<String>): Int {
    val robots = input.map(::parseRobot)

    debug("Initial state:", config, robots)

    var cycle = 0
    var found = false
    var grid = ArrayGrid.create(config.dimension) { 0 }

    while (!found) {
        // Move
        robots.forEach { it.move(config.dimension) }
        cycle++

        grid = grid.map { 0 }
        for (robot in robots) {
            val value = grid.getValue(robot.position)
            grid.setValue(robot.position, value + 1)
        }

        val horizontalLines = grid.rowIndices
            .asSequence()
            .map { grid.grid[it] }
            .map { countConsequtiveInts(it) }
            .filter { it > 8 }
            .count()
        found = horizontalLines >= 2
        if (horizontalLines >= 2) {
            println("Cycle: $cycle Lines: $horizontalLines")
            grid.prettyPrint()
            println()
        }
    }

    return cycle
}

fun countConsequtiveInts(array: Array<Int>): Int {
    var current = 0
    var largest = 0
    for (element in array) {
        if (element != 1) {
            largest = max(largest, current)
            current = 0
        } else {
            current++
        }
    }
    largest = max(largest, current)
    return largest
}

fun debug(
    heading: String,
    config: Config,
    robots: List<Robot>
) {
    if (!config.debug) return

    val grid = ArrayGrid.create(config.dimension) { 0 }
    for (robot in robots) {
        val value = grid.getValue(robot.position)
        grid.setValue(robot.position, value + 1)
    }

    println(heading)
    grid.prettyPrint()
    println()
}

fun ArrayGrid<Int>.prettyPrint() {
    println(this.asString { _, _, v ->
        if (v == 0) "."
        else v.toString()
    })
}

data class Robot(
    var position: Coordinate,
    var velocity: Vector,
) {
    fun move(dimension: Dimension) {
        position = Coordinate.of(
            row = (position.row + velocity.y + dimension.height) % dimension.height,
            column = (position.column + velocity.x + dimension.width) % dimension.width,
        )
    }
}

fun parseRobot(line: String): Robot {
    val (pNums, vNums) = line
        .split(" ")
        .map { it.substring(2) }
        .map { it.split(",").map(String::toInt) }

    return Robot(
        position = Coordinate.of(pNums[1], pNums[0]),
        velocity = Vector(vNums[0], vNums[1]),
    )
}

