package year2016.day04

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2016/day04"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 1514, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input
        .map(::Room)
        .filter(Room::validate)
        .sumOf(Room::sectorId)
}

fun part2(input: List<String>): Int {
    return input
        .map(::Room)
        .filter(Room::validate)
        .firstOrNull { it.decrypted() == "northpole object storage" }
        ?.sectorId()
        ?: -1
}

data class Room(
    val roomName: String,
) {
    fun name(): String {
        return roomName
            .takeWhile { !it.isDigit() }
            .dropLast(1)
    }

    fun sectorId(): Int {
        return roomName
            .drop(name().length + 1)
            .takeWhile { it != '[' }
            .toInt()
    }

    fun validate(): Boolean {
        val checksum = roomName
            .takeLastWhile { it != '[' }
            .dropLast(1)

        val extractedChecksum = name()
            .toCharArray()
            .filter { it != '-' }
            .groupingBy { it }
            .eachCount()
            .entries
            .asSequence()
            .map { Pair(it.key, it.value) }
            .sortedWith(
                compareByDescending<Pair<Char, Int>> {
                    it.second
                }.thenBy {
                    it.first
                }
            )
            .take(5)
            .map { it.first }
            .joinToString("")

        return checksum == extractedChecksum
    }

    fun decrypted(): String {
        val shift = sectorId() % 26
        return name()
            .toCharArray()
            .map { ch ->
                if (ch == '-') {
                    ' '
                } else {
                    val base = ch - 'a'
                    val new = (base + shift) % 26
                    'a' + new
                }
            }
            .joinToString("")
    }
}