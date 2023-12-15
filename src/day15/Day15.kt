package day15

import println
import readInput
import timed
import verifySolution

const val dir = "day15"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution("HASH", 52, ::asciihash)
    verifySolution(testInputPart1, 1320, ::part1)
    verifySolution(testInputPart2, 145, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input
        .joinToString()
        .split(",")
        .map(String::trim)
        .sumOf(::asciihash)
}

fun part2(input: List<String>): Int {
    val boxes = Array(256) { Box() }
    val commands = input
        .joinToString()
        .split(",")
        .map(String::trim)

    for (command in commands) {
        val (label, value) = command.split('-', '=')
        val boxId = asciihash(label)
        val box = boxes[boxId]

        if (command.endsWith('-')) {
            box.removeLensWithLabel(label)
        } else {
            box.addLens(label, value.toInt())
        }
    }

    return boxes.mapIndexed { boxNo, box ->
        box.lenses.mapIndexed { lensNo, lens ->
            (boxNo + 1) * (lensNo + 1) * lens.focalLength
        }.sum()
    }.sum()
}

data class Lens(val label: String, var focalLength: Int)
data class Box(val lenses: MutableList<Lens> = mutableListOf()) {
    // TODO maybe use hashmap
    fun removeLensWithLabel(label: String) {
        lenses.removeIf { it.label == label }
    }
    fun addLens(label: String, focalLength: Int) {
        val existingLens = lenses.indexOfFirst { it.label == label }
        if (existingLens >= 0) {
            lenses[existingLens].focalLength = focalLength
        } else {
            lenses.add(Lens(label, focalLength))
        }
    }
}

fun asciihash(value: String): Int {
    return value.fold(0) { acc, ch ->
        var n = acc + ch.code
        n *= 17
        n %= 256
        n
    }
}