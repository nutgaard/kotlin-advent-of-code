package year2016.day02

import utils.*

const val dir = "year2016/day02"

val keypad = ArrayGrid(
    arrayOf(
        arrayOf('1', '2', '3'),
        arrayOf('4', '5', '6'),
        arrayOf('7', '8', '9'),
    )
)
val advancedKeypad = ArrayGrid(
    arrayOf(
        arrayOf(' ', ' ', '1', ' ', ' '),
        arrayOf(' ', '2', '3', '4', ' '),
        arrayOf('5', '6', '7', '8', '9'),
        arrayOf(' ', 'A', 'B', 'C', ' '),
        arrayOf(' ', ' ', 'D', ' ', ' '),
    )
)

fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")

    verifySolution(testInputPart1, "1985", bind(keypad, ::part1))
    verifySolution(testInputPart1, "5DB3", bind(advancedKeypad, ::part1))

    val input = readInput("$dir/Input")

    timed("part1") { part1(keypad, input).println() }
    timed("part2") { part1(advancedKeypad, input).println() }
}

fun part1(keypad: ArrayGrid<Char>, input: List<String>): String {
    var position = requireNotNull(keypad.findCoordinate { it == '5' })
    return buildString {
        for (line in input) {
            for (ch in line.toCharArray()) {
                val direction = Direction.fromDir(ch)
                val newPosition = position.move(direction)

                val validPosition = newPosition withinBoundsOf keypad &&
                        keypad.getValue(newPosition) != ' '
                if (validPosition) {
                    position = newPosition
                }
            }
            append(keypad.getValue(position))
        }
    }
}

fun part2(input: List<String>): Int {
    return -1
}