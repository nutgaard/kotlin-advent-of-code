package day13

import println
import readInput
import timed
import verifySolution
import java.util.*

const val dir = "day13"

fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")
    val input = readInput("$dir/Input")

    verifySolution(testInputPart1, 405, ::part1)
    verifySolution(testInputPart2, 400, ::part2)

    verifySolution(input, 32035, ::part1)


    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return parseInput(input)
        .map { it.findReflection(errorTolerance = 0) }
        .sumOf { (vertical, horizontal) -> 100 * (horizontal ?: 0) + (vertical ?: 0) }
}

fun part2(input: List<String>): Int {
    return parseInput(input)
        .map { it.findReflection(errorTolerance = 1) }
        .sumOf { (vertical, horizontal) -> 100 * (horizontal ?: 0) + (vertical ?: 0) }
}

fun parseInput(input: List<String>): List<BitGrid> {
    val gridAggregate = mutableListOf<String>()
    return buildList {
        for (line in input) {
            if (line.isBlank()) {
                add(BitGrid.parse(gridAggregate))
                gridAggregate.clear()
            } else {
                gridAggregate.add(line)
            }
        }
        add(BitGrid.parse(gridAggregate))
    }
}

class BitGrid(val grid: Array<SizeAwareBitSet>) {
    companion object {
        fun parse(input: List<String>, trueValue: Char = '#'): BitGrid {
            val grid = input
                .map { line ->
                    val bitline = SizeAwareBitSet(line.length)
                    line.mapIndexed { index, ch -> bitline.set(index, ch == trueValue) }
                    bitline
                }
            return BitGrid(grid.toTypedArray())
        }
    }

    override fun toString(): String = buildString {
        for (row in grid) {
            val cardinality = row.nbits
            for (column in 0..<cardinality) {
                append(if (row.get(column)) "1" else "0")
            }
            appendLine()
        }
    }

    fun transpose(): BitGrid {
        val rows = grid.size
        val columns = grid[0].nbits
        val transposed = Array(columns) {
            SizeAwareBitSet(rows)
        }

        for (i in 0..<rows) {
            for (j in 0..<columns) {
                transposed[j].set(i, grid[i].get(j))
            }
        }

        return BitGrid(transposed)
    }
}

fun BitGrid.findReflection(errorTolerance: Int): Pair<Int?, Int?> {
    val horizontal = this.findReflectionRow(errorTolerance)
    if (horizontal != null) return null to horizontal + 1

    val vertical = this.transpose().findReflectionRow(errorTolerance)
    return vertical?.plus(1) to null
}

fun BitGrid.findReflectionRow(errorTolerance: Int): Int? {
    val grid = this.grid

    val candidates = sequence {
        for (i in 0..<grid.size - 1) {
            val current = grid[i]
            val next = grid[i + 1]
            val diff = current.clone().apply { xor(next) }
            if (diff.cardinality() <= errorTolerance) yield(i)
        }
    }

    return candidates.find { candidate ->
        var accumulatedError = 0
        var top = candidate
        var bottom = candidate + 1
        do {
            val topEl = grid[top]
            val bottomEl = grid[bottom]
            val diff = topEl.clone().apply { xor(bottomEl) }
            accumulatedError += diff.cardinality()
            if (accumulatedError > errorTolerance) return@find  false
            top--
            bottom++
        } while (top >= 0 && bottom < grid.size)

        accumulatedError == errorTolerance
    }
}

class SizeAwareBitSet(val nbits: Int) : BitSet(nbits) {
    override fun clone(): BitSet = super.clone() as BitSet
}