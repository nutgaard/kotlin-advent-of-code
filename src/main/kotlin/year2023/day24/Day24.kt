package year2023.day24

import year2023.day11.pairs
import utils.*
import java.math.BigDecimal

const val dir = "day24"

fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test").parse()
    val testInputPart2 = readInput("${dir}/Part02_test").parse()

    verifySolution(testInputPart1, 2, bind(BigDecimal("7")..BigDecimal("27"), ::part1))
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input").parse()

    timed("part1") { part1(BigDecimal("200000000000000")..BigDecimal("400000000000000"), input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(boundry: ClosedRange<BigDecimal>, input: List<Hailstone>): Int {
    return input.pairs()
        .filter { (first, second) -> first != second }
        .map { (a, b) -> Triple(a, b, intersection2D(a, b)) }
        .count { (a, b, intersection) ->
            if (intersection == null) return@count false
            a.isInTheFuture(intersection) && b.isInTheFuture(intersection) && intersection.x in boundry && intersection.y in boundry
        }
}

fun part2(input: List<Hailstone>): Int {
    return -1
}

fun List<String>.parse(): List<Hailstone> {
    return this.map {
        val (position, velocity) = it.split("@")
        Hailstone(
            position = Point.parse(position),
            velocity = Vector.parse(velocity),
        )
    }
}

data class Point(
    val x: BigDecimal,
    val y: BigDecimal,
    val z: BigDecimal,
) {
    companion object {
        fun parse(text: String): Point {
            val (x, y, z) = text.split(",").map { it.trim() }
            return Point(
                x = BigDecimal(x),
                y = BigDecimal(y),
                z = BigDecimal(z),
            )
        }
    }
}

data class Vector(
    val x: BigDecimal,
    val y: BigDecimal,
    val z: BigDecimal,
) {
    companion object {
        fun parse(text: String): Vector {
            val (x, y, z) = text.split(",").map { it.trim() }
            return Vector(
                x = BigDecimal(x),
                y = BigDecimal(y),
                z = BigDecimal(z),
            )
        }
    }
}

data class Hailstone(
    val position: Point,
    val velocity: Vector,
) {
    fun isInTheFuture(other: Point): Boolean {
        val deltaX = other.x - position.x
        val deltaY = other.y - position.y

        return deltaY.signum() == velocity.y.signum()
    }
}

fun intersection2D(a: Hailstone, b: Hailstone): Point? {
    val deltaA = a.velocity.y / a.velocity.x
    val deltaB = b.velocity.y / b.velocity.x
    if (deltaA == deltaB) return null

    val divisor = deltaA - deltaB
    val dividend = b.position.y - a.position.y + deltaA * a.position.x - deltaB * b.position.x

    val x = dividend / divisor
    val y = a.position.y + a.velocity.y * ((x - a.position.x)/a.velocity.x)

    return Point(x, y, BigDecimal.ZERO)
}