package year2024.day22

import utils.*
import utils.Trie.Node
import java.util.LinkedList

const val dir = "year2024/day22"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 37327623, ::part1)
    verifySolution(testInputPart2, 23, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    val secrets = input
        .map(String::toLong)
        .map {
            generateSequence(it, ::pseudorandom)
                .drop(2000)
                .take(1)
                .toList()
                .first()
        }

    return secrets.sum()
}

fun part2(input: List<String>): Long {
    val sequences = Array(20 * 20 * 20 * 20) { 0L }

    for (line in input) {
        processMonkey(line.toLong(), sequences)
    }

    return sequences.max()
}

fun processMonkey(secret: Long, sequences: Array<Long>) {
    var current = secret

    var currentCost = (current % 10).toInt()
    var previousCost = (current % 10).toInt()

    val seenSequences = Array(20 * 20 * 20 * 20) { false }
    val currentSequence = LinkedList<Int>()

    for (i in 0 until 2000) {
        currentSequence.add(currentCost - previousCost)
        previousCost = currentCost

        current = pseudorandom(current)
        currentCost = (current % 10).toInt()

        if (currentSequence.size == 4) {
            val idx =
                (currentSequence[0] + 10) +
                20 * (currentSequence[1] + 10) +
                400 * (currentSequence[2] + 10) +
                8000 * (currentSequence[3] + 10)

            val seen = seenSequences[idx]
            if (!seen) {
                sequences[idx] += previousCost.toLong()
                seenSequences[idx] = true
            }
            currentSequence.pollFirst()
        }
    }
}

fun pseudorandom(secret: Long): Long {
    var value = secret
    value = (value xor (value shl 6)) % 16777216
    value = (value xor (value shr 5)) % 16777216
    value = (value xor (value shl 11)) % 16777216
    return value
}