package dev.acraig.aoc.y2021

import java.io.File

fun day09Part1(input: List<String>): Long {
    val coordinates = findCoordinates(input)
    val lowPoints = findLowPoints(coordinates)
    return lowPoints.values.sumOf { "$it".toLong() + 1 }
}

fun day09Part2(input: List<String>): Long {
    val coordinates = findCoordinates(input)
    val lowPoints = findLowPoints(coordinates)
    val plains = lowPoints.map { (position, _) ->
        generateSequence(setOf(position)) { nearby ->
            val extra =
                nearby.flatMap { coord -> coordinates.getAdjacent(coord).entries }.toSet().filterNot { it.value == '9' }
                    .filter { !nearby.contains(it.key) }
            if (extra.isEmpty()) {
                null
            } else {
                nearby + extra.map { it.key }
            }
        }.last()
    }
    return plains.sortedByDescending { it.size }.take(3).fold(1) { acc, set ->
        acc * set.size
    }
}

private fun findLowPoints(coordinates: Map<Pair<Int, Int>, Char>): Map<Pair<Int, Int>, Char> {
    val lowPoints = coordinates.filter { (position, value) ->
        val adjacentTiles = coordinates.getAdjacent(position).values
        adjacentTiles.all { it > value }
    }
    return lowPoints
}

private fun findCoordinates(input: List<String>): Map<Pair<Int, Int>, Char> {
    val coordinates = input.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, col ->
            Pair(colIndex, rowIndex) to col
        }
    }.toMap()
    return coordinates
}

private fun Map<Pair<Int, Int>, Char>.getAdjacent(position: Pair<Int, Int>): Map<Pair<Int, Int>, Char> {
    return listOf(
        position.copy(first = position.first - 1), position.copy(first = position.first + 1),
        position.copy(second = position.second - 1), position.copy(second = position.second + 1)
    ).associateWith { get(it) }.filterValues { it != null }.mapValues { it.value as Char }
}

fun main() {
    val testData = File("src/test/resources/day09testdata.txt").readLines()
    println("(Test) Low Points: ${day09Part1(testData)}, Biggest 3 plains: ${day09Part2(testData)}")
    val data = File("data/day09.txt").readLines()
    println("(Actual) Low Points: ${day09Part1(data)}, Biggest 3 plains: ${day09Part2(data)}")

}