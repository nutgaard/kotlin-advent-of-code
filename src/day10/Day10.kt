package day10

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution
import kotlin.experimental.and
import kotlin.experimental.or

const val dir = "day10"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test1")
    val testInputPart3 = readInput("${dir}/Part02_test2")
    val testInputPart4 = readInput("${dir}/Part02_test3")
    val testInputPart5 = readInput("${dir}/Part02_test4")

    verifySolution(testInputPart1, 4, ::part1)
    verifySolution(testInputPart2, 4, ::part2)
    verifySolution(testInputPart3, 4, ::part2)
    verifySolution(testInputPart4, 8, ::part2)
    verifySolution(testInputPart5, 10, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    val diagram = Diagram.parse(input)
    return findLoop(diagram).size / 2
}

val verticalCrossing = arrayOf(
    MapEntity.BAR,
    MapEntity.J,
    MapEntity.L,
)
fun part2(input: List<String>): Int {
    val diagram = Diagram.parse(input)
    val loop = findLoop(diagram)
    diagram.set(diagram.startingPoint, diagram.startingPipe)

    val numberOfRows = diagram.rows.size
    val numberOfColumns = diagram.rows.first().size
    for (row in 0..<numberOfRows) {
        for (column in 0..<numberOfColumns) {
            val coordinate = Coordinate(row, column)
            if (!loop.contains(coordinate)) {
                diagram.set(coordinate, MapEntity.DOT)
            }
        }
    }

    val inside = mutableListOf<Coordinate>()

    for (row in 0..<numberOfRows) {
        for (column in 0..<numberOfColumns) {
            val coordinate = Coordinate(row, column)
            if (column == 0) continue
            else if (loop.contains(coordinate)) continue

            var crossing = 0
            var current = coordinate
            while (current.column > 0) {
                current = current.west()
                if (verticalCrossing.contains(diagram.get(current))) crossing++
            }


            if (crossing % 2 == 1){
                inside.add(coordinate)
            }
        }
    }
    return inside.size
}

fun findLoop(diagram: Diagram): List<Coordinate> {
    var currentPosition = diagram.startingPoint
    val loop = mutableListOf<Coordinate>()
    var currentEntity = diagram.startingPipe
    var past: Byte = 0b0000

    do {
        if (currentEntity.hasNorth() && past != SOUTH) {
            currentPosition = currentPosition.north()
            past = NORTH
        } else if (currentEntity.hasEast() && past != WEST) {
            currentPosition = currentPosition.east()
            past = EAST
        } else if (currentEntity.hasSouth() && past != NORTH) {
            currentPosition = currentPosition.south()
            past = SOUTH
        } else if (currentEntity.hasWest() && past != EAST) {
            currentPosition = currentPosition.west()
            past = WEST
        } else {
            error("Got into a place with no connections")
        }
        currentEntity = diagram.get(currentPosition)
        loop.add(currentPosition)
    } while (currentPosition != diagram.startingPoint)

    return loop
}


class Diagram private constructor(val rows: Array<Array<MapEntity>>) {
    val startingPoint: Coordinate by lazy {
        val rowIndex = rows.indexOfFirst { it.contains(MapEntity.START) }
        val columnIndex = rows[rowIndex].indexOf(MapEntity.START)
        Coordinate(rowIndex, columnIndex)
    }

    val startingPipe: MapEntity by lazy {
        MapEntity.fromConnections(
            (if (get(startingPoint.north()).hasSouth()) NORTH else 0) or
                    (if (get(startingPoint.south()).hasNorth()) SOUTH else 0) or
                    (if (get(startingPoint.west()).hasEast()) WEST else 0) or
                    (if (get(startingPoint.east()).hasWest()) EAST else 0)
        )
    }

    companion object {
        fun parse(input: List<String>): Diagram {
            val mapEntities = input
                .map { it
                    .toCharArray()
                    .map(MapEntity::fromChar)
                    .toTypedArray() }
                .toTypedArray()

            return Diagram(mapEntities)
        }
    }

    fun get(coordinate: Coordinate): MapEntity {
        if (coordinate.row < 0 || coordinate.row >= rows.size) return MapEntity.DOT
        if (coordinate.column < 0 || coordinate.column >= rows[0].size) return MapEntity.DOT
        return rows[coordinate.row][coordinate.column]
    }

    fun set(coordinate: Coordinate, entity: MapEntity) {
        rows[coordinate.row][coordinate.column] = entity
    }
}

const val NORTH: Byte = 0b1000
const val EAST: Byte = 0b0100
const val SOUTH: Byte = 0b0010
const val WEST: Byte = 0b0001

enum class MapEntity(val connections: Byte) {
    BAR(NORTH or SOUTH),
    HYPHEN(EAST or WEST),
    L(NORTH or EAST),
    J(NORTH or WEST),
    SEVEN(SOUTH or WEST),
    F(SOUTH or EAST),
    DOT(0),
    START(NORTH or EAST or SOUTH or WEST);

    fun hasNorth(): Boolean = this.connections and NORTH > 0
    fun hasEast(): Boolean = this.connections and EAST > 0
    fun hasSouth(): Boolean = this.connections and SOUTH > 0
    fun hasWest(): Boolean = this.connections and WEST > 0


    companion object {
        fun fromChar(char: Char): MapEntity {
            return when (char) {
                '|' -> BAR
                '-' -> HYPHEN
                'L' -> L
                'J' -> J
                '7' -> SEVEN
                'F' -> F
                '.' -> DOT
                'S' -> START
                else -> DOT
            }
        }

        fun fromConnections(connections: Byte): MapEntity {
            return entries.first { it.connections == connections }
        }
    }
}

data class Coordinate(val row: Int, val column: Int) {
    fun north() = Coordinate(row - 1, column)
    fun south() = Coordinate(row + 1, column)
    fun east() = Coordinate(row, column + 1)
    fun west() = Coordinate(row, column - 1)
}