package day03

import println
import readInput
import verifySolution
import kotlin.math.max
import kotlin.math.min

const val dir = "day03"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 4361, ::part1)
    verifySolution(testInputPart2, 467835, ::part2)

    val input = readInput("$dir/Input")

    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Int {
    val schematic = Schematic(input)
    return schematic.findPartNumbers()
        .filter {
            val boundingbox = it.boundingbox.getBorder()
            val hasAdjecentSymbol = boundingbox.any { (r, c) -> schematic.getPoint(r, c) != "." }
            hasAdjecentSymbol
        }
        .sumOf { it.value }
}

fun part2(input: List<String>): Int {
    val schematic = Schematic(input)
    val parts = schematic.findPartNumbers()
    val gears = schematic.findGears(parts)

    return gears.sumOf { it.ratio() }
}

class Schematic(val rows: List<String>) {
    private val partRegex = Regex("\\d+")
    private val gearRegex = Regex("\\*")
    private val grid = rows
        .map { it.split("").filter { it.isNotEmpty() } }

    fun findPartNumbers(): List<PartNumber> {
        return rows
            .flatMapIndexed { index, row ->
                partRegex.findAll(row).map { index to it }
            }
            .map { (row, match) ->
                PartNumber(
                    row = row,
                    column = match.range,
                    value = match.value.toInt()
                )
            }
    }

    fun findGears(parts: List<PartNumber>): List<Gear> {
        return rows
            .flatMapIndexed { index, row ->
                gearRegex.findAll(row).map { index to it.range.first  }
            }
            .mapNotNull {(r, c) ->
                val connectedParts = parts.filter { it.boundingbox.includes(r, c) }
                if (connectedParts.size != 2) {
                    null
                } else {
                    Gear(
                        row = r,
                        column = c,
                        parts = connectedParts.first() to connectedParts.last()
                    )
                }
            }
    }

    fun getPoint(row: Int, column: Int): String {
        return if (row < 0 || row >= grid.size) "."
        else if (column < 0 || column >= grid[0].size) "."
        else grid[row][column]
    }
}

data class PartNumber(
    val row: Int,
    val column: IntRange,
    val value: Int
) {
    val boundingbox by lazy { Boundingbox(
        topLeft = row - 1 to column.first - 1,
        bottomRight = row + 1 to column.last + 1
    ) }
}

data class Gear(
    val row: Int,
    val column: Int,
    val parts: Pair<PartNumber, PartNumber>
) {
    fun ratio(): Int {
        return parts.first.value * parts.second.value
    }
}

class Boundingbox(val topLeft: Pair<Int, Int>, val bottomRight: Pair<Int, Int>) {
    val top: Int = min(topLeft.first, bottomRight.first)
    val bottom: Int = max(topLeft.first, bottomRight.first)
    val left: Int = min(topLeft.second, bottomRight.second)
    val right: Int = max(topLeft.second, bottomRight.second)

    fun getBorder(): List<Pair<Int, Int>> = buildSet {
        (left..right).map {
            add(top to it)
            add(bottom to it)
        }
        (top..bottom).map {
            add(it to left)
            add(it to right)
        }
    }.toList()

    fun includes(row: Int, column: Int): Boolean {
        val withinRowRange = (top..bottom).contains(row)
        val withinColumnRange = (left..right).contains(column)

        return withinRowRange && withinColumnRange
    }
}
