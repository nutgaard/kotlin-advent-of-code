package year2016.day05

import utils.*

const val dir = "year2016/day05"
fun main() {
    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): String {
    return generateSimplePassword(
        seed = "reyedfim",
        indicator = "00000"
    )
}

fun part2(input: List<String>): String {
    return generateAdvancedPassword(
        seed = "reyedfim",
        indicator = "00000"
    )
}

fun hashes(seed: String): Sequence<String> {
    return sequence {
        var i = 0
        while (true) {
            val hash = md5("$seed${i++}")
            yield(hash)
        }
    }
}

fun generateSimplePassword(
    seed: String,
    indicator: String,
    length: Int = 8
): String {
    return hashes(seed)
        .filter { it.startsWith(indicator) }
        .take(length)
        .map { it[indicator.length] }
        .joinToString("")
}

fun generateAdvancedPassword(
    seed: String,
    indicator: String,
    length: Int = 8
): String {
    var count = 0
    var password = Array(length) { ' ' }
    val hashes = hashes(seed)
        .filter { it.startsWith(indicator) }

    for (hash in hashes) {
        val position = hash[indicator.length].digitToIntOrNull()
        if (position == null || position >= length) continue
        if (password[position] != ' ') continue

        val char = hash[indicator.length + 1]
        password[position] = char
        count++

        if (count == length) break
    }

    return password.joinToString("")
}