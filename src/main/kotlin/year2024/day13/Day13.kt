package year2024.day13

import utils.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

const val dir = "year2024/day13"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 480, ::part1)
//    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val games = input.parse()
    return games
        .map { game -> game.solvePart1().prepend(game) }
        .filter { (game, a, b) ->
            a.toDouble() in 0.0..100.0 &&
            b.toDouble() in 0.0..100.0 &&
            a.isIntIsh() &&
            b.isIntIsh()
        }
        .sumOf { (game, a, b) ->
            game.btnA.cost * a.toDouble() + game.btnB.cost * b.toDouble()
        }
        .toInt()
}

fun part2(input: List<String>): Long {
    val games = input.parse()
    return games
        .map { game -> Game(
            btnA = game.btnA,
            btnB = game.btnB,
            prize = Prize(
                x = 10000000000000L + game.prize.x,
                y = 10000000000000L + game.prize.y,
            )
        ) }
        .map { game -> game.solvePart1().prepend(game) }
        .filter { (game, a, b) ->
            a.toDouble() > 0 &&
            b.toDouble() > 0 &&
            a.isIntIsh() &&
            b.isIntIsh()
        }
        .sumOf { (game, a, b) ->
            game.btnA.cost * a.toDouble() + game.btnB.cost * b.toDouble()
        }
        .toLong()
}

fun List<String>.parse(): List<Game> {
    val iterator = this.iterator()
    return buildList {
        while (iterator.hasNext()) {
            val btnALine = iterator.next()
            val btnBLine = iterator.next()
            val prizeLine = iterator.next()
            runCatching { iterator.next() } // Read empty line if possible

            add(
                Game(
                    btnA = Button.fromString(btnALine, 3),
                    btnB = Button.fromString(btnBLine, 1),
                    prize = Prize.fromString(prizeLine)
                )
            )
        }
    }
}

open class XY(open val x: Long, open val y: Long)
data class Button(
    val name: String,
    val cost: Int,
    override val x: Long,
    override val y: Long
): XY(x, y) {
    companion object {
        fun fromString(str: String, cost: Int): Button {
            assert(str.startsWith("Button"))
            val (btn, values) = str.split(":")
            val name = btn.last().toString()
            val (x, y) = values.split(",")
                .map(String::trim)
                .map{ it.substring(2).toLong() }

            return Button(
                name = name,
                cost = cost,
                x = x,
                y = y
            )
        }
    }
}
data class Prize(
    override val x: Long,
    override val y: Long
): XY(x, y) {
    companion object {
        fun fromString(str: String): Prize {
            assert(str.startsWith("Prize:"))
            val (_, values) = str.split(":")
            val (x, y) = values.split(",")
                .map(String::trim)
                .map{ it.substring(2).toLong() }

            return Prize(x, y)
        }
    }
}

data class Game(
    val btnA: Button,
    val btnB: Button,
    val prize: Prize,
) {
    fun solvePart1(): Pair<BigDecimal, BigDecimal> {
        val eq1 = Eq(a = btnA.x, b = btnB.x, r = prize.x)
        val eq2 = Eq(a = btnA.y, b = btnB.y, r = prize.y)

        return eqSolve(eq1, eq2)
    }

    private val mc = MathContext(32, RoundingMode.HALF_UP)
    private fun eqSolve(
        eq1: Eq,
        eq2: Eq,
    ): Pair<BigDecimal, BigDecimal> {
        val eq1A = eq1.a
        val eq2A = eq2.a

        eq1.multiply(eq2A)
        eq2.multiply(eq1A)

        val eq3 = eq1.subtract(eq2)

        val bEstimate = eq3.r.divide(eq3.b, mc)
        val aEstimate = eq1.r.minus(bEstimate.multiply(eq1.b)).divide(eq1.a, mc)
        return aEstimate to bEstimate
    }
}

data class Eq(
    var a: BigDecimal,
    var b: BigDecimal,
    var r: BigDecimal
) {
    companion object {
        operator fun invoke(a: Long, b: Long, r: Long) = Eq(a.toBigDecimal(), b.toBigDecimal(), r.toBigDecimal())
    }
    fun multiply(factor: BigDecimal) {
        a *= factor
        b *= factor
        r *= factor
    }

    fun subtract(eq: Eq) = Eq(
        a = this.a - eq.a,
        b = this.b - eq.b,
        r = this.r - eq.r,
    )
}

fun BigDecimal.isIntIsh(): Boolean {
    return this.toBigInteger().toBigDecimal() == this
}

fun <A, B, C> Pair<A, B>.prepend(other: C): Triple<C, A, B> = Triple(other, this.first, this.second)