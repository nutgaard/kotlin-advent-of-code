package year2023.day07

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "day07"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 6440, ::part1)
    verifySolution(testInputPart2, 5905, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    return input
        .map(BidHand::parse)
        .sortedBy { it.score }
        .mapIndexed { index, hand ->
            (index + 1) * hand.bid
        }
        .sum()
}

fun part2(input: List<String>): Long {
    return input
        .map(BidHand::parse)
        .sortedBy { it.jokerScore }
        .mapIndexed { index, hand ->
            (index + 1) * hand.bid
        }
        .sum()
}

class BidHand(private val handStr: String, private val bidStr: String) {
    val bid by lazy { bidStr.toLong() }
    private val cards by lazy {
        handStr
            .split("")
            .filter { it.isNotBlank() }
    }
    val score by lazy { scoreHand(cards, cardScoring) }
    val jokerScore by lazy { scoreHand(cards, jokerCardScore, ::fixHandWithJokers) }

    companion object {
        private val cardScoring: Map<String, String> = "AKQJT98765432".reversed().rankMap()
        private val jokerCardScore: Map<String, String> = "AKQT98765432J".reversed().rankMap()

        fun parse(line: String): BidHand {
            val (handStr, bidStr) = line.split(" ").map(String::trim)
            return BidHand(handStr, bidStr)
        }

        fun fixHandWithJokers(cards: List<String>): List<String> {
            val cardInstances = cards
                .groupingBy { it }
                .eachCount()

            val jokerCount = cards.count { it == "J" }
            if (jokerCount == 5) return cards.replace("J", "A")


            val maxCardCount = cardInstances
                .filter { it.key != "J" }
                .maxOf { it.value }

            val maxCardValue = cardInstances
                .filter { it.key != "J" }
                .filter { it.value == maxCardCount }
                .maxBy { cardScoring[it.key] ?: "00" }
                .key

            return cards.replace("J", maxCardValue)
        }

        private enum class HandType(val score: Int) {
            FIVE_ALIKE(7),
            FOUR_ALIKE(6),
            FULL_HOUSE(5),
            THREE_ALIKE(4),
            TWO_PAIR(3),
            ONE_PAIR(2),
            HIGH_CARD(1),
        }

        private fun scoreHand(
            cards: List<String>,
            cardScoring: Map<String, String>,
            fixupFn: (List<String>) -> List<String> = { it },
        ): Long {
            val handType = findHandType(fixupFn(cards)).score.toString().padStart(2, '0')
            val cardValues: String = cards.joinToString("") { cardScoring[it] ?: "00" }

            return "$handType$cardValues".toLong()
        }

        private fun findHandType(cards: List<String>): HandType {
            val cardInstances = cards
                .groupingBy { it }
                .eachCount()

            return if (cardInstances.containsValue(5)) {
                HandType.FIVE_ALIKE
            } else if (cardInstances.containsValue(4)) {
                HandType.FOUR_ALIKE
            } else if (cardInstances.containsValue(3)) {
                if (cardInstances.containsValue(2)) {
                    HandType.FULL_HOUSE
                } else {
                    HandType.THREE_ALIKE
                }
            } else if (cardInstances.containsValue(2)) {
                val numberOfPairs = cardInstances.count { it.value == 2 }
                if (numberOfPairs == 2) {
                    HandType.TWO_PAIR
                } else {
                    HandType.ONE_PAIR
                }
            } else {
                HandType.HIGH_CARD
            }
        }
    }

}

private fun List<String>.replace(oldValue: String, newValue: String): List<String> {
    return this.map {
        if (it == oldValue) newValue
        else it
    }
}

private fun String.rankMap(): Map<String, String> {
    return this.split("")
        .filter { it.isNotBlank() }
        .mapIndexed { i, v -> v to (i + 2).toString().padStart(2, '0') }
        .toMap()
}