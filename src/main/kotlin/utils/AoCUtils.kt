package utils

import java.io.Writer
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.writeLines
import kotlin.io.path.writer
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/main/kotlin/$name.txt").readLines()
fun writerForOutput(name: String): Writer {
    return Path("src/main/kotlin/$name.txt").writer()
}
fun writeOutput(name: String, content: List<String>) {
    Path("src/main/kotlin/$name.txt").writeLines(content)
}

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