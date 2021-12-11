package dev.acraig.aoc.y2021

import java.io.File

private data class Coordinate(val x: Int, val y: Int)

private fun day11(input: List<String>): Sequence<Pair<Map<Coordinate, Int>, Long>> {
    val positions = input.flatMapIndexed { rowIndex, row ->
        row.trim().mapIndexed { colIndex, col ->
            Coordinate(colIndex, rowIndex) to "$col".toInt()
        }
    }.toMap()
    return generateSequence(positions to 0L) { (currentPosition, flashCount) ->
        val updatedEnergyLevels = currentPosition.mapValues { (_, value) -> value + 1 }
        val cascaded =
            generateSequence(updatedEnergyLevels to emptySet<Coordinate>()) { (repeatedEnergy, alreadyFlashed) ->
                val flashPoints = repeatedEnergy.filterValues { it > 9 }.keys - alreadyFlashed
                if (flashPoints.isEmpty()) {
                    null
                } else {
                    val adjacent = flashPoints.flatMap { repeatedEnergy.findAdjacent(it) }.groupBy { it }
                    repeatedEnergy.mapValues { (coordinate, level) ->
                        level + (adjacent[coordinate]?.size ?: 0)
                    } to alreadyFlashed + flashPoints
                }
            }.last()
        cascaded.first.mapValues { (_, level) -> if (level > 9) 0 else level } to flashCount + cascaded.second.size
    }
}

fun day11Part1(input: List<String>): Long {
    return day11(input).drop(100).map { (_, count) -> count }.first()
}

fun day11Part2(input: List<String>): Int {
    return day11(input).indexOfFirst { (grid, _) -> grid.values.all { it == 0 } }
}

private fun Map<Coordinate, Int>.findAdjacent(startPoint: Coordinate): Collection<Coordinate> {
    val offsets = listOf(-1, 0, 1)
    return offsets.flatMap { xOffset -> offsets.map { yOffset -> Pair(xOffset, yOffset) } }
        .filterNot { it.first == 0 && it.second == 0 }
        .map { startPoint.copy(x = startPoint.x + it.first, y = startPoint.y + it.second) }.filter(this::containsKey)
}

fun main() {
    val testData = File("src/test/resources/day11testdata.txt").readLines()
    println("Counts after 100 iterations: ${day11Part1(testData)}. First Sync Event: ${day11Part2(testData)}")

    val data = File("data/day11.txt").readLines()
    println("Counts after 100 iterations: ${day11Part1(data)}. First Sync Event: ${day11Part2(data)}")
}