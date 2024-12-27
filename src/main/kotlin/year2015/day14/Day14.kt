package year2015.day14

import utils.*
import year2024.day21.score
import kotlin.math.min

const val dir = "year2015/day14"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 1120, bind(1000, ::part1))
    verifySolution(testInputPart2, 689, bind(1000, ::part2))

    val input = readInput("$dir/Input")

    timed("part1") { part1(2503, input).println() }
    timed("part2") { part2(2503, input).println() }
}

fun part1(time: Int, input: List<String>): Int {
    return input
        .map(::parse)
        .map { race(it, time) }
        .max()
}

fun part2(time: Int, input: List<String>): Int {
    val reindeer = input.map(::parse)
    val positions = reindeer.associate { it.name to it.simulate(time) }
    val scoring = reindeer.associate { it.name to 0 }.toMutableMap()

    for (i in 0 ..< time) {
        val p = positions.mapValues { it.value[i] }
        val max = p.maxOf { it.value }
        for ((k, v) in p) {
            if (v == max) {
                scoring[k] = (scoring[k] ?: 0) + 1
            }
        }
    }

    return scoring.maxOf { it.value }
}

fun race(reindeer: Reindeer, time: Int): Int {
    val cycleTime = reindeer.stamina + reindeer.rest
    val fullCycles = time / cycleTime
    val residual = time - (cycleTime * fullCycles)

    return (fullCycles * reindeer.stamina * reindeer.speed) + min(residual, reindeer.stamina) * reindeer.speed
}

data class Reindeer(
    val name: String,
    val speed: Int,
    val stamina: Int,
    val rest: Int,
) {
    fun simulate(time: Int): Array<Int> {
        var staminaTimer = stamina
        var restingTimer = 0
        var position = 0
        return buildList {
            for (i in 0 ..< time) {
                if (staminaTimer > 0) {
                    position += speed
                    staminaTimer--

                    if (staminaTimer == 0) {
                        restingTimer = rest
                    }
                } else if (restingTimer > 0) {
                    restingTimer--
                    if (restingTimer == 0) {
                        staminaTimer = stamina
                    }
                }
                add(position)
            }
        }.toTypedArray()
    }
}
fun parse(input: String): Reindeer {
    val words = input.split(" ")

    return Reindeer(
        name = words[0],
        speed = words[3].toInt(),
        stamina = words[6].toInt(),
        rest = words[13].toInt(),
    )
}