package year2024.day04

import utils.*

const val dir = "year2024/day04"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 18, ::part1)
    verifySolution(testInputPart2, 9, ::part2)

    val input = readInput("$dir/Input")

    // 2297
    // 1745
    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}


fun part1(input: List<String>): Int {
    val separator = "O"
    val lineLength = input[0].length
    val dotLine = ".".repeat(lineLength)
    val regexs = listOf(
        // Horizontal
        Regex("(X)(M)(A)(S)"),
        Regex("(S)(A)(M)(X)"),
        // Vertical
        Regex("(X)${dotLine}(M)${dotLine}(A)${dotLine}(S)"),
        Regex("(S)${dotLine}(A)${dotLine}(M)${dotLine}(X)"),

        // Down-right
        Regex("(X)${".".repeat(lineLength + 1)}(M)${".".repeat(lineLength + 1)}(A)${".".repeat(lineLength + 1)}(S)"),
        Regex("(S)${".".repeat(lineLength + 1)}(A)${".".repeat(lineLength + 1)}(M)${".".repeat(lineLength + 1)}(X)"),

        // Down-left
        Regex("(X)${".".repeat(lineLength - 1)}(M)${".".repeat(lineLength - 1)}(A)${".".repeat(lineLength - 1)}(S)"),
        Regex("(S)${".".repeat(lineLength - 1)}(A)${".".repeat(lineLength - 1)}(M)${".".repeat(lineLength - 1)}(X)"),
    )

    val content = input.joinToString(separator)

    val matches = regexs
        .flatMap { it.findAllWithOverlap(content) }
        .toList()

    //debug(input, matches)

    return matches.size
}

fun part2(input: List<String>): Int {
    val separator = "O"
    val lineLength = input[0].length
    val regexs = listOf(
        // M M
        //  A
        // S S
        Regex("(M).(M)${".".repeat(lineLength - 1)}(A)${".".repeat(lineLength - 1)}(S).(S)"),

        // S M
        //  A
        // S M
        Regex("(S).(M)${".".repeat(lineLength - 1)}(A)${".".repeat(lineLength - 1)}(S).(M)"),

        // S S
        //  A
        // M M
        Regex("(S).(S)${".".repeat(lineLength - 1)}(A)${".".repeat(lineLength - 1)}(M).(M)"),

        // M S
        //  A
        // M S
        Regex("(M).(S)${".".repeat(lineLength - 1)}(A)${".".repeat(lineLength - 1)}(M).(S)"),
    )

    val content = input.joinToString(separator)

    val matches = regexs
        .flatMap { it.findAllWithOverlap(content) }
        .toList()

    //debug(input, matches)

    return matches.size
}

fun debug(input: List<String>, matches: List<MatchResult>) {
    val lineLength = input[0].length
    val grid = input.toCharGrid().map { "." }

    for (match in matches) {
        for (group in match.groups.filterNotNull()) {
            if (group.value.length > 1) continue
            val value = group.value
            val index = group.range.first

            val lineShiftAdjustment = index / (lineLength + 1)
            val coordinate = Coordinate.fromArrayIndex(index - lineShiftAdjustment, lineLength)
            grid.setValue(coordinate, value)
        }
    }

    grid.println()
}

fun Regex.findAllWithOverlap(
    input: CharSequence,
    startIndex: Int = 0
): Sequence<MatchResult> {
    if (startIndex < 0 || startIndex > input.length) {
        throw IndexOutOfBoundsException("Start index out of bounds: $startIndex, input length: ${input.length}")
    }

    return generateSequence(
        seedFunction = { find(input, startIndex) },
        nextFunction = { find(input, it.range.first + 1) }
    )
}