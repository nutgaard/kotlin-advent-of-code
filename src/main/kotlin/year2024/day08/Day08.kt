package year2024.day08

import utils.*

const val dir = "year2024/day08"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2x1 = readInput("${dir}/Part02_1_test")
    val testInputPart2x2 = readInput("${dir}/Part02_2_test")

    verifySolution(testInputPart1, 14, ::part1)
    verifySolution(testInputPart2x1, 9, ::part2)
    verifySolution(testInputPart2x2, 34, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val (grid, frequencies) = input.gridFrequencies()
    return frequencies.values
        .flatMap { it.createAntinodes(grid) }
        .toSet()
        .size
}

fun part2(input: List<String>): Int {
    val (grid, frequencies) = input.gridFrequencies()
    return frequencies.values
        .flatMap { it.createAntinodes(grid, 99) }
        .toSet()
        .size
}

typealias Frequencies = MutableMap<Char, MutableList<Coordinate>>

fun List<String>.gridFrequencies(): Pair<Grid<Char>, Frequencies> {
    val grid = this.toCharGrid()
    val frequencies: MutableMap<Char, MutableList<Coordinate>> = grid.reduce(mutableMapOf()){ acc, value, coordinate ->
        val list = acc.getOrElse(value) { mutableListOf() }
        list.add(coordinate)
        acc[value] = list
        acc
    }
    frequencies.remove('.')
    return Pair(grid, frequencies)
}

fun List<Coordinate>.createAntinodes(grid: Grid<*>, maxHarmonics: Int = 1): List<Coordinate> {
    val allowHarmonics = maxHarmonics > 1
    return this.pairs()
        .flatMap { (a, b) ->
            val antinodes = createAntinodesForCoordinatePair(grid, a, b, maxHarmonics)
            if (allowHarmonics) {
                antinodes.plus(this)
            } else {
                antinodes
            }
        }
}

fun createAntinodesForCoordinatePair(
    grid: Grid<*>,
    a: Coordinate,
    b: Coordinate,
    maxHarmonics: Int = 1
): List<Coordinate> {
    val distancePlus = Vector(
        x = a.column - b.column,
        y = a.row - b.row
    )
    val distanceMinus = distancePlus.negate()

    val plusSequence = generateSequence(a) {
        it.plus(distancePlus)
    }
        .withIndex()
        .drop(1)
        .take(maxHarmonics)
        .takeWhile { it.value withinBoundsOf grid }
        .map { it.value }

    val minusSequence = generateSequence(b) {
        it.plus(distanceMinus)
    }
        .withIndex()
        .drop(1)
        .take(maxHarmonics)
        .takeWhile { it.value withinBoundsOf grid }
        .map { it.value }

    return (plusSequence + minusSequence).toList()
}


fun <S> List<S>.pairs(): List<Pair<S, S>> = buildList {
    for (i in 0..<this@pairs.size - 1) {
        for (j in i + 1..<this@pairs.size) {
            this.add(this@pairs[i] to this@pairs[j])
        }
    }
}