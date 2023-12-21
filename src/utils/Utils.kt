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