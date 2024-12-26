package year2015.day11

import utils.*


const val dir = "year2015/day11"
fun main() {
    timed("solve") {
        passwordSequence("hxbxwxba")
            .take(2)
            .toList()
            .println()
    }
}

fun passwordSequence(seed: String): Sequence<String> {
    val alphabet = Alphabet("abcdefghijklmnopqrstuvwxyz")
    val seedLong = seed.fromAlpha(alphabet)

    return generateSequence(seedLong) { it + 1 }
        .map { it.toAlpha(alphabet).padStart(8, 'a') }
        .filter(::validatePassword)
}

fun validatePassword(value: String): Boolean {
    val chars = value.toCharArray()

    return when {
        chars.size > 8 -> false
        chars.contains('i') -> false
        chars.contains('o') -> false
        chars.contains('l') -> false
        doubleCharsCount(chars) < 2 -> false
        !hasTriplet(chars) -> false
        else -> true
    }
}

fun doubleCharsCount(chars: CharArray): Int {
    var i = 0
    var c = 0

    while (i < chars.size - 1) {
        val a = chars[i]
        val b = chars[i + 1]
        if (a == b) {
            c++
            i += 2
        } else {
            i++
        }
    }

    return c
}

fun hasTriplet(chars: CharArray): Boolean {
    var i = 0
    while (i < chars.size - 2) {
        val a = chars[i] - 'a'
        val b = chars[i + 1] - 'b'
        val c = chars[i + 2] - 'c'

        if (a == b && b == c) return true

        i++
    }

    return false
}