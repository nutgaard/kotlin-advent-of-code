package utils

enum class Direction(val row: Int, val column: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    RIGHT(0, 1),
    LEFT(0, -1);

    companion object {
        fun fromDir(str: Char): Direction = fromDir(str.toString())
        fun fromDir(str: CharSequence): Direction {
            return when (str) {
                "U" -> UP
                "^" -> UP
                "D" -> DOWN
                "v" -> DOWN
                "R" -> RIGHT
                ">" -> RIGHT
                "L" -> LEFT
                "<" -> LEFT
                else -> error("Unrecognized direction $str")
            }
        }
    }

    fun rotateClockwise(): Direction {
        return when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    fun rotateCounterClockwise(): Direction {
        return when (this) {
            UP -> LEFT
            RIGHT -> UP
            DOWN -> RIGHT
            LEFT -> DOWN
        }
    }

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }
    }
}

fun Coordinate.move(direction: Direction) = Coordinate.of(
    this.row + direction.row,
    this.column + direction.column,
)