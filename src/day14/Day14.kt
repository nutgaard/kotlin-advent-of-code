package day14

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "day14"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 136, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return parse(input).transpose('X')
        .map { applyGravity(it) }
        .sumOf { column ->
            column.mapIndexed { i, c ->
                if (c == 'O') column.size - i else 0
            }.sum()
        }

//    return -1
}

fun applyGravity(column: Array<Char>): Array<Char> {
    outer@while (true) {
        val firstAvailable = column.indexOf('.')
        if (firstAvailable == -1 || firstAvailable >= column.size - 1)break

        for (idx in (firstAvailable + 1)..<column.size) {
            val ch = column[idx]
            if (ch == 'O') {
                column[firstAvailable] = 'O'
                column[idx] = '.'
                break
            } else if (ch == '#') {
                column[firstAvailable] = 'X' // Fill space to avoid trying to fill this in later iterations
                break
            } else if (idx == column.size -1) {
                break@outer
            }
        }
    }

    return column
}

fun part2(input: List<String>): Int {
    val grid = parse(input)
    return -1
}

typealias Grid = Array<Array<Char>>
fun parse(input: List<String>): Grid {
    return input.map { it.toMutableList().toTypedArray() }.toTypedArray()
}



inline fun <reified T> Array<Array<T>>.transpose(defaultValue: T): Array<Array<T>> {
    val rows = this.size
    val columns = this.first().size
    val transposed = Array(columns) {
        Array(rows) {
            defaultValue
        }
    }

    for (i in 0..<rows) {
        for (j in 0..<columns) {
            transposed[j][i] = this[i][j]
        }
    }

    return transposed
}