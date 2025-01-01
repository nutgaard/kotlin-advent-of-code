package year2016.day10

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "year2016/day10"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    //verifySolution(testInputPart1, -1, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

sealed class GiveAway {}
data class Bot(
    val id: String,
    val chips: MutableList<Int> = mutableListOf(),
    var low: GiveAway? = null,
    var high: GiveAway? = null,
): GiveAway() {
    override fun toString(): String {
        return "Bot $id"
    }
}
data class Output(
    val id: String,
    val chips: MutableList<Int> = mutableListOf(),
) : GiveAway()

fun part1(input: List<String>): String {
    val outputs = Array(21) { Output("$it") }
    val bots = Array(210) { Bot("$it") }

    val highlowPattern = Regex("^bot (\\d+).+?(bot|output) (\\d+).+?(bot|output) (\\d+)\$")

    for (line in input) {
        if (line.startsWith("value ")) {
            val chip = line
                .removePrefix("value ")
                .takeWhile { it.isDigit() }
                .toInt()
            val bot = line
                .takeLastWhile { it.isDigit() }
                .toInt()

            bots[bot].chips.add(chip)
        } else if (line.startsWith("bot ")) {
            val match = requireNotNull(highlowPattern.find(line))

            val initialBot = requireNotNull(match.groups[1]).value.toInt()
            val lowOut = requireNotNull(match.groups[2]).value
            val lowId = requireNotNull(match.groups[3]).value.toInt()
            val highOut = requireNotNull(match.groups[4]).value
            val highId = requireNotNull(match.groups[5]).value.toInt()

            val bot = bots[initialBot]
            bot.low = if (lowOut == "bot") bots[lowId] else outputs[lowId]
            bot.high = if (highOut == "bot") bots[highId] else outputs[highId]
        } else {
            error("Invalid line: $line")
        }
    }

    do {
        val botsToProcess = bots.filter { it.chips.size == 2 }

        for (bot in botsToProcess) {
            val low = bot.chips.min()
            val high = bot.chips.max()

            if (low == 17 && high == 61) {
                println(bot.id)
            }

            val lowReceiver = bot.low ?: error("No low receiver for ${bot.id}")
            val highReceiver = bot.high ?: error("No high receiver for ${bot.id}")

            when (lowReceiver) {
                is Bot -> { lowReceiver.chips.add(low) }
                is Output -> { lowReceiver.chips.add(low) }
            }
            when (highReceiver) {
                is Bot -> { highReceiver.chips.add(high) }
                is Output -> { highReceiver.chips.add(high) }
            }
            bot.chips.clear()
        }
    } while (botsToProcess.size > 0)

    val oChipd = outputs.take(3)
        .map { it.chips.first() }
        .reduce { acc, i -> acc * i }
    println(oChipd)
    return ""
}

fun part2(input: List<String>): Int {
    return -1
}