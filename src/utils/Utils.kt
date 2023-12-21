package utils

fun List<String>.toCharGrid(): ArrayGrid<Char> {
    val grid = this
        .map { it.toCharArray().toTypedArray() }
        .toTypedArray()
    return ArrayGrid(grid)
}

