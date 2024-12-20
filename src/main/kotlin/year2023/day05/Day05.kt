package year2023.day05

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import java.util.*
import kotlin.math.min
import kotlin.streams.asStream

const val dir = "day05"
fun main() {
    val testInputPart1 = Almanac.parse(readInput("${dir}/Part01_test"))
    val testInputPart2 = Almanac.parse(readInput("${dir}/Part02_test"))

    verifySolution(testInputPart1, 35, ::part1)
    verifySolution(testInputPart2, 46, ::part2)

    val input = readInput("$dir/Input")
    val almanac = timed("almanac") { Almanac.parse(input) }

    timed("part1") { part1(almanac).println() }
    timed("part2") { part2(almanac).println() }
}

fun part1(almanac: Almanac): Long {
    val seeds = almanac.seeds
    println("[part1] Find minimum amount ${seeds.size}")
    return seeds
        .map { almanac.resolveUntilCategory(it, Category.Location) }
        .minOf { it.number }
}

fun part2(almanac: Almanac): Long {
    val seeds = almanac.seeds
        .chunked(2)
        .asSequence()
        .flatMap { (first, second) -> (first.number..<first.number + second.number) }

    println("[part1] Find minimum amount ${seeds.count()}")

    return seeds
        .asStream()
        .parallel()
        .map { EntityNumber(Category.Seed, it) }
        .map { almanac.resolveUntilCategory(it, Category.Location).number }
        .reduce(10000000000000L, ::min)
}

data class Almanac(
    val seeds: List<EntityNumber>,
    val categoryMaps: Map<Category, CategoryMap<Category>>
) {
    companion object {
        fun parse(lines: List<String>): Almanac {
            val seeds = mutableListOf<EntityNumber>()
            val categoryMaps = mutableMapOf<Category, CategoryMap<Category>>()

            var fromCategory: Category? = null
            var toCategory: Category? = null
            var ranges = mutableListOf<RangeMap>()

            for (line in lines) {
                if (line.startsWith("seeds:")) {
                    seeds += line
                        .removePrefix("seeds:")
                        .trim()
                        .split(" ")
                        .map(String::toLong)
                        .map { EntityNumber(Category.Seed, it) }
                } else if (line.endsWith("map:")) {
                    val (from, to) = line
                        .removeSuffix("map:")
                        .trim()
                        .split("-to-")
                        .map(Category::valueOfCaseInSensitive)
                    fromCategory = from
                    toCategory = to
                } else if (line.isBlank()) {
                    if (fromCategory != null && toCategory != null) {
                        categoryMaps[fromCategory] = CategoryMap(
                            to = toCategory,
                            ranges = ranges
                        )
                    }
                    fromCategory = null
                    toCategory = null
                    ranges = mutableListOf()
                } else {
                    val (a, b, c) = line.split(" ").map(String::toLong)
                    ranges.add(RangeMap(a, b, c))
                }
            }
            if (fromCategory != null && toCategory != null) {
                categoryMaps[fromCategory] = CategoryMap(
                    to = toCategory,
                    ranges = ranges
                )
            }

            return Almanac(seeds, categoryMaps)
        }
    }

    fun resolveUntilCategory(entityNumber: EntityNumber, endCategory: Category): EntityNumber {
        var (category, number) = entityNumber
        while (category != endCategory) {
            val categoryMap = requireNotNull(categoryMaps[category]) {
                "Could not find category map for $category"
            }
            val next = categoryMap.resolveNext(number)
            category = next.category
            number = next.number
        }
        return EntityNumber(category, number)
    }
}

data class EntityNumber(val category: Category, val number: Long)

data class CategoryMap<To : Category>(
    val to: To,
    private val ranges: List<RangeMap>
) {
    fun resolveNext(num: Long): EntityNumber {
        val range = ranges.firstOrNull { it.isWithin(num) }
        val number = range?.destination(num) ?: num
        return EntityNumber(to, number)
    }
}

data class RangeMap(val destination: Long, val source: Long, val range: Long) {
    private val zeroRange = 0..<range
    fun offset(num: Long): Long = num - source

    fun isWithin(num: Long): Boolean = offset(num) in zeroRange

    fun destination(num: Long): Long = destination + offset(num)
}

enum class Category {
    Seed,
    Soil,
    Fertilizer,
    Water,
    Light,
    Temperature,
    Humidity,
    Location;

    companion object {
        fun valueOfCaseInSensitive(str: String): Category {
            return Category.valueOf(
                str
                    .lowercase()
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                        else it.toString()
                    }
            )
        }
    }
}