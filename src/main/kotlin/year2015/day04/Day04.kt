package year2015.day04

import utils.*
import java.security.MessageDigest
import java.util.stream.IntStream

const val dir = "year2015/day04"
fun main() {
    verifySolution("abcdef", 609043, ::part1)
    verifySolution("pqrstuv", 1048970, ::part1)

    val input = "ckczppom"

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: String): Int {
    return IntStream
        .iterate(1) { it + 1 }
        .mapToObj { it to "$input$it" }
        .map { (v, i) -> v to md5(i) }
        .filter { (_, h) -> h.startsWith("00000") }
        .map { (v, _) -> v }
        .findFirst()
        .orElse(-1)
}

fun part2(input: String): Int {
    return IntStream
        .iterate(1) { it + 1 }
        .mapToObj { it to "$input$it" }
        .map { (v, i) -> v to md5(i) }
        .filter { (_, h) -> h.startsWith("000000") }
        .map { (v, _) -> v }
        .findFirst()
        .orElse(-1)
}
