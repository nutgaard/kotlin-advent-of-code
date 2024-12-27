package year2015.day13

import utils.*

const val dir = "year2015/day13"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 330, ::part1)
    //verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val happiness = parse(input)

    return permutations(happiness.keys.toList())
        .map { scoreArrangement(it, happiness) }
        .max()
}

fun part2(input: List<String>): Int {
    val happiness = parse(input)
    happiness["me"] = mutableMapOf()
    happiness.keys.forEach {
        if (it != "me") {
            happiness["me"]?.put(it, 0)
            happiness[it]?.put("me", 0)
        }
    }

    return permutations(happiness.keys.toList())
        .map { scoreArrangement(it, happiness) }
        .max()
}

fun scoreArrangement(arrangement: List<String>, happiness: MutableMap<String, MutableMap<String, Int>>): Int {
    var score = 0
    for (idx in arrangement.indices) {
        val current = arrangement[idx]
        val left = arrangement[arrangement.cyclicIdx(idx - 1)]
        val right = arrangement[arrangement.cyclicIdx(idx + 1)]

        score += happiness[current]?.get(left) ?: 0
        score += happiness[current]?.get(right) ?: 0
    }
    return score
}

fun <T> List<T>.cyclicIdx(idx: Int): Int {
    if (idx < 0) return idx + this.size
    return idx % this.size
}

fun parse(input: List<String>): MutableMap<String, MutableMap<String, Int>> {
    val happiness = mutableMapOf<String, MutableMap<String, Int>>()

    for (line in input) {
        val words = line.split(" ")
        val nameA = words.first()
        val nameB = words.last().dropLast(1)
        val sign = if (words[2] == "gain") 1 else -1
        val amount = words[3].toInt()

        happiness.getOrPut(nameA) { mutableMapOf() }[nameB] = sign * amount
    }

    return happiness
}
