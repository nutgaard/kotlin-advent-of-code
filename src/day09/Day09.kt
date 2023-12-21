package day09

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "day09"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 114, ::part1)
    verifySolution(testInputPart2, 2, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return input
        .map(String::intoIntList)
        .map(::extrapolate).sum()
}

fun part2(input: List<String>): Int {
    return input
        .map(String::intoIntList)
        .map(List<Int>::reversed)
        .map(::extrapolate)
        .sum()
}

fun extrapolate(numbers: List<Int>): Int {
    val data: MutableList<MutableList<Int>> = mutableListOf(numbers.toMutableList())
    var level = 0

    while (true) {
        val current = data[level]
        val next = current.zipWithNext { i, j -> j - i }.toMutableList()
        data.add(next)
        level++
        if (next.all { it == 0 }) break
    }

    data[level--].add(0)
    while (level >= 0) {
        val diff = data[level + 1].last()
        val last = data[level].last()
        data[level].add(last + diff)
        level--
    }

    return data[0].last()
}

fun String.intoIntList(): List<Int> = this
    .split(" ")
    .map(String::trim)
    .map(String::toInt)