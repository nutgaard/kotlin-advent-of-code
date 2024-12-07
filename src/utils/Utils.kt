package utils

fun List<String>.toCharGrid(): ArrayGrid<Char> {
    val grid = this
        .map { it.toCharArray().toTypedArray() }
        .toTypedArray()
    return ArrayGrid(grid)
}

fun <S, T, U> bind(s: S, fn: (S, T) -> U): (T) -> U {
    return { t -> fn(s, t) }
}

typealias Alphabet = Map<Int, String>
fun String.toAlphabet(): Alphabet {
    return this
        .split("")
        .filterNot { it.isEmpty() }
        .mapIndexed { index, s -> index to s }
        .toMap()
}

val ALPHA: Alphabet = "abcdefghijklmnopqrstuvwxyz".uppercase().toAlphabet()
val HEX: Alphabet = "0123456789abcdef".uppercase().toAlphabet()

fun Int.toAlpha(alphabet: Alphabet): String = this.toLong().toAlpha(alphabet)
fun Long.toAlpha(alphabet: Alphabet): String {
    var current = this
    return buildString {
        do {
            val rest: Int = (current % alphabet.size).toInt()
            append(alphabet[rest])
            current -= rest
            current /= alphabet.size
        } while (current > 0)
    }.reversed()
}