package year2024.day07

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import java.util.concurrent.ConcurrentHashMap

const val dir = "year2024/day07"

fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 3749, ::part1)
    verifySolution(testInputPart2, 11387, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    val cases = input.map(::parse)
    val permutations = PermutationGenerator(listOf(Operator.PLUS, Operator.MULTIPLY))

    return cases
        .parallelStream()
        .filter { it.isSolveable(permutations) }
        .mapToLong { it.result }
        .sum()
}

fun part2(input: List<String>): Long {
    val cases = input.map(::parse)
    val permutations = PermutationGenerator(listOf(Operator.PLUS, Operator.MULTIPLY, Operator.CONCAT))
    for (i in 1..10){
        permutations.get(i)
    }

    return cases
        .parallelStream()
        .filter { it.isSolveable(permutations) }
//        .sumOf { it.result }
        .mapToLong { it.result }
        .sum()
}

enum class Operator(val fn: (a: Long, b: Long) -> Long)
{
    PLUS(Long::plus),
    MINUS(Long::minus),
    MULTIPLY(Long::times),
    DIVIDE(Long::div),
    CONCAT({ a, b -> "$a$b".toLong() }),
}
data class Day07TestCase(
    val result: Long,
    val numbers: List<Long>,
) {
    fun isSolveable(permutationGenerator: PermutationGenerator<Operator>): Boolean {
        val permutations = permutationGenerator.get(numbers.size - 1)
        return permutations.any { operators ->
            evaluate(operators.toTypedArray()) == result
        }
    }

    private fun evaluate(operators: Array<Operator>): Long {
        return numbers.reduceIndexed { index, acc, l ->
            operators[index - 1].fn.invoke(acc, l)
        }
    }
}

private fun parse(line: String): Day07TestCase {
    val (resultStr, numbersStr) = line.split(":").map(String::trim)
    return Day07TestCase(
        result = resultStr.toLong(),
        numbers = numbersStr.split(" ").map(String::toLong)
    )
}

class PermutationGenerator<T>(
    val values: List<T>,
) {
    private val lut: Map<Int, List<List<T>>> = ConcurrentHashMap()

    fun get(n: Int): List<List<T>> {
        return lut.getOrElse(n) {
            permutations(values, n)
        }
    }

    private fun permutations(values: List<T>, n: Int): List<List<T>> {
        if (n == 0) return listOf(emptyList())

        val smallerSelections = permutations(values, n - 1)
        return smallerSelections.flatMap { selection ->
            values.map { value ->
                selection + value
            }
        }
    }
}