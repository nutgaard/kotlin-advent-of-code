package year2015.day20

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import kotlin.math.floor
import kotlin.math.sqrt

const val dir = "year2015/day20"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    //verifySolution(testInputPart1, -1, ::part1)
    //verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    val target = 36000000L
    val houses = Array(1_000_000) { 10L }

    for (i in 1 ..< houses.size) {
        for (j in i..<houses.size step i) {
            houses[j] += i*10L
        }
    }

    return houses.indexOfFirst { it >= target }.toLong()
}

fun part2(input: List<String>): Long {
    val target = 36000000L
    val houses = Array(1_000_000) { 10L }

    for (i in 1 ..< houses.size) {
        var count = 0
        for (j in i..<houses.size step i) {
            houses[j] += i*11L
            if (++count == 50) break
        }
    }

    return houses.indexOfFirst { it >= target }.toLong()
}

fun findFactors(value: Long): List<Long> {
    val factors = mutableListOf(1L)
    val maxPrime = floor(sqrt(value.toDouble()))
    val primes = primes().takeWhile { it <= maxPrime }.toList()

    var current = value
    while(current > 1) {
        val factor = primes.firstOrNull { current % it == 0L } ?: current

        factors.add(factor)
        current /= factor
    }

    return factors
}

fun naturalNumbers(start: Long, step: Long = 1L): Sequence<Long> {
    return generateSequence(start) { it + step }
}

fun primes(): Sequence<Long> {
    return sequence {
        val primes = mutableListOf(2L, 3L)
        yieldAll(primes)

        yieldAll(naturalNumbers(start = 6, step = 6)
            .flatMap { listOf(it - 1, it + 1) }
            .filter {
                if (isPrime(it, primes)) {
                    primes.add(it)
                    true
                } else {
                    false
                }
            }
        )

    }
}

private fun isPrime(value: Long, primes: List<Long>): Boolean {
    val max = floor(sqrt(value.toDouble()))
    for (prime in primes) {
        if (value % prime == 0L) {
            return false
        }

        if (prime >= max) break
    }

    return true
}