package utils

import kotlin.math.sqrt

data class Vector(
    val x: Long,
    val y: Long,
) {
    fun length() = sqrt(((x * x) + (y * y)).toDouble())

    fun negate() = Vector(-x, -y)
}