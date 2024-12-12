package year2023.day11

import utils.*

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
        .sumOf { (first, second) -> first.gridStepsBetween(second) }
}

class GalaxyMap(
    val unscaledGalaxyMap: ArrayGrid<Char>,
    val galaxies: List<Coordinate>,
) {
    fun applyExpansionRate(rate: Long): GalaxyMap {
        if (rate == 1L) return this

        val expandingRows = unscaledGalaxyMap.rowIndices
            .map { row ->
                val isEmpty = unscaledGalaxyMap.grid[row].all { it == '.' }
                if (isEmpty) 1 else 0
            }
            .runningReduce(Int::plus)

        val expandingColumns = unscaledGalaxyMap.columnIndices
            .map { column ->
                val isEmpty = unscaledGalaxyMap.rowIndices.all { row -> unscaledGalaxyMap.grid[row][column] == '.' }
                if (isEmpty) 1 else 0
            }
            .runningReduce(Int::plus)

        val newGalaxies = galaxies
            .map { (row, column) ->
                val newRow = row + expandingRows[row.toInt()] * rate - expandingRows[row.toInt()]
                val newColumn = column + expandingColumns[column.toInt()] * rate - expandingColumns[column.toInt()]
                Coordinate.of(newRow, newColumn)
            }
        return GalaxyMap(unscaledGalaxyMap, newGalaxies)
    }

    companion object {
        fun parse(input: List<String>): GalaxyMap {
            val galaxyMap = input.toCharGrid()
            val galaxies = galaxyMap
                .mapIndexed { row, column, value ->
                    Triple(row, column, value)
                }
                .flattenToList()
                .filter { (_, _, c) -> c == '#' }
                .map { (row, column, _) -> Coordinate.of(row.toLong(), column.toLong()) }

            return GalaxyMap(galaxyMap, galaxies)
        }
    }
}

fun <S> List<S>.pairs(): List<Pair<S, S>> = buildList {
    for (i in 0..<this@pairs.size) {
        for (j in i..<this@pairs.size) {
            this.add(this@pairs[i] to this@pairs[j])
        }
    }
}