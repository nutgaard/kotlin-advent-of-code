package year2015.day16

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2015/day16"
fun main() {
    //val testInputPart1 = readInput("${dir}/Part01_test")
    //val testInputPart2 = readInput("${dir}/Part02_test")

    //verifySolution(testInputPart1, -1, ::part1)
    //verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

val matchingAttributes = mutableMapOf(
    "children" to 3,
    "cats" to 7,
    "samoyeds" to 2,
    "pomeranians" to 3,
    "akitas" to 0,
    "vizslas" to 0,
    "goldfish" to 5,
    "trees" to 3,
    "cars" to 2,
    "perfumes" to 1,
)
fun part1(input: List<String>): Int {
    val sues = input.map(Sue::parse)
    val match = sues.first { sue ->
        val wasMatch = matchingAttributes.all { (attr, value) ->
           val sueAttr = sue.attributes[attr]
            sueAttr == null || sueAttr == value
        }
        wasMatch
    }
    return match.id
}

fun part2(input: List<String>): Int {
    val sues = input.map(Sue::parse)
    val match = sues.first { sue ->
        val wasMatch = matchingAttributes.all { (attr, value) ->
            val sueAttr = sue.attributes[attr]
            if (sueAttr == null) {
                true
            } else if (attr == "cats") {
                sueAttr > value
            } else if (attr == "trees") {
                sueAttr > value
            } else if (attr == "pomeranians") {
                sueAttr < value
            } else if (attr == "goldfish") {
                sueAttr < value
            } else {
                sueAttr == value
            }
        }
        wasMatch
    }
    return match.id
}

data class Sue(
    val id: Int,
    val attributes: Map<String, Int>,
) {
    companion object {
        fun parse(input: String): Sue {
            val attributes = mutableMapOf<String, Int>()
            val words = input.split(" ")
            val id = words[1].dropLast(1).toInt()
            for (idx in 2..<words.size step 2) {
                val term = words[idx].removeSuffix(":")
                val value = words[idx + 1].removeSuffix(",").toInt()
                attributes[term] = value
            }

            return Sue(id, attributes)
        }
    }
}