package day11

import GridCoordinate
import pairs
import println
import readInput
import timed
import verifySolution
import kotlin.math.abs

const val dir = "day11"
fun main() {
    val textGalaxyMap = GalaxyMap.parse(readInput("${dir}/Part01_test"))
    verifySolution(textGalaxyMap.applyExpansionRate(2), 374, ::solve)
    verifySolution(textGalaxyMap.applyExpansionRate(10), 1030, ::solve)
    verifySolution(textGalaxyMap.applyExpansionRate(100), 8410, ::solve)

    val galaxyMap = GalaxyMap.parse(readInput("$dir/Input"))
    timed("part1") { solve(galaxyMap.applyExpansionRate(2)).println() }
    timed("part2") { solve(galaxyMap.applyExpansionRate(1000000)).println() }
}

fun solve(galaxyMap: GalaxyMap): Long {
    return galaxyMap.galaxies
        .pairs()
        .filter { (first, second) -> first != second }
        .sumOf { (first, second) -> first.distanceTo(second) }
}

class GalaxyMap(
    val unscaledGalaxyMap: Array<Array<Char>>,
    val galaxies: List<GridCoordinate>,
) {
    fun applyExpansionRate(rate: Long): GalaxyMap {
        if (rate == 1L) return this

        val expandingRows = unscaledGalaxyMap.indices
            .map { row ->
                val isEmpty = unscaledGalaxyMap[row].all { it == '.' }
                if (isEmpty) 1 else 0
            }
            .runningReduce(Int::plus)

        val expandingColumns = unscaledGalaxyMap.first().indices
            .map { column ->
                val isEmpty = unscaledGalaxyMap.indices.all { row -> unscaledGalaxyMap[row][column] == '.' }
                if (isEmpty) 1 else 0
            }
            .runningReduce(Int::plus)

        val newGalaxies = galaxies
            .map { (row, column) ->
                val newRow = row + expandingRows[row.toInt()] * rate - expandingRows[row.toInt()]
                val newColumn = column + expandingColumns[column.toInt()] * rate - expandingColumns[column.toInt()]
                GridCoordinate(newRow, newColumn)
            }
        return GalaxyMap(unscaledGalaxyMap, newGalaxies)
    }

    companion object {
        fun parse(input: List<String>): GalaxyMap {
            val galaxyMap = input.map { it.toList().toTypedArray() }.toTypedArray()
            val galaxies = buildList {
                for (rowIdx in galaxyMap.indices) {
                    val row = galaxyMap[rowIdx]
                    for (columnIdx in row.indices) {
                        if (row[columnIdx] == '#') add(
                            GridCoordinate(
                                row = rowIdx.toLong(),
                                column = columnIdx.toLong()
                            )
                        )
                    }
                }
            }
            return GalaxyMap(galaxyMap, galaxies)
        }
    }
}

fun GridCoordinate.distanceTo(other: GridCoordinate): Long {
    return abs(this.row - other.row) + abs(this.column - other.column)
}