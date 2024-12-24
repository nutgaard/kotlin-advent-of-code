package year2024.day24

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

const val dir = "year2024/day24"
fun main() {
    val testInputPart1x1 = readInput("${dir}/Part01_01_test")
    val testInputPart1x2 = readInput("${dir}/Part01_02_test")

    verifySolution(testInputPart1x1, 4, ::part1)
    verifySolution(testInputPart1x2, 2024, ::part1)

    val input = readInput("$dir/Input")
    val inputOriginal = readInput("$dir/Input-original")

    timed("part1") { part1(input).println() }

    part2(inputOriginal)
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    val gatemap = parse(input)
    val numbers = readNumber(prefix = "z", gatemap)

    return numbers.toLong(2)
}

fun part2(input: List<String>): String {
    val gatemap = parse(input)
    val x = readNumber(prefix = "x", gatemap)
    val y = readNumber(prefix = "y", gatemap)

    val expectedValue = (x.toLong(2) + y.toLong(2)).toString(2)

    val str = buildString {
        appendLine("graph TD")
        appendLine("classDef error stroke:#f00")
        appendLine("classDef ok stroke:#0f0")

        getSequence(prefix = "x", gatemap)
            .zip(getNames(prefix = "x"))
            .forEach { (gate, name) ->
                appendLine("""    $name["`$name
        ${gate.read(gatemap)}`"]""")
            }
        getSequence(prefix = "y", gatemap)
            .zip(getNames(prefix = "y"))
            .forEach { (gate, name) ->
                appendLine("""    $name["`$name
        ${gate.read(gatemap)}`"]""")
            }

        getSequence(prefix = "z", gatemap)
            .zip(getNames(prefix = "z"))
            .forEach { (gate, name) ->
                val idx = name.removePrefix("z").toInt()
                val expectedBit = expectedValue[expectedValue.length - 1 - idx].toString()
                val actualBit = gate.read(gatemap).toString()
                val color = if (expectedBit != actualBit) "#f00" else "#0f0"
                appendLine("""    $name["`$name
        ${actualBit} (exp: $expectedBit)`"]""")
                appendLine("    style $name fill: $color")

                if (expectedBit != actualBit) {
                    println("mismatch at bit $name")
                }
            }

        appendLine()
        val operatorCounter = mutableMapOf<String, Int>(
            "AND" to 0,
            "OR" to 0,
            "XOR" to 0,
        )
        gatemap.forEach { output, gate ->
            if (gate is ByteGate.Operator) {
                val counter = operatorCounter[gate.operator] ?: 0
                operatorCounter[gate.operator] = counter + 1

                val operatorName = "${gate.operator}${counter}"
                appendLine("""    $operatorName["${gate.operator}"]""")
                appendLine("""    ${gate.inputA} --> $operatorName""")
                appendLine("""    ${gate.inputB} --> $operatorName""")
                appendLine("""    $operatorName --> $output""")
                appendLine()
            }
        }
    }

    /**
     * Error at
     * 06, 07, 08           z06 <-> vwr
     * 11, 12, 13           tqm <-> z11
     * 16, 17, 18 19        kfs <-> z16
     * 36, 37               hcm <-> gfv
     */

    return listOf(
        "z06", "vwr",
        "tqm", "z11",
        "kfs", "z16",
        "hcm", "gfv",
    ).sorted().joinToString(",")
}

private fun getNames(prefix: String): Sequence<String> {
    return generateSequence(0) { it + 1 }
        .map { it.toString().padStart(2, '0') }
        .map { "$prefix$it" }
}
private fun getSequence(prefix: String, gatemap: GateMap): Sequence<ByteGate> {
    return getNames(prefix)
        .map { gatemap[it] }
        .takeWhile { it != null }
        .filterNotNull()
}
private fun readNumber(prefix: String, gatemap: GateMap): String {
    val numbers = getSequence(prefix, gatemap)
        .map { it.read(gatemap) }
        .toList()
        .reversed()
        .joinToString("")

    return numbers
}

typealias GateMap = MutableMap<String, ByteGate>
sealed class ByteGate {
    abstract fun read(gateMap: GateMap): Byte

    class Value(val value: Byte) : ByteGate() {
        override fun read(gateMap: GateMap): Byte = value
    }
    class Operator(
        val inputA: String,
        val inputB: String,
        val operator: String
    ) : ByteGate() {
        override fun read(gateMap: GateMap): Byte {
            val a = gateMap[inputA]!!.read(gateMap)
            val b = gateMap[inputB]!!.read(gateMap)

            return when (operator) {
                "AND" -> a and b
                "OR" -> a or b
                "XOR" -> a xor b
                else -> error("Invalid operator: $operator")
            }
        }
    }
}

fun parse(input: List<String>): GateMap {
    val gateValues: GateMap = mutableMapOf()
    val initialValues = input.takeWhile { it.isNotBlank() }
    val gates = input.drop(initialValues.size + 1)

    initialValues.forEach {
        val (name, value) = it.split(": ")
        gateValues[name] = ByteGate.Value(value.toByte())
    }
    gates.forEach {
        val (inputA, operator, inputB, _, output) = it.split(" ")
        gateValues[output] = ByteGate.Operator(
            inputA = inputA,
            inputB = inputB,
            operator = operator
        )
    }

    return gateValues
}