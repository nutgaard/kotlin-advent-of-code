package year2024.day09

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2024/day09"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")[0]
    val testInputPart2 = readInput("${dir}/Part02_test")[0]

    verifySolution(testInputPart1, 1928, ::part1)
    verifySolution(testInputPart2, 2858, ::part2)

    val input = readInput("$dir/Input")[0]

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: String): Long {
    val expanded = expandInput(input)
    compactDiskspace(expanded)

    return expanded.mapIndexed { index, c ->
        val v = if (c == ".") 0 else Integer.parseInt(c)
        (index * v).toLong()
    }.sum()
}

fun expandInput(input: String): ArrayList<String> {
    var fileId = 0
    var isFile = true

    val chars = input.toCharArray()

    val expanded = buildList {
        for (c in chars) {
            val v = Integer.parseInt(c.toString())
            if (isFile) {
                repeat(v) {
                    add("$fileId")
                }
                fileId++
            } else {
                repeat(v) {
                    add(".")
                }
            }
            isFile = !isFile
        }
    }

    return ArrayList(expanded)
}

fun compactDiskspace(disk: ArrayList<String>): ArrayList<String> {
    val availableSpaces = disk
        .mapIndexed { index, c -> if (c == ".") index else -1 }
        .filter { it != -1 }
    val files = disk
        .mapIndexed { index, c -> if (c != ".") index else -1 }
        .filter { it != -1 }

    val minimumLength = files.size

    val compactionPlan = availableSpaces.zip(files.reversed())
    for ((freespaceIdx, fileIdx) in compactionPlan) {
        if (freespaceIdx > minimumLength) break
        val fileValue = disk[fileIdx]
        disk[freespaceIdx] = fileValue
        disk[fileIdx] = "."
    }

    return disk
}

fun part2(input: String): Long {
    val expanded = filecompaction(expandInput(input))

    return expanded.mapIndexed { index, c ->
        val v = if (c == ".") 0 else Integer.parseInt(c)
        (index * v).toLong()
    }.sum()
}

fun filecompaction(disk: ArrayList<String>): List<String> {
    val continousSpaces = findContinuous(disk)
    val files = continousSpaces.filter { it.value != "." }.reversed()

    for (file in files) {
        val freespaceIdx = continousSpaces.indexOfFirst { it.value == "." && it.size >= file.size }
        if (freespaceIdx < 0) continue
        val freespace = continousSpaces[freespaceIdx]
        if (file.index <= freespace.index) continue

        continousSpaces.add(freespaceIdx, file.copy())
        file.value = "."

        freespace.index += file.size
        freespace.size -= file.size
    }

    return continousSpaces.asList()
}

data class ContinuousSpace(
    var index: Int,
    var size: Int,
    var value: String
)
fun findContinuous(disk: ArrayList<String>): MutableList<ContinuousSpace> {
    val out = mutableListOf<ContinuousSpace>()
    var current: String? = null
    var startIndex: Int? = null

    for (index in disk.indices) {
        val value = disk[index]
        if (current == null) {
            current = value
            startIndex = index
        } else if (current != value) {
            out.add(ContinuousSpace(startIndex!!, index - startIndex, current))
            current = value
            startIndex = index
        }
    }
    if (current != null) {
        out.add(ContinuousSpace(startIndex!!, disk.size - startIndex, current))
    }

    return out
}

fun List<ContinuousSpace>.asList(): List<String> = buildList {
    for (space in this@asList) {
        repeat(space.size) { add(space.value) }
    }
}