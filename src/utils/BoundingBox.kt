package utils

import kotlin.math.max
import kotlin.math.min

data class BoundingBox(
    val top: Long,
    val left: Long,
    val bottom: Long,
    val right: Long,
) {
    companion object {
        fun fromCoordinates(topLeft: Coordinate, bottomRight: Coordinate): BoundingBox {
            val top = min(topLeft.row, bottomRight.row)
            val bottom = max(topLeft.row, bottomRight.row)
            val left = min(topLeft.column, bottomRight.column)
            val right = max(topLeft.column, bottomRight.column)

            return BoundingBox(
                top = top,
                left = left,
                bottom = bottom,
                right = right,
            )
        }
    }
    fun border(): Set<Coordinate> = buildSet {
        (left..right).forEach {
            add(Coordinate(top, it))
            add(Coordinate(bottom, it))
        }
        (top..bottom).forEach {
            add(Coordinate(it, left))
            add(Coordinate(it, right))
        }
    }

    fun includes(coordinate: Coordinate): Boolean {
        return coordinate.row in (top..bottom) && coordinate.column in (left..right)
    }
}