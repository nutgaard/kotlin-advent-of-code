package year2024.day05

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2024/day05"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 143, ::part1)
    verifySolution(testInputPart2, 123, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

data class PageOrderingRule(
    val first: Int,
    val second: Int
)

data class PageOrderingValidation(
    val ordering: PageOrdering,
    val rules: List<PageOrderingRule>,
    val invalidRules: List<PageOrderingRule>
) {
    fun isValid(): Boolean = invalidRules.isEmpty()
    fun isInvalid(): Boolean = invalidRules.isNotEmpty()

    fun fixup(recursion: Int = 0): PageOrdering {
        if (this.isValid()) return ordering
        val firstInvalidRule = invalidRules.first()
        val fixed = swap(ordering, firstInvalidRule)
        val revalidated = fixed.validate(rules)
        return revalidated.fixup(recursion + 1)
    }

    private fun swap(ordering: PageOrdering, rule: PageOrderingRule): PageOrdering {
        val firstIdx = ordering.pages.indexOf(rule.first)
        val secondIdx = ordering.pages.indexOf(rule.second)

        val firstValue = ordering.pages[firstIdx]
        val secondValue = ordering.pages[secondIdx]

        val copy = ordering.pages.toMutableList()
        copy[firstIdx] = secondValue
        copy[secondIdx] = firstValue

        return PageOrdering(copy)
    }
}

data class PageOrdering(
    val pages: List<Int>
) {
    fun getMiddleValue() = this.pages.get(this.pages.size / 2)

    fun validate(rules: List<PageOrderingRule>): PageOrderingValidation {
        return PageOrderingValidation(
            ordering = this,
            rules = rules,
            invalidRules = rules.filter { rule -> !this.follows(rule) }
        )
    }

    private fun follows(rule: PageOrderingRule): Boolean {
        val firstIdx = pages.indexOf(rule.first)
        val secondIdx = pages.indexOf(rule.second)

        if (firstIdx == -1 || secondIdx == -1) return true
        return firstIdx < secondIdx
    }
}
private fun List<String>.parse(): Pair<List<PageOrderingRule>, List<PageOrdering>> {
    val rules = this
        .takeWhile { it.contains("|") }
        .map {
            val (first, second) = it.split("|").map(String::toInt)
            PageOrderingRule(first, second)
        }

    val pageOrderings = this
        .dropWhile { it.isEmpty() || it.contains("|") }
        .map {
            PageOrdering(
                it.split(",").map(String::toInt)
            )
        }

    return Pair(rules, pageOrderings)
}

fun part1(input: List<String>): Int {
    val (rules, pageOrderings) = input.parse()
    return pageOrderings
        .map { it.validate(rules) }
        .filter(PageOrderingValidation::isValid)
        .sumOf { it.ordering.getMiddleValue() }
}

fun part2(input: List<String>): Int {
    val (rules, pageOrderings) = input.parse()
    return pageOrderings
        .map { it.validate(rules) }
        .filter(PageOrderingValidation::isInvalid)
        .map(PageOrderingValidation::fixup)
        .sumOf { it.getMiddleValue() }
}