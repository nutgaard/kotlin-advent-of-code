package utils

import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
fun <INPUT, RES> verifySolution(input: INPUT, expectedResult: RES, algo: (INPUT) -> RES) {
    val result = timed("verifySolution") { algo(input) }
    check(result == expectedResult) {
        "Expected $expectedResult, but got $result"
    }
}

fun <OUT> timed(name: String, fn: () -> OUT): OUT {
    val result = measureTimedValue(fn)
    println("[$name] Calculated in ${result.duration}")
    return result.value
}