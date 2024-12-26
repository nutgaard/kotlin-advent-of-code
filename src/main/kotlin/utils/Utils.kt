package utils

fun List<String>.toCharGrid(): ArrayGrid<Char> {
    val grid = this
        .map { it.toCharArray().toTypedArray() }
        .toTypedArray()
    return ArrayGrid(grid)
}
fun List<String>.toIntGrid(): ArrayGrid<Int> = this.toCharGrid().map { it - '0' }
fun List<String>.toLongGrid(): ArrayGrid<Long> = this.toCharGrid().map { (it - '0').toLong() }

fun <S, T, U> bind(s: S, fn: (S, T) -> U): (T) -> U {
    return { t -> fn(s, t) }
}


class Alphabet(value: String) {
    val size = value.length
    val toAlpha: Map<Int, Char> = value
        .toCharArray()
        .mapIndexed { index, s -> index to s }
        .toMap()

    val fromAlpha: Map<Char, Int> = toAlpha
        .map { (key, value) -> value to key }
        .toMap()
}

val ALPHA: Alphabet = Alphabet("abcdefghijklmnopqrstuvwxyz".uppercase())
val HEX: Alphabet = Alphabet("0123456789abcdef".uppercase())

fun Int.toAlpha(alphabet: Alphabet): String = this.toLong().toAlpha(alphabet)
fun Long.toAlpha(alphabet: Alphabet): String {
    var current = this
    return buildString {
        do {
            val rest: Int = (current % alphabet.size).toInt()
            append(alphabet.toAlpha[rest])
            current -= rest
            current /= alphabet.size
        } while (current > 0)
    }.reversed()
}

fun String.fromAlpha(alphabet: Alphabet): Long {
    val chars = this.toCharArray()
    var radix: Long = 1
    var result: Long = 0
    for (idx in this.indices) {
        val ch = chars[chars.size - 1 - idx]
        val value = alphabet.fromAlpha[ch] ?: error("Invalid format: $ch")

        result += value * radix
        radix *= alphabet.size
    }

    return result
}

fun main() {
    val alphabet = Alphabet("abcdefghijklmnopqrstuvwxyz")

    val r = "ba".fromAlpha(alphabet)
    require(r == 26L)

    repeat(100) {
        val a =  it.toLong().toAlpha(alphabet)
        val b = a.fromAlpha(alphabet)

        println("$it -> $a -> $b")
    }
}