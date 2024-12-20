package year2024.day17

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import java.util.*

const val dir = "year2024/day17"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, "4,6,3,5,6,3,5,2,1,0", ::part1)
    verifySolution(testInputPart2, 117440, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): String {
    val (computer, program) = parseInput(input)
    computer.asAssembly(program).println()

    computer.run(program)

    return computer.stdout.joinToString(",")
}

/**
 * Problem input - Pretty printed.
 * B = A % 8
 * B = B ^ 2
 * C = A >> B
 * B = B ^ 3
 * B = B ^ C
 * OUTPUT(B % 8)
 * A = A >> 3
 * JMP 0 // If A is not zero
 *
 * Pseudo-code:
 * do {
 *   B = mangle(A, B, C)
 *   println(B % 8)
 *   A /= 8
 * } while (A > 0)
 *
 * Hence each iteration divides A by 8, and prints a mangled B%8.
 * We can therefore find an A which would produce the last instruction/operand value,
 * multiply it by 8, and start the search for the second to last value from that point.
 *
 * This is done for each value in the program (reversed).
 *
 */
fun part2(input: List<String>): Long {
    val startingPoints = mutableListOf(0L)
    val (originalComputer, program) = parseInput(input)
    val expectedOut = program.toList().map { it.toLong() }
    var targetSize = 1

    while (targetSize < expectedOut.size) {
        val target = expectedOut.takeLast(targetSize)
        val startingPoint = startingPoints.last()
        val nextStart = searchSolution(
            computer = originalComputer.copy(regA = startingPoint),
            program = program,
            target = target
        )
        startingPoints.add(8 * nextStart)
        targetSize++
    }

    return searchSolution(
        computer = originalComputer.copy(regA = startingPoints.last()),
        program = program,
        target = expectedOut
    )
}

fun searchSolution(
    computer: Computer,
    program: Program,
    target: List<Long>,
): Long {
    return generateSequence(computer.regA) { it + 1}
        .first {
            val c = computer.copy(regA = it, stdout = LinkedList())
            c.run(program)
            c.stdout == target
        }
}

data class Computer(
    var regA: Long,
    var regB: Long,
    var regC: Long,
    var instructionPointer: Int = 0,
    var stdout: LinkedList<Long> = LinkedList()
) {
    fun run(program: Program) {
        while (instructionPointer < program.size - 1) {
            if (instructionPointer < 0) error("Instruction pointer out of bounds")

            val opcode = OpCode.from(program[instructionPointer])
            val operand = program[instructionPointer + 1]
            opcode.run(this, operand)
            instructionPointer += 2
        }
    }

    fun asAssembly(program: Program): String = buildString {
        for (i in program.indices step 2) {
            val opcode = OpCode.from(program[i])
            val operand = program[i + 1]

            appendLine(opcode.toString(operand))
        }
    }

    fun combo(value: Int): Long {
        if (value <= 3) return value.toLong()
        else if (value == 4) return regA
        else if (value == 5) return regB
        else if (value == 6) return regC
        error("Invalid operand $value")
    }
}

typealias Program = Array<Int>

enum class OpCode(
    val opcode: Int,
    val run: (computer: Computer, operand: Int) -> Unit,
    val toString: (operand: Int) -> String = { "" },
) {
    ADV(
        0,
        { computer, operand -> computer.regA = computer.regA shr computer.combo(operand).toInt() },
        { operand -> "A = A >> ${operandString(operand)}" }
    ),
    BXL(
        1,
        { computer, operand -> computer.regB = computer.regB xor operand.toLong() },
        { operand -> "B = B ^ $operand" }
    ),
    BST(
        2,
        { computer, operand -> computer.regB = computer.combo(operand) and 0b111 },
        { operand -> "B = ${operandString(operand)} % 8" }
    ),
    JNZ(
        3,
        { computer, operand ->
            if (computer.regA != 0L) {
                computer.instructionPointer = operand - 2
            }
        },
        { operand -> "JMP $operand // If A is zero" }
    ),
    BXC(
        4,
        { computer, operand -> computer.regB = computer.regB xor computer.regC },
        { operand -> "B = B ^ C" }
    ),
    OUT(
        5,
        { computer, operand -> computer.stdout += computer.combo(operand) and 0b111 },
        { operand -> "OUTPUT(${operandString(operand)} % 8)" }
    ),
    BDV(
        6,
        { computer, operand -> computer.regB = computer.regA shr computer.combo(operand).toInt() },
        { operand -> "B = A >> ${operandString(operand)}" }
    ),
    CDV(
        7,
        { computer, operand ->
            computer.regC = computer.regA shr computer.combo(operand).toInt()
        },
        { operand -> "C = A >> ${operandString(operand)}" }
    );

    companion object {
        private val LUT = entries.associateBy(OpCode::opcode)
        fun from(opcode: Int): OpCode = LUT.getOrElse(opcode) { error("Invalid opcode") }
        fun from(opcode: String): OpCode = from(opcode.toInt())
    }
}

fun parseInput(input: List<String>): Pair<Computer, Program> {
    val values = input
        .filter { it.isNotBlank() }
        .map { it.split(":").last().trim() }

    val (regA, regB, regC) = values.take(3).map(String::toLong)
    val computer = Computer(
        regA = regA,
        regB = regB,
        regC = regC,
    )
    val program = values.last().split(',').map(String::toInt).toTypedArray()

    return Pair(computer, program)
}

fun operandString(operand: Int): String =
    if (operand <= 3) operand.toString()
    else if (operand == 4) "A"
    else if (operand == 5) "B"
    else if (operand == 6) "C"
    else error("Invalid operand $operand")