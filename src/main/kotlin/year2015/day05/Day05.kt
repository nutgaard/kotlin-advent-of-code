package year2015.day05

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2015/day05"
fun main() {
    verifySolution("ugknbfddgicrmopn", true, ::isNicePart1)
    verifySolution("aaa", true, ::isNicePart1)
    verifySolution("jchzalrnumimnmhp", false, ::isNicePart1)
    verifySolution("haegwjzuvuyypxyu", false, ::isNicePart1)
    verifySolution("dvszwmarrgswjxmb", false, ::isNicePart1)

    verifySolution("qjhvhtzxzqqjkmpb", true, ::isNicePart2)
    verifySolution("xxyxx", true, ::isNicePart2)
    verifySolution("uurcxstgmygtbstg", false, ::isNicePart2)
    verifySolution("ieodomkazucvgmuy", false, ::isNicePart2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input
        .filter(::isNicePart1)
        .count()
}

fun part2(input: List<String>): Int {
    return input
        .filter(::isNicePart2)
        .count()
}

fun isNicePart1(value: String): Boolean {
    return when {
        value.contains("ab") -> false
        value.contains("cd") -> false
        value.contains("pq") -> false
        value.contains("xy") -> false
        !hasDoubleChar(value) -> false
        vowelCount(value) < 3 -> false
        else -> true
    }
}

val repeatingCharacter = Regex("(.).\\1")
val repeatingPair = Regex("(..).*\\1")
fun isNicePart2(value: String): Boolean {
    return repeatingPair.find(value) != null &&
            repeatingCharacter.find(value) != null
}

fun hasDoubleChar(value: String): Boolean {
    for (idx in 1..< value.length) {
        if (value[idx - 1] == value[idx]) return true
    }

    return false
}
fun vowelCount(value: String): Int {
    var c = 0
    for (ch in value) {
        if (ch == 'a' || ch == 'e'  || ch == 'i'  || ch == 'o'  || ch == 'u') {
            c++
        }
    }
    return c
}