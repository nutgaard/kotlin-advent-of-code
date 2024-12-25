package year2015.day09

import utils.*

const val dir = "year2015/day09"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")

    verifySolution(testInputPart1, 605 to 982, ::part1)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    //timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Pair<Int, Int> {
    val adjacency = parse(input)
    val cities = adjacency.keys

    val allRoutes = permutations(cities.toList()).toList()
    val lengths = allRoutes
        .map { route ->
            val transfers = route.zipWithNext()
            val length = transfers.sumOf { (from, to) -> adjacency[from]!![to]!! }
            route to length
        }
        .toMap()

    return lengths.values.min() to lengths.values.max()
}

fun part2(input: List<String>): Int {
    return -1
}

data class Edge(val to: String, val length: Int)
fun parse(input: List<String>): MutableMap<String, MutableMap<String, Int>> {
    val adjacency = mutableMapOf<String, MutableMap<String, Int>>()

    for (line in input) {
        val (from, _, to, _, lengthStr) = line.split(" ")
        val length = lengthStr.toInt()

        adjacency.getOrPut(from, { mutableMapOf() }).put(to, length)
        adjacency.getOrPut(to, { mutableMapOf() }).put(from, length)
    }

    return adjacency
}