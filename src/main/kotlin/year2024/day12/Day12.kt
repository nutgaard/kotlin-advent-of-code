package year2024.day12

import utils.*

const val dir = "year2024/day12"
fun main() {
    val testInputPart1x1 = readInput("${dir}/Part01_1_test")
    val testInputPart1x2 = readInput("${dir}/Part01_2_test")
    val testInputPart1x3 = readInput("${dir}/Part01_3_test")
    val testInputPart2x1 = readInput("${dir}/Part02_1_test")
    val testInputPart2x2 = readInput("${dir}/Part02_2_test")

    verifySolution(testInputPart1x1, 140, ::part1)
    verifySolution(testInputPart1x2, 772, ::part1)
    verifySolution(testInputPart1x3, 1930, ::part1)

    verifySolution(testInputPart1x1, 80, ::part2)
    verifySolution(testInputPart1x2, 436, ::part2)
    verifySolution(testInputPart2x1, 236, ::part2)
    verifySolution(testInputPart2x2, 368, ::part2)

    val input = readInput("$dir/Input")
    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Int {
    return findRegions(input).sumOf { it.price() }
}

fun part2(input: List<String>): Int {
    return findRegions(input).sumOf { it.price2() }
}

data class Region(
    val char: Char,
    val coordinates: List<FencedCoordinate>
) {
    fun price(): Int = area() * perimeter()
    fun price2(): Int = area() * sides()

    private fun area(): Int = coordinates.size

    private fun perimeter(): Int {
        return coordinates.sumOf { it.fences.size }
    }

    private fun sides(): Int {
        val fences: List<Fence> = coordinates.flatMap { it.fences }
        val processedFences = mutableSetOf<Fence>()
        val segments = mutableListOf<FenceSegment>()

        for (fence in fences) {
            if (fence in processedFences) continue

            val segment = fences.findSegment(fence)
            segment.fences.forEach { processedFences.add(it) }
            segments.add(segment)
        }

        return segments.size
    }
}

class FencedCoordinate(
    val coordinate: Coordinate,
    var fences: MutableList<Fence> = mutableListOf(),
    val char: Char
): Coordinate by coordinate

data class FenceSegment(
    val fences: List<Fence>
)

data class Fence(
    val coordinate: Coordinate,
    val direction: Direction,
    val orientation: FenceOrientation
)
enum class FenceOrientation {
    VERTICAL,
    HORIZONTAL;

    fun directions(): Array<Direction> = when (this) {
        HORIZONTAL -> arrayOf(Direction.LEFT, Direction.RIGHT)
        VERTICAL -> arrayOf(Direction.UP, Direction.DOWN)
    }
}

fun findRegions(input: List<String>): List<Region> {
    val grid = input.toCharGrid()
    val coordinates = grid.indicies().toList()
    val visited = grid.map { false }
    val regions = mutableListOf<Region>()

    for (coordinate in coordinates) {
        if (visited.getValue(coordinate)) continue

        val region = grid.findRegionFrom(coordinate)
        region.coordinates.forEach { visited.setValue(it, true) }
        regions.add(region)
    }

    return regions
}

fun ArrayGrid<Char>.findRegionFrom(startingPoint: Coordinate): Region {
    val char = this.getValue(startingPoint)
    val stack = ConfigurableDeque.Stack<Coordinate>()
    val visited = mutableSetOf<Coordinate>()
    stack.push(startingPoint)

    while (stack.isNotEmpty()) {
        val coordinate = stack.pop()
        visited.add(coordinate)

        for (direction in Direction.entries) {
            val newCoordinate = coordinate.move(direction)

            if (newCoordinate notWithinBoundsOf this) continue
            if (this.getValue(newCoordinate) != char) continue
            if (newCoordinate in visited) continue

            stack.push(newCoordinate)
        }
    }

    val fencedCoordinates = visited.map {
        val fences = mutableListOf<Fence>()
        for (direction in Direction.entries) {
            val neighbour = it.move(direction)
            if (neighbour !in visited) {
                val fenceOrientation: FenceOrientation = when (direction) {
                    Direction.UP, Direction.DOWN -> FenceOrientation.HORIZONTAL
                    Direction.LEFT,Direction.RIGHT -> FenceOrientation.VERTICAL
                }
                fences.add(Fence(it, direction, fenceOrientation))
            }
        }
        FencedCoordinate(it, fences, char)
    }

    return Region(char, fencedCoordinates)
}


fun List<Fence>.findSegment(startingFence: Fence): FenceSegment {
    val byCoordinate = this.associateBy { it.coordinate to it.direction }
    val stack = ConfigurableDeque.Stack<Fence>()
    val visited = mutableSetOf<Fence>()
    stack.push(startingFence)

    while (stack.isNotEmpty()) {
        val fence = stack.pop()
        visited.add(fence)

        val fenceOrientation = fence.orientation
        val directions = fenceOrientation.directions()
        for (direction in directions) {
            val newCoordinate = fence.coordinate.move(direction)

            val newFence = byCoordinate[newCoordinate to fence.direction] ?: continue
            if (newFence.orientation != fenceOrientation) continue
            if (newFence.direction != fence.direction) continue
            if (newFence in visited) continue

            stack.push(newFence)
        }
    }

    return FenceSegment(visited.toList())
}