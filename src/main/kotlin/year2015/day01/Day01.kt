package year2015.day01

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2015/day01"
fun main() {
    verifySolution("(())", 0, ::part1)
    verifySolution("()()", 0, ::part1)
    verifySolution("(((", 3, ::part1)
    verifySolution("(()(()(", 3, ::part1)
    verifySolution("())", -1, ::part1)
    verifySolution("))(", -1, ::part1)
    verifySolution(")))", -3, ::part1)
    verifySolution(")())())", -3, ::part1)

    verifySolution(")", 1, ::part2)
    verifySolution("()())", 5, ::part2)


    val input = readInput("$dir/Input").first()

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: String): Int {
    var floor = 0
    for (ch in input) {
        if (ch == '(') floor++
        else floor--
    }
    return floor
}

fun part2(input: String): Int {
    var floor = 0
    var idx = 1
    for (ch in input) {
        if (ch == '(') floor++
        else floor--

        if (floor < 0) return idx
        idx++
    }
    return -1
}