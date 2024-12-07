package year2023.day12

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "day12"
fun main() {
    val testInputPart0 = readInput("${dir}/Part00_test")
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    testInputPart0.forEach {
        require(ConditionRecord.parse(it).isValid(0))
    }



    val debug = ConditionRecord.parse(".??..??...?##. 1,1,3").findValidFillers();

    require(ConditionRecord.parse("???.### 1,1,3").isValid(5) == true)
    require(ConditionRecord.parse("???.### 1,1,3").isValid(2) == false)
    require(ConditionRecord.parse(".??..??...?##. 1,1,3").findValidFillers().size == 1)

    /**
     * ?#?.?#?#????????#?. 2,4,4,3
     * ##?.####???????#?. 2,4,4,3
     *
     * ##?.?####???????#?. 2,4,4,3
     *
     * ?##.?#?#????????#?. 2,4,4,3
     */


    verifySolution(testInputPart1, 21, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")
    val validInTest = input.map { ConditionRecord.parse(it).findValidFillers() }

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input
        .sumOf { ConditionRecord.parse(it).findValidFillers().size }
}

fun part2(input: List<String>): Int {
    return -1
}

class ConditionRecord(val conditions: String, val errorcodes: List<Int>) {
    private val numberOfUnknowns: Int by lazy { conditions.count { it == '?' } }

    companion object {
        fun parse(str: String): ConditionRecord {
            val (conditions, errorcodeStr) = str.split(" ")
            val errorcodes = errorcodeStr.split(",").map(String::toInt)
            return ConditionRecord(conditions, errorcodes)
        }
    }

    fun isValid(filler: Int): Boolean {
        val groups = mutableListOf<Int>()
        var shiftedFiller = filler
        var groupStart = -1
        for (idx in conditions.indices) {
            val ch = conditions[idx]
            val fch =
                if (ch == '?') {
                    val o = if (shiftedFiller and 0b1 == 1) '#' else '.'
                    shiftedFiller = shiftedFiller shr 1
                    o
                } else ch
            if (fch == '.') {
                if (groupStart >= 0) {
                    groups.add(idx - groupStart)
                    groupStart = -1
                }
            } else { // ch == "#"
                if (groupStart == -1) {
                    groupStart = idx
                }
            }
        }
        if (groupStart >= 0) {
            groups.add(conditions.length - groupStart)
        }

        return groups.size == errorcodes.size && groups.zip(errorcodes).all { (a, b) -> a == b }
    }

    fun findValidFillers(): List<Int> {
        val max = 2 shl (numberOfUnknowns - 1)
        return (0..<max).filter(::isValid)
    }
}