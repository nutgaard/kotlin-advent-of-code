package utils

import kotlin.math.max
import kotlin.math.min

data class MutableMinMax(var min: Long = Long.MAX_VALUE, var max: Long = Long.MIN_VALUE) {
    fun update(value: Long) {
        this.min = min(this.min, value)
        this.max = max(this.max, value)
    }

    fun size() = this.max - this.min + 1
}

data class MinMax(val min: Long, val max: Long) {
    fun size() = this.max - this.min + 1
}