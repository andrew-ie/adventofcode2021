package dev.acraig.aoc.y2021

import java.io.File

private data class Line(val startX: Int, val startY: Int, val endX: Int, val endY: Int) {
    fun buildPoints(): List<Pair<Int, Int>> {
        val startPoint = Pair(startX, startY)
        val endPoint = Pair(endX, endY)
        val points = generateSequence(startPoint) { currPoint ->
            if (currPoint == endPoint) {
                null
            } else {
                val (currX, currY) = currPoint
                val newX = next(currX, endX)
                val newY = next(currY, endY)
                Pair(newX, newY)
            }
        }.toList()
        return points
    }

    private fun next(current: Int, end: Int): Int {
        return when {
            current < end -> current + 1
            current > end -> current - 1
            else -> current
        }
    }
}

private fun buildLine(input: String): Line {
    val pattern = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex()
    val (startX, startY, endX, endY) = pattern.find(input)!!.groupValues.drop(1).map(String::toInt)
    return Line(startX, startY, endX, endY)
}

private fun day05(input: List<String>, filter: (Line) -> Boolean): Int {
    return input.asSequence().map(::buildLine).filter(filter)
        .flatMap(Line::buildPoints)
        .groupBy { point -> point }
        .mapValues { (_, list) -> list.size }
        .filterValues { count -> count > 1 }
        .size
}

fun day05Part1(input: List<String>): Int {
    return day05(input) {
        it.startX == it.endX || it.startY == it.endY
    }
}

fun day05Part2(input: List<String>): Int {
    return day05(input) {
        true
    }
}

fun main() {
    val testdata = File("src/test/resources/day05testdata.txt").readLines()
    println("${day05Part1(testdata)}, ${day05Part2(testdata)}")
    val data = File("data/day05.txt").readLines()
    println("${day05Part1(data)}, ${day05Part2(data)}")
}