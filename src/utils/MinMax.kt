package utils

import kotlin.math.max
import kotlin.math.min

class MutableMinMax(var min: Long, var max: Long) {
    fun update(value: Long) {
        this.min = min(this.min, value)
        this.max = max(this.max, value)
    }

    fun size() = this.max - this.min + 1
}

class MinMax(val min: Long, val max: Long) {
    fun size() = this.max - this.min + 1
}