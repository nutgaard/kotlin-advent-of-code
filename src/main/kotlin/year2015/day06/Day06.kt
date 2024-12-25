package year2015.day06

import utils.*
import kotlin.math.max
import kotlin.math.min

const val dir = "year2015/day06"
fun main() {
    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val grid = ArrayGrid(
        Array(1000) {
            Array(1000) {
                0
            }
        }
    )
    val instructions = input.map(Instruction::parse)

    val opMap = mutableMapOf<Instruction.Operation, (Int) -> Int>(
        Instruction.Operation.TURN_ON to { 1 },
        Instruction.Operation.TURN_OFF to { 0 },
        Instruction.Operation.TOGGLE to { if (it == 1) 0 else 1 },
    )

    for (instruction in instructions) {
        val op = opMap[instruction.operation]!!
        instruction.domain.coordinates().forEach {
            grid.setValue(it, op(grid.getValue(it)))
        }
    }

    return grid.flattenToList().sum()
}

fun part2(input: List<String>): Int {
    val grid = ArrayGrid(
        Array(1000) {
            Array(1000) {
                0
            }
        }
    )
    val instructions = input.map(Instruction::parse)

    val opMap = mutableMapOf<Instruction.Operation, (Int) -> Int>(
        Instruction.Operation.TURN_ON to { it + 1 },
        Instruction.Operation.TURN_OFF to { max(0, it - 1) },
        Instruction.Operation.TOGGLE to { it + 2 },
    )

    for (instruction in instructions) {
        val op = opMap[instruction.operation]!!
        instruction.domain.coordinates().forEach {
            grid.setValue(it, op(grid.getValue(it)))
        }
    }

    return grid.flattenToList().sum()
}

data class Instruction(
    val operation: Operation,
    val domain: Domain,
) {
    enum class Operation {
        TURN_ON, TURN_OFF, TOGGLE;

        companion object {
            fun parse(line: String): Operation {
                return when {
                    line.startsWith("turn on") -> TURN_ON
                    line.startsWith("turn off") -> TURN_OFF
                    line.startsWith("toggle") -> TOGGLE
                    else -> error("Invalid operation: $line")
                }
            }
        }
    }
    data class Domain(
        val rows: IntRange,
        val columns: IntRange
    ) {
        fun coordinates(): Sequence<Coordinate> {
            return sequence {
                for (row in rows) {
                    for (column in columns) {
                        yield(Coordinate.of(row, column))
                    }
                }
            }
        }
    }


    companion object {
        fun parse(line: String): Instruction {
            val words = line.split(" ")
            val (xStart, yStart) = words[words.size - 3].split(",").map(String::toInt)
            val (xEnd, yEnd) = words[words.size - 1].split(",").map(String::toInt)

            return Instruction(
                operation = Operation.parse(line),
                domain = Domain(
                    rows = min(yStart, yEnd)..max(yStart, yEnd),
                    columns = min(xStart, xEnd)..max(xStart, xEnd),
                )
            )
        }
    }
}