package year2024.day25

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2024/day25"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 3, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val (keys, locks) = parse(input)

    var fit = 0
    for (lock in locks) {
        for (key in keys) {
            val lockHeights = lock.pinheights
            val keyHeights = key.pinheights

            val fits = lockHeights.zip(keyHeights)
                .all { (a, b) -> a + b < 6  }
            if (fits) fit++
        }
    }

    return fit
}

fun part2(input: List<String>): Int {
    return -1
}

fun parse(input: List<String>): Pair<List<KeyOrLock.Key>, List<KeyOrLock.Lock>> {
    val keysAndLocks = input
        .asSequence()
        .chunked(8)
        .map { it.dropLastWhile { it.isBlank() } }
        .map {
            if (it.first() == ".....") {
                KeyOrLock.Key(it)
            } else {
                KeyOrLock.Lock(it)
            }
        }
        .toList()

    return Pair(
        keysAndLocks.filterIsInstance<KeyOrLock.Key>(),
        keysAndLocks.filterIsInstance<KeyOrLock.Lock>(),
    )
}

sealed class KeyOrLock {
    class Key(
        private val rows: List<String>,
    ) : KeyOrLock() {
        val pinheights: IntArray get() {
            val columns = Array(rows.first().length) { column ->
                rows.map { it[column] }.count { it == '#' } - 1
            }

            return columns.toIntArray()
        }
    }

    class Lock(
        private val rows: List<String>,
    ) : KeyOrLock() {
        val pinheights: IntArray get() {
            val columns = Array(rows.first().length) { column ->
                rows.map { it[column] }.count { it == '#' } - 1
            }
            return columns.toIntArray()
        }
    }
}