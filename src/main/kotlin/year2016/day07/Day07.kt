package year2016.day07

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import year2024.day04.findAllWithOverlap

const val dir = "year2016/day07"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 2, ::part1)
    verifySolution(testInputPart2, 3, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input
        .map(IPv7::parse)
        .count(IPv7::supportsTLS)
}

fun part2(input: List<String>): Int {
    return input
        .map(IPv7::parse)
        .count(IPv7::supportsSSL)
}

data class IPv7(
    val supernet: List<String>,
    val hypernet: List<String>,
) {
    companion object {
        fun parse(input: String): IPv7 {
            val supernets = mutableListOf<String>()
            val hypernets = mutableListOf<String>()
            var withinBracets = false
            val chars = input.toCharArray()

            var buffer = StringBuilder()
            for (char in chars) {
                if (char == '[') {
                    if (buffer.length > 0) {
                        supernets.add(buffer.toString())
                        buffer.clear()
                    }
                } else if (char == ']') {
                    if (buffer.length > 0) {
                        hypernets.add(buffer.toString())
                        buffer.clear()
                    }
                } else {
                    buffer.append(char)
                }
            }

            if (buffer.length > 0) {
                supernets.add(buffer.toString())
                buffer.clear()
            }

            return IPv7(
                supernets,
                hypernets
            )
        }
    }

    fun supportsTLS(): Boolean {
        val abbaPattern = Regex("(.)((?!\\1).)\\2\\1")
        val hasHypernetABBA = hypernet.any { abbaPattern.find(it) != null }
        val hasSupernetABBA = supernet.any { abbaPattern.find(it) != null }
        if (hasHypernetABBA) return false

        return hasSupernetABBA
    }

    fun supportsSSL(): Boolean {
        val abaPattern = Regex("(.)((?!\\1).)\\1")
        val abaMatches = supernet
            .flatMap { abaPattern.findAllWithOverlap(it) }
            .map { it.groupValues[1] to it.groupValues[2] }

        if (abaMatches.isEmpty()) return false

        val babPatterns = abaMatches.map { (a, b) -> "$b$a$b" }
        val babPattern = Regex(babPatterns.joinToString("|"))

        return hypernet.any { babPattern.find(it) != null }
    }
}