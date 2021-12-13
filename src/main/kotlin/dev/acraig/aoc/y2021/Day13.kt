package dev.acraig.aoc.y2021

import java.io.File

fun day13Part1(input: List<String>) {
    val coordinates = input.subList(0, input.indexOf("")).map { it.split(",") }
        .associate { (x, y) -> Pair(x.toInt(), y.toInt()) to "#" }
    val folds = input.drop(coordinates.size + 1)
    generateSequence(Pair(coordinates, 0)) { (currCoordinates, foldIndex) ->
        if (foldIndex < folds.size) {
            val foldPoint = """fold along (\w)=(\d+)""".toRegex().matchEntire(folds[foldIndex])!!
            val horizontal = foldPoint.groupValues[1] == "y"
            val foldLine = foldPoint.groupValues[2].toInt()
            val newCoordinates = currCoordinates.mapKeys { entry ->
                val (x, y) = entry.key
                if (horizontal) {
                    if (y < foldLine) {
                        entry.key
                    } else {
                        Pair(x, foldLine - (y - foldLine))
                    }
                } else {
                    if (x < foldLine) {
                        entry.key
                    } else {
                        Pair(foldLine - (x - foldLine), y)
                    }
                }
            }
            newCoordinates to foldIndex + 1
        } else {
            null
        }
    }.map { it.first }.forEachIndexed { index, result ->
        println("After $index folds, ${result.values.count()} points")
        if (index == folds.size) {
            printGrid(result)
        }
    }
}

private fun printGrid(coordinates: Map<Pair<Int, Int>, String>) {
    val width = coordinates.maxOf { it.key.first }
    val height = coordinates.maxOf { it.key.second }
    val grid = (0..height).joinToString("\n") { y ->
        (0..width).joinToString("") { x ->
            coordinates.getOrDefault(Pair(x, y), " ")
        }
    }
    println("$grid\n(Width: $width, Height: $height")
}

fun main() {
    val testdata = File("src/test/resources/day13testdata.txt").readLines()
    day13Part1(testdata)
    val data = File("data/day13.txt").readLines()
    day13Part1(data)
}