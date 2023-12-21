package day02

import utils.println
import utils.readInput
import utils.verifySolution
import kotlin.math.max

const val dir = "day02"
val bagOfCubes = SetOfCubes(
    red = 12,
    green = 13,
    blue = 14
)

fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 8, ::part1)
    verifySolution(testInputPart2, 2286, ::part2)

    val input = readInput("$dir/Input")

    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Int {
    return input
        .map(Game::parse)
        .filter { it.isValid(bagOfCubes) }
        .sumOf { it.id }
}

fun part2(input: List<String>): Int {
    return input
        .map(Game::parse)
        .sumOf { it.minimalValidSet().powerValue() }
}

data class Game(
    val id: Int,
    val sets: List<SetOfCubes>
) {
    companion object {
        fun parse(input: String): Game {
            val (gameIdStr, setString) = input.split(":")
            val id = gameIdStr.removePrefix("Game ").toInt()
            val sets = setString.split(";").map(SetOfCubes::parse)
            return Game(id, sets)
        }
    }

    fun isValid(bagCubes: SetOfCubes): Boolean {
        return sets.all { it isPossibleGiven bagCubes }
    }

    fun minimalValidSet(): SetOfCubes {
        return this.sets.reduce { acc, other ->
            acc.minimalUnion(other)
        }
    }
}

data class SetOfCubes(
    val red: Int,
    val green: Int,
    val blue: Int,
) {
    companion object {
        fun parse(input: String): SetOfCubes {
            var red = 0
            var green = 0
            var blue = 0

            input
                .split(",")
                .map(String::trim)
                .forEach {
                    val (cubes, color) = it.split(" ")
                    when (color) {
                        "red" -> {
                            red = cubes.toInt()
                        }

                        "green" -> {
                            green = cubes.toInt()
                        }

                        "blue" -> {
                            blue = cubes.toInt()
                        }
                    }
                }

            return SetOfCubes(red, green, blue)
        }
    }

    infix fun isPossibleGiven(bagCubes: SetOfCubes): Boolean {
        return this.red <= bagCubes.red &&
                this.green <= bagCubes.green &&
                this.blue <= bagCubes.blue
    }

    fun minimalUnion(other: SetOfCubes): SetOfCubes {
        return SetOfCubes(
            red = max(this.red, other.red),
            green = max(this.green, other.green),
            blue = max(this.blue, other.blue),
        )
    }

    fun powerValue(): Int {
        return this.red * this.green * this.blue
    }
}