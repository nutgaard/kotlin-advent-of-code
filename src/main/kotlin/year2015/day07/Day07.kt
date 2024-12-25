package year2015.day07

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2015/day07"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    //verifySolution(testInputPart1, -1, ::part1)
    //verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val circuit = parse(input)

    return circuit.get("a")?.read(circuit)?.toInt() ?: error("Invalid")
}

fun part2(input: List<String>): Int {
    val circuit = parse(input)
    circuit["b"] = CircuitComponent.Operand.Value(956.toUShort())

    return circuit.get("a")?.read(circuit)?.toInt() ?: error("Invalid")
}

typealias GateMap = MutableMap<String, CircuitComponent>
sealed class CircuitComponent {
    abstract fun read(gateMap: GateMap): UShort

    val memoized = mutableMapOf<String, UShort>()

    sealed class Operand : CircuitComponent() {
        data class Value(val value: UShort) : Operand()
        data class Reference(val value: String) : Operand()

        override fun read(gateMap: GateMap): UShort {
            return when (this) {
                is Value -> this.value
                is Reference -> {
                    if (memoized[this.value] != null) {
                        memoized[this.value]!!
                    } else {
                        val value: UShort = gateMap[this.value]!!.read(gateMap)
                        memoized[this.value] = value
                        value
                    }
                }
            }
        }

        companion object {
            fun of(value: String): Operand {
                return runCatching { value.toUShort() }
                    .map { Value(it) }
                    .getOrDefault(Reference(value))
            }
        }
    }

    data class UnaryGate(
        val inputA: Operand,
        val operator: String,
    ): CircuitComponent() {
        override fun read(gateMap: GateMap): UShort {
            val current = inputA.read(gateMap)
            return when (operator) {
                "NOT" -> current.inv()
                else -> error("Invalid operator: $operator")
            }
        }
    }

    class BinaryGate(
        val inputA: Operand,
        val inputB: Operand,
        val operator: String
    ) : CircuitComponent() {
        override fun read(gateMap: GateMap): UShort {
            val a = inputA.read(gateMap)
            val b = inputB.read(gateMap)

            return when (operator) {
                "AND" -> a and b
                "OR" -> a or b
                "XOR" -> a xor b
                "LSHIFT" -> if (inputB !is Operand.Value) error("Invalid leftshift: $inputB") else (a.toInt() shl b.toInt()).toUShort()
                "RSHIFT" -> if (inputB !is Operand.Value) error("Invalid rightshift: $inputB") else (a.toInt() shr b.toInt()).toUShort()
                else -> error("Invalid operator: $operator")
            }
        }
    }
}

fun parse(lines: List<String>): GateMap {
    val gateValues: GateMap = mutableMapOf()
    val wordgroups = lines
        .map { it.split(" ").toTypedArray() }
        .groupBy { it.size }

    wordgroups.getOrDefault(3, emptyList()).forEach {
        val (numStr, _, ref) = it
        gateValues[ref] = CircuitComponent.Operand.of(numStr)
    }

    wordgroups.getOrDefault(4, emptyList()).forEach {
        val (operator, inputA, _, out) = it
        gateValues[out] = CircuitComponent.UnaryGate(
            inputA = CircuitComponent.Operand.of(inputA),
            operator = operator
        )
    }

    wordgroups.getOrDefault(5, emptyList()).forEach {
        val (inputA, operator, inputB, _, out) = it
        gateValues[out] = CircuitComponent.BinaryGate(
            inputA = CircuitComponent.Operand.of(inputA),
            inputB = CircuitComponent.Operand.of(inputB),
            operator = operator
        )
    }

    return gateValues
}