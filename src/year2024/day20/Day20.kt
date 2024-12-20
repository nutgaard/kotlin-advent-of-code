package year2024.day20

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import utils.*
import kotlin.math.abs

const val dir = "year2024/day20"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 8, bind(Config(12, 2, true, true), ::part1))
    verifySolution(testInputPart2, 285, bind(Config(50, 20, false, true), ::part1))

    val input = readInput("$dir/Input")

    timed("part1") { part1(Config(100, 2, true), input).println() }
    timed("part2") { part1(Config(100, 20, false), input).println() }
}

class Config(
    val cutoff: Int,
    val range: Int,
    val mustBeStraight: Boolean,
    val debug: Boolean = false
)

fun part1(config: Config, input: List<String>): Int {
    val grid = input.toCharGrid()
    val start = requireNotNull(grid.findCoordinate { it == 'S' })
    val end = requireNotNull(grid.findCoordinate { it == 'E' })

    val racingGrid = findPaths(grid, start)
    val racepath = requireNotNull(racingGrid.getValue(end))

    val costmap: ArrayGrid<Int?> = racingGrid.map { it?.size }
    racepath.forEachIndexed { index, coordinate -> costmap.setValue(coordinate, index) }

    val cheats = findCheats(costmap, racepath, config)

    if (config.debug) {
        val cheatBySaving = cheats.groupBy { it.savings }
        (50..76 step 2)
            .forEach {
                println("There are ${cheatBySaving[it]?.size ?: "N/A"} cheats that save $it picoseconds.")
            }
    }

    return cheats.count { it.savings >= config.cutoff }
}

fun part2(input: List<String>): Int {
    return -1
}


data class Cheat(
    val start: Coordinate,
    val end: Coordinate,
    val savings: Int,
)

fun findCheats(
    costGrid: ArrayGrid<Int?>,
    path: List<Coordinate>,
    config: Config
): Set<Cheat> {
    val rangeSeq = (-config.range..config.range)

    val cheats = mutableSetOf<Cheat>()
    for (coordinate in path) {

        for (rowOffset in rangeSeq ) {
            for (columnOffset in rangeSeq) {
                val moved = abs(rowOffset) + abs(columnOffset)
                if (moved > config.range) continue
                val expectedNewCost = costGrid.getValue(coordinate)!! + moved
                if (rowOffset == 0 && columnOffset == 0) continue
                if (config.mustBeStraight && (rowOffset != 0 && columnOffset != 0)) continue

                val nRow = coordinate.row + rowOffset
                if (nRow < 0 || nRow >= costGrid.dimension.height) continue
                val nColumn = coordinate.column + columnOffset
                if (nColumn < 0 || nColumn >= costGrid.dimension.width) continue

                val newCost = costGrid.getValue(nRow.toInt(), nColumn.toInt()) ?: continue
                val savings = newCost - expectedNewCost

                if (savings > 0) {
                    cheats.add(Cheat(
                        start = coordinate,
                        end =  Coordinate.of(
                            row = coordinate.row + rowOffset,
                            column = coordinate.column + columnOffset
                        ),
                        savings = savings
                    ))
                }

            }
        }
    }
    return cheats
}

fun findPaths(
    grid: ArrayGrid<Char>,
    start: Coordinate,
): ArrayGrid<PersistentList<Coordinate>?> {
    val costGrid: ArrayGrid<PersistentList<Coordinate>?> = grid.map { null }
    val stack = ConfigurableDeque.Stack<Coordinate>()

    costGrid.setValue(start, persistentListOf(start))
    stack.push(start)

    while (stack.isNotEmpty()) {
        val current = stack.pop()
        val currentPath = costGrid.getValue(current)!!
        val currentCost = currentPath.size

        for (direction in Direction.entries) {
            val newPosition = current.move(direction)
            if (newPosition notWithinBoundsOf grid) continue
            if (grid.getValue(newPosition) == '#') continue

            val previousPath = costGrid.getValue(newPosition)
            val previousCostValue = previousPath?.size ?: Int.MAX_VALUE

            if (currentCost + 1 < previousCostValue) {
                costGrid.setValue(
                    newPosition,
                    currentPath.add(newPosition)
                )
                stack.push(newPosition)
            }
        }
    }

    return costGrid
}