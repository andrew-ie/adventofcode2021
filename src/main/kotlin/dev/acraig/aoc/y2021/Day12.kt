package dev.acraig.aoc.y2021

import java.io.File

fun day12(pathList: List<String>, allowSmallTwice: Boolean): Int {
    val paths = pathList.map { it.split("-") }.flatMap { listOf(it[0] to it[1], it[1] to it[0]) }
        .groupBy({ it.first }) { it.second }.mapValues { (_, value) -> value.filterNot { it == "start" } }
    return paths.findPaths(emptyMap(), "start", allowSmallTwice).size
}

private fun String.isSmallCave(): Boolean {
    return all { it.isLowerCase() }
}

private fun Map<String, List<String>>.findPaths(
    previouslyVisited: Map<String, Int>,
    currentPosition: String,
    allowSmallTwice: Boolean = false
): Collection<List<String>> {
    if (currentPosition == "end") {
        return listOf(listOf(currentPosition))
    }
    val nextPossible = getOrDefault(currentPosition, emptyList())
    val visitedAnywhereTwice = previouslyVisited.any { (cave, count) -> cave.isSmallCave() && count > 1 }
    val routesToConsider = if (!allowSmallTwice || visitedAnywhereTwice) {
        nextPossible - previouslyVisited.keys
    } else {
        nextPossible
    }
    val nextEntries = routesToConsider.flatMap {
        val updatedPreviouslyVisited = if (it.isSmallCave()) {
            previouslyVisited + mapOf(it to previouslyVisited.getOrDefault(it, 0) + 1)
        } else previouslyVisited
        this.findPaths(updatedPreviouslyVisited, it, allowSmallTwice)
    }
    return nextEntries.map { list ->
        listOf(currentPosition) + list
    }
}

fun main() {
    val testdata = File("src/test/resources/day12testdata.txt").readLines()
    println("Part1: ${day12(testdata, false)}, Part2: ${day12(testdata, true)}")
    val data = File("data/day12.txt").readLines()
    println("Part1: ${day12(data, false)}, Part2: ${day12(data, true)}")
}