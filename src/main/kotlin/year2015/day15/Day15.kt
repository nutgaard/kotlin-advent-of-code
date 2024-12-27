package year2015.day15

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import kotlin.math.max
import kotlin.math.min

const val dir = "year2015/day15"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 62842880, ::part1)
    verifySolution(testInputPart2, 57600000, ::part2)

    val input = readInput("$dir/Input")

    //timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    val ingredients = input.map(Ingredient::parse)

    val sequence: Sequence<Any> = generateSequence(0) { it + 1 }.takeWhile { it < 100 }
    val combinations = ingredients.drop(1)
        .fold(sequence) { acc, it ->
            acc.cartesianProduct(sequence)
        }

    val a = combinations.map { it.flatten<Int>() }
        .filter { it.sum() == 100 }
        .map { ingredients.zip(it) }
        .map { it to scoreRecipe(it) }
        .toList()

    return a.maxOf { it.second }
}

fun part2(input: List<String>): Long {
    val ingredients = input.map(Ingredient::parse)

    val sequence: Sequence<Any> = generateSequence(0) { it + 1 }.takeWhile { it < 100 }
    val combinations = ingredients.drop(1)
        .fold(sequence) { acc, it ->
            acc.cartesianProduct(sequence)
        }

    val a = combinations.map { it.flatten<Int>() }
        .filter { it.sum() == 100 }
        .map { ingredients.zip(it) }
        .filter { countCalories(it) == 500L }
        .map { it to scoreRecipe(it) }
        .toList()

    return a.maxOf { it.second }
}

typealias Recipe = List<Pair<Ingredient, Int>>
fun scoreRecipe(recipe: Recipe): Long {
    var capacity = 0L
    var durability = 0L
    var flavor = 0L
    var texture = 0L

    for ((ingredient, amount) in recipe) {
           capacity += amount * ingredient.capacity
           durability += amount * ingredient.durability
           flavor += amount * ingredient.flavor
           texture += amount * ingredient.texture
    }

    capacity = max(0, capacity)
    durability = max(0, durability)
    flavor = max(0, flavor)
    texture = max(0, texture)

    return capacity * durability * flavor * texture
}
fun countCalories(recipe: Recipe): Long {
    var calories = 0L

    for ((ingredient, amount) in recipe) {
        calories += amount * ingredient.calories
    }

    return calories
}

data class Ingredient(
    val name: String,
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int,
) {
    companion object {
        fun parse(input: String): Ingredient {
            val words = input.split(" ")
            return Ingredient(
                name = words[0].dropLast(1),
                capacity = words[2].dropLast(1).toInt(),
                durability = words[4].dropLast(1).toInt(),
                flavor = words[6].dropLast(1).toInt(),
                texture = words[8].dropLast(1).toInt(),
                calories = words[10].toInt(),
            )
        }
    }
}

data class Quad<S, T, U, V>(
    val first: S,
    val second: T,
    val third: U,
    val fourth: V,
)

fun <T, U> Sequence<T>.cartesianProduct(seq2: Sequence<U>): Sequence<Pair<T, U>> {
    return this.flatMap { first ->
        seq2.map { second -> Pair(first, second) }
    }
}

fun <T> Any.flatten(): List<T> {
    return when (this) {
        is Pair<*, *> -> this.toList().flatMap { it?.flatten() ?: emptyList() }
        else -> listOf(this as T)
    }
}