package day03

import utils.*

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
            val boundingbox = it.boundingbox.border()
            val hasAdjecentSymbol = boundingbox.any { (r, c) -> schematic.getPoint(r, c) != '.' }
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
    private val grid: Grid<Char> = rows.toCharGrid()

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
                gearRegex.findAll(row).map { index to it.range.first }
            }
            .mapNotNull { (r, c) ->
                val connectedParts = parts.filter { it.boundingbox.includes(Coordinate(r, c)) }
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

    fun getPoint(row: Long, column: Long): Char {
        return if (row < 0 || row >= grid.dimension.height) '.'
        else if (column < 0 || column >= grid.dimension.width) '.'
        else Coordinate(row, column).getValue(grid)
    }
}

data class PartNumber(
    val row: Int,
    val column: IntRange,
    val value: Int
) {
    val boundingbox by lazy {
        BoundingBox.fromCoordinates(
            topLeft = Coordinate(row - 1, column = column.first - 1),
            bottomRight = Coordinate(row + 1, column = column.last + 1),
        )
    }
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