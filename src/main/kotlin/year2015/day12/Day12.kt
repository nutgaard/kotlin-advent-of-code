package year2015.day12

import org.json.simple.JSONArray
import org.json.simple.JSONAware
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
const val dir = "year2015/day12"
fun main() {
    verifySolution("[1,2,3]", 6, ::part1)
    verifySolution("{\"a\":2,\"b\":4}", 6, ::part1)
    verifySolution("[1,{\"c\":\"red\",\"b\":2},3]", 6, ::part1)
    verifySolution("{\"d\":\"red\",\"e\":[1,2,3,4],\"f\":5}", 15, ::part1)
    verifySolution("[1,\"red\",5]", 6, ::part1)
    verifySolution("[[[3]]]", 3, ::part1)
    verifySolution("{\"a\":{\"b\":4},\"c\":-1}", 3, ::part1)
    verifySolution("{\"a\":[-1,1]}", 0, ::part1)
    verifySolution("[-1,{\"a\":1}]", 0, ::part1)
    verifySolution("[]", 0, ::part1)
    verifySolution("{}", 0, ::part1)

    verifySolution("[1,2,3]", 6, ::part2)
    verifySolution("{\"a\":2,\"b\":4}", 6, ::part2)
    verifySolution("[1,{\"c\":\"red\",\"b\":2},3]", 4, ::part2)
    verifySolution("{\"d\":\"red\",\"e\":[1,2,3,4],\"f\":5}", 0, ::part2)
    verifySolution("[1,\"red\",5]", 6, ::part2)
    verifySolution("[[[3]]]", 3, ::part2)
    verifySolution("{\"a\":{\"b\":4},\"c\":-1}", 3, ::part2)
    verifySolution("{\"a\":[-1,1]}", 0, ::part2)
    verifySolution("[-1,{\"a\":1}]", 0, ::part2)
    verifySolution("[]", 0, ::part2)
    verifySolution("{}", 0, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input.joinToString("")).println() }
    timed("part2") { part2(input.joinToString("")).println() }
}

fun part1(input: String): Long {
    return traverseJSON(JSONParser().parse(input))
}

fun part2(input: String): Long {
    return traverseJSON(JSONParser().parse(input), true)
}

fun traverseJSON(root: Any?, ignoreRed: Boolean = false): Long {
    val reducer = { acc: Long, child: Any? ->
        if (child is JSONAware) {
            acc + traverseJSON(child, ignoreRed)
        } else if (child is Number) {
            acc + child.toLong()
        } else {
            acc
        }
    }
    return when(root) {
        is JSONArray -> root.fold(0L, reducer)
        is JSONObject -> {
            if (ignoreRed) {
                val containsRed = root.values
                    .filterIsInstance<String>()
                    .any { it == "red" }
                if (containsRed) 0L
                else root.values.fold(0L, reducer)
            } else {
                root.values.fold(0L, reducer)
            }
        }
        else -> error("Unknown type: $root")
    }
}