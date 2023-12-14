import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

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

data class GridCoordinate(val row: Long, val column: Long)

fun <S> List<S>.pairs(): List<Pair<S, S>> = buildList {
    for (i in 0..<this@pairs.size) {
        for (j in i..<this@pairs.size) {
            this.add(this@pairs[i] to this@pairs[j])
        }
    }
}

inline fun <reified T> Array<Array<T>>.transpose(defaultValue: T): Array<Array<T>> {
    val rows = this.size
    val columns = this.first().size
    val transposed = Array(columns) {
        Array(rows) {
            defaultValue
        }
    }

    for (i in 0..<rows) {
        for (j in 0..<columns) {
            transposed[j][i] = this[i][j]
        }
    }

    return transposed
}