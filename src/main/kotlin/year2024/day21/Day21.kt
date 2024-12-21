package year2024.day21

import utils.*

const val dir = "year2024/day21"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")

    verifySolution(testInputPart1, 126384, bind(2, ::part1))

    val input = readInput("$dir/Input")

    timed("part1") { part1(2, input).println() }
    timed("part2") { part1(25, input).println() }
}

data class Path(
    val from: Char,
    val to: Char,
    val path: String,
) {
    val directions = path
        .split("")
        .filter(String::isNotBlank)
        .map(Direction::fromDir)

    companion object {
        fun of(
            from: Char,
            to: Char,
            path: String,
        ) = Path(from, to, path)
    }
}

/**
 * +---+---+---+
 * | 7 | 8 | 9 |
 * +---+---+---+
 * | 4 | 5 | 6 |
 * +---+---+---+
 * | 1 | 2 | 3 |
 * +---+---+---+
 *     | 0 | A |
 *     +---+---+
 */
val numericalPadPaths = listOf(
    Path.of('9', '9', ""),
    Path.of('9', '8', "<"),
    Path.of('9', '7', "<<"),
    Path.of('9', '6', "v"),
    Path.of('9', '5', "<v"),
    Path.of('9', '4', "<<v"),
    Path.of('9', '3', "vv"),
    Path.of('9', '2', "<vv"),
    Path.of('9', '1', "<<vv"),
    Path.of('9', '0', "<vvv"),
    Path.of('9', 'A', "vvv"),
    Path.of('8', '9', ">"),
    Path.of('8', '8', ""),
    Path.of('8', '4', "<v"),
    Path.of('8', '7', "<"),
    Path.of('8', '6', "v>"),
    Path.of('8', '5', "v"),
    Path.of('8', '3', "vv>"),
    Path.of('8', '2', "vv"),
    Path.of('8', '1', "<vv"),
    Path.of('8', '0', "vvv"),
    Path.of('8', 'A', "vvv>"),
    Path.of('7', '9', ">>"),
    Path.of('7', '8', ">"),
    Path.of('7', '7', ""),
    Path.of('7', '6', "v>>"),
    Path.of('7', '5', "v>"),
    Path.of('7', '4', "v"),
    Path.of('7', '3', "vv>>"),
    Path.of('7', '2', "vv>"),
    Path.of('7', '1', "vv"),
    Path.of('7', '0', ">vvv"),
    Path.of('7', 'A', ">>vvv"),
    Path.of('6', '9', "^"),
    Path.of('6', '8', "<^"),
    Path.of('6', '7', "<<^"),
    Path.of('6', '6', ""),
    Path.of('6', '5', "<"),
    Path.of('6', '4', "<<"),
    Path.of('6', '3', "v"),
    Path.of('6', '2', "<v"),
    Path.of('6', '1', "<<v"),
    Path.of('6', '0', "<vv"),
    Path.of('6', 'A', "vv"),
    Path.of('5', '9', "^>"),
    Path.of('5', '8', "^"),
    Path.of('5', '7', "<^"),
    Path.of('5', '6', ">"),
    Path.of('5', '5', ""),
    Path.of('5', '4', "<"),
    Path.of('5', '3', "v>"),
    Path.of('5', '2', "v"),
    Path.of('5', '1', "<v"),
    Path.of('5', '0', "vv"),
    Path.of('5', 'A', "vv>"),
    Path.of('4', '9', "^>>"),
    Path.of('4', '8', "^>"),
    Path.of('4', '7', "^"),
    Path.of('4', '6', ">>"),
    Path.of('4', '5', ">"),
    Path.of('4', '4', ""),
    Path.of('4', '3', "v>>"),
    Path.of('4', '2', "v>"),
    Path.of('4', '1', "v"),
    Path.of('4', '0', ">vv"),
    Path.of('4', 'A', ">>vv"),
    Path.of('3', '9', "^^"),
    Path.of('3', '8', "<^^"),
    Path.of('3', '7', "<<^^"),
    Path.of('3', '6', "^"),
    Path.of('3', '5', "<^"),
    Path.of('3', '4', "<<^"),
    Path.of('3', '3', ""),
    Path.of('3', '2', "<"),
    Path.of('3', '1', "<<"),
    Path.of('3', '0', "<v"),
    Path.of('3', 'A', "v"),
    Path.of('2', '9', "^^>"),
    Path.of('2', '8', "^^"),
    Path.of('2', '7', "<^^"),
    Path.of('2', '6', "^>"),
    Path.of('2', '5', "^"),
    Path.of('2', '4', "<^"),
    Path.of('2', '3', ">"),
    Path.of('2', '2', ""),
    Path.of('2', '1', "<"),
    Path.of('2', '0', "v"),
    Path.of('2', 'A', "v>"),
    Path.of('1', '9', "^^>>"),
    Path.of('1', '8', "^^>"),
    Path.of('1', '7', "^^"),
    Path.of('1', '6', "^>>"),
    Path.of('1', '5', "^>"),
    Path.of('1', '4', "^"),
    Path.of('1', '3', ">>"),
    Path.of('1', '2', ">"),
    Path.of('1', '1', ""),
    Path.of('1', '0', ">v"),
    Path.of('1', 'A', ">>v"),
    Path.of('0', '9', "^^^>"),
    Path.of('0', '8', "^^^"),
    Path.of('0', '7', "^^^<"),
    Path.of('0', '6', "^^>"),
    Path.of('0', '5', "^^"),
    Path.of('0', '4', "^^<"),
    Path.of('0', '3', "^>"),
    Path.of('0', '2', "^"),
    Path.of('0', '1', "^<"),
    Path.of('0', '0', ""),
    Path.of('0', 'A', ">"),
    Path.of('A', '9', "^^^"),
    Path.of('A', '8', "<^^^"),
    Path.of('A', '7', "^^^<<"),
    Path.of('A', '6', "^^"),
    Path.of('A', '5', "<^^"),
    Path.of('A', '4', "^^<<"),
    Path.of('A', '3', "^"),
    Path.of('A', '2', "<^"),
    Path.of('A', '1', "^<<"),
    Path.of('A', '0', "<"),
    Path.of('A', 'A', ""),
)
/**
 *     +---+---+
 *     | ^ | A |
 * +---+---+---+
 * | < | v | > |
 * +---+---+---+
 */
val directionalPadPaths = listOf(
    Path.of('A', '^', "<"),
    Path.of('A', '<', "v<<"),
    Path.of('A', 'v', "<v"),
    Path.of('A', '>', "v"),
    Path.of('A', 'A', ""),
    Path.of('^', 'A', ">"),
    Path.of('^', '<', "v<"),
    Path.of('^', 'v', "v"),
    Path.of('^', '>', "v>"),
    Path.of('^', '^', ""),
    Path.of('v', 'A', "^>"),
    Path.of('v', '^', "^"),
    Path.of('v', '<', "<"),
    Path.of('v', '>', ">"),
    Path.of('v', 'v', ""),
    Path.of('>', 'A', "^"),
    Path.of('>', '^', "<^"),
    Path.of('>', '<', "<<"),
    Path.of('>', 'v', "<"),
    Path.of('>', '>', ""),
    Path.of('<', '>', ">>"),
    Path.of('<', 'A', ">>^"),
    Path.of('<', '^', ">^"),
    Path.of('<', 'v', ">"),
    Path.of('<', '<', ""),
)

fun part1(levels: Int, input: List<String>): Long {
    typeCharMemo.clear()
    val sequences = input.map { typesequence(it, levels) }
    val scores = input.map(::score)

    return sequences.zip(scores)
        .map { (seq, score) -> seq * score }
        .sum()
}

fun score(value: String): Int {
    return value.toCharArray().takeWhile { it.isDigit() }.joinToString("").toInt()
}

val typeCharMemo = mutableMapOf<Triple<Char, Char, Int>, Long>()
fun typeChar(current: Char, target: Char, idx: Int, maxDepth: Int, paths: List<List<Path>>): Long {
    val ctx = Triple(current, target, idx)
    return typeCharMemo.getOrPut(ctx) {
        val path = paths[idx].first {
            it.from == current && it.to == target
        }

        val sequence = "${path.path}A"
        if (idx == maxDepth) {
            sequence.length.toLong()
        } else {
            var length = 0L
            var c = 'A'
            for (t in sequence) {
                length += typeChar(c, t, idx + 1, maxDepth, paths)
                c = t
            }
            length
        }
    }
}

fun typesequence(value: String, levels: Int = 2): Long {
    val padPaths = buildList {
        add(numericalPadPaths)
        repeat(levels) {
            add(directionalPadPaths)
        }
    }

    var length = 0L
    var current = 'A'
    for (target in value) {
        length += typeChar(current, target, 0, levels, padPaths)
        current = target
    }

    return length
}
