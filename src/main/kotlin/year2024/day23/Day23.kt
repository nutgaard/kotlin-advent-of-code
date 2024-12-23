package year2024.day23

import utils.*
import java.util.LinkedList
import kotlin.math.floor
import kotlin.math.sqrt

const val dir = "year2024/day23"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 7, ::part1)
    verifySolution(testInputPart2, "co,de,ka,ta", ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val (lines, adjency) = parse(input)
    val cliques = mutableSetOf<List<String>>()

    for ((from, to) in lines) {
        val fromList = adjency[from]!!
        val toList = adjency[to]!!

        val common = fromList.intersect(toList)
        for (factor in common) {
            cliques.add(listOf(from, to, factor).sorted())
        }
    }

    val cheifCliques = cliques.filter { clique ->
        clique.find { it.startsWith("t") } != null
    }

    return cheifCliques.size
}

fun part2(input: List<String>): String {
    val (_, adjency) = parse(input)

    val result = mutableListOf<Set<String>>()
    bronKerbosch(
        r = mutableSetOf(),
        p = adjency.keys.toMutableSet(),
        x = mutableSetOf(),
        adjency,
        result
    )

    val largestClique = result.fold(emptySet<String>()) {acc, it ->
        if (acc.size < it.size) it else acc
    }
    return largestClique.sorted().joinToString(",")
}

private fun parse(input: List<String>): Pair<List<List<String>>, Map<String, MutableSet<String>>> {
    val lines = input.map { it.split("-") }
    val names = lines.flatten()
        .asSequence()
        .distinct()
        .toList()

    val adjency = names.associateWith { mutableSetOf<String>() }

    for ((from, to) in lines) {
        adjency[from]!!.add(to)
        adjency[to]!!.add(from)
    }

    return Pair(lines, adjency)
}

fun <T> bronKerbosch(
    r: Set<T>,
    p: MutableSet<T>,
    x: MutableSet<T>,
    adjencency: Map<T, Set<T>>,
    result: MutableList<Set<T>> = mutableListOf()
) {
    if (p.isEmpty() and x.isEmpty()) {
        result.add(r.toSet())
        return
    }

    val pivot = p.union(x).firstOrNull() ?: return
    val nonNeighbours = p - adjencency[pivot].orEmpty().toSet()

    for (v in nonNeighbours) {
        val neighbours = adjencency[v].orEmpty().toSet()

        bronKerbosch(
            r = (r + v),
            p = p.intersect(neighbours).toMutableSet(),
            x = x.intersect(neighbours).toMutableSet(),
            adjencency,
            result
        )
        p.remove(v)
        x.add(v)
    }
}