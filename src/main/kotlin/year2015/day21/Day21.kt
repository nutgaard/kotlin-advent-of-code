package year2015.day21

import utils.*
import kotlin.math.max

const val dir = "year2015/day21"
fun main() {
    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    /**
     * Hit Points: 104
     * Damage: 8
     * Armor: 1
     */
    val boss = Player.Boss(104, 8, 1)

    val weaponChoices = weapons.asSequence()
    val armourChoices = listOf<Item?>(null) + armors
    val ringChoices = rings.map { listOf(it) } + permutations(rings, 2).toList()

    return weaponChoices.cartesianProduct(armourChoices.asSequence())
        .cartesianProduct(ringChoices.asSequence())
        .map { (first, rings) ->
            val (weapon, armor) = first
            Player.Me(
                hp = 100,
                weapon = weapon,
                armor = armor,
                rings = rings
            )
        }
        .filter { battle(it, boss).playerWins }
        .minBy { it.cost() }
        .cost()
}

fun part2(input: List<String>): Int {
    /**
     * Hit Points: 104
     * Damage: 8
     * Armor: 1
     */
    val boss = Player.Boss(104, 8, 1)

    val weaponChoices = weapons.asSequence()
    val armourChoices = listOf<Item?>(null) + armors
    val ringChoices = rings.map { listOf(it) } + permutations(rings, 2).toList()

    return weaponChoices.cartesianProduct(armourChoices.asSequence())
        .cartesianProduct(ringChoices.asSequence())
        .map { (first, rings) ->
            val (weapon, armor) = first
            Player.Me(
                hp = 100,
                weapon = weapon,
                armor = armor,
                rings = rings
            )
        }
        .filter { !battle(it, boss).playerWins }
        .maxBy { it.cost() }
        .cost()
}

data class Result(
    val playerHP: Int,
    val bossHP: Int,
    val playerWins: Boolean,
)

fun battle(player: Player, boss: Player): Result {
    var playersTurn = true
    var playerHP = player.hp()
    var bossHP = boss.hp()

    while (playerHP > 0 && bossHP > 0) {
        val damage = if (playersTurn) player.damage() else boss.damage()
        val armor = if (playersTurn) boss.armor() else player.armor()

        val effect = max(1, damage - armor)
        if (playersTurn) {
            bossHP -= effect
        } else {
            playerHP -= effect
        }
        playersTurn = !playersTurn
    }

    return Result(
        playerHP = playerHP,
        bossHP = bossHP,
        playerWins = bossHP <= 0
    )
}

abstract class Player {
    abstract fun hp(): Int
    abstract fun damage(): Int
    abstract fun armor(): Int

    data class Me(
        val hp: Int,
        val weapon: Item,
        val armor: Item?,
        val rings: List<Item>
    ) : Player() {
        override fun hp(): Int = hp
        override fun damage(): Int = weapon.damage + rings.sumOf { it.damage }
        override fun armor(): Int = (armor?.armor ?: 0) + rings.sumOf { it.armor }
        fun cost(): Int = weapon.cost + (armor?.cost ?: 0) + rings.sumOf { it.cost }
    }

    data class Boss(
        var hp: Int,
        var damage: Int,
        var armor: Int,
    ) : Player() {
        override fun hp(): Int = hp
        override fun damage(): Int = damage
        override fun armor(): Int = armor
    }
}

data class Item(
    val name: String,
    val cost: Int,
    val damage: Int,
    val armor: Int,
)

val weapons = listOf(
    Item("Dagger", 8, 4, 0),
    Item("Shortsword", 10, 5, 0),
    Item("Warhammer", 25, 6, 0),
    Item("Longsword", 40, 7, 0),
    Item("Greataxe", 74, 8, 0),
)

val armors = listOf(
    Item("Leather", 13, 0, 1),
    Item("Chainmail", 31, 0, 2),
    Item("Splintmail", 53, 0, 3),
    Item("Bandedmail", 75, 0, 4),
    Item("Platemail", 102, 0, 5),
)

val rings = listOf(
    Item("Damage +1", 25, 1, 0),
    Item("Damage +2", 50, 2, 0),
    Item("Damage +3", 100, 3, 0),
    Item("Defence +1", 20, 0, 1),
    Item("Defence +2", 40, 0, 2),
    Item("Defence +3", 80, 0, 3),
)