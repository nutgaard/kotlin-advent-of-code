package utils

fun main() {
    permutations(listOf("a", "b", "c", "d", "e", "f", "g")).count().println()
}

fun <T> permutations(items: List<T>, qty: Int = items.size): Sequence<List<T>> = sequence {
    if (items.size < qty || qty == 0)
        return@sequence

    val availableIndices = Array<Int?>(items.size, { it })
    val iterators = Array<Iterator<Int>>(qty, { (0 until availableIndices.size).iterator() })
    val accumulator = Array<Int?>(qty, { null })
    var index = 0
    while (index >= 0) {
        if (index >= qty) {
            yield(accumulator.map { i -> items[i!!] }.toList())  // <-- return a permutation
            index--
        } else {
            if (iterators[index].hasNext()) {
                // return the value we took (if any)
                if (accumulator[index] != null) {
                    availableIndices[accumulator[index]!!] = accumulator[index]
                    accumulator[index] = null
                }

                // take the value from available indices and store it
                val selectedIndex = iterators[index].next()
                accumulator[index] = availableIndices[selectedIndex]
                availableIndices[selectedIndex] = null

                if (accumulator[index] != null) {
                    index++
                }
            } else {
                // return the value we took
                if (accumulator[index] != null) {
                    availableIndices[accumulator[index]!!] = accumulator[index]
                    accumulator[index] = null
                }

                // reset iterator
                iterators[index] = (0 until availableIndices.size).iterator()
                index--
            }
        }
    }
}