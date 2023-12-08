package day08

import println
import readInput
import timed
import verifySolution

const val dir = "day08"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 6, ::part1)
    verifySolution(testInputPart2, 6, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    val wastelandMap = WastelandMap.parse(input)
    var stepCount = 0L
    var location = "AAA"
    do {
        val instruction = wastelandMap.instruction(stepCount++)
        location = wastelandMap.step(location, instruction)
    } while (location != "ZZZ")

    return stepCount
}

fun part2(input: List<String>): Long {
    val wastelandMap = WastelandMap.parse(input)
    val locations = wastelandMap.map.keys.filter { it.endsWith('A') }
    val cycles = mutableListOf<Long>()

    for (location in locations) {
        var current = location
        var stepCount = 0L
        do {
            val instruction = wastelandMap.instruction(stepCount++)
            current = wastelandMap.step(current, instruction)
        } while (!current.endsWith('Z'))
        cycles.add(stepCount)
    }

    return lcm(*cycles.toLongArray())
}

class WastelandMap(
    private val instructions: String,
    val map: Map<String, Pair<String, String>>
) {
    private val instructionLength = instructions.length

    companion object {
        fun parse(lines: List<String>): WastelandMap {
            val instructions = lines.first()
            val map = mutableMapOf<String, Pair<String, String>>()
            for (line in lines) {
                if (line.contains(" = ")) {
                    val (sourceStr, destinationStr) = line.split(" = ")
                    val source = sourceStr.trim()
                    val (leftDestination, rightDestination) = destinationStr
                        .removePrefix("(").removeSuffix(")")
                        .split(",")
                        .map { it.trim() }
                    map[source] = leftDestination to rightDestination
                }
            }

            return WastelandMap(instructions, map)
        }
    }

    fun instruction(step: Long): Char {
        val idx = step % instructionLength
        return instructions[idx.toInt()]
    }

    fun step(source: String, direction: Char): String {
        val node = requireNotNull(map[source])
        return if (direction == 'L') node.first
        else node.second
    }
}

fun gcd(first: Long, second: Long): Long {
    var a = first
    var b = second
    while (b != 0L) {
        val t = b
        b = a % b
        a = t
    }
    return a
}
fun lcm(a: Long, b: Long): Long {
    return a * b / gcd(a, b)
}
fun lcm(vararg numbers: Long): Long {
    return numbers.reduce(::lcm)
}