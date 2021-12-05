package dev.acraig.aoc.y2021

import java.io.File
import kotlin.math.max
import kotlin.math.min

private data class Line(val startX: Int, val startY: Int, val endX: Int, val endY: Int) {
    fun buildPoints(): List<Pair<Int, Int>> {
        val points = if (startX == endX) {
            (min(startY, endY)..max(startY, endY)).map { y -> Pair(startX, y) }
        } else if (startY == endY) {
            (min(startX, endX)..max(startX, endX)).map { x -> Pair(x, startY) }
        } else {
            val startPoint = Pair(startX, startY)
            generateSequence(startPoint) { currPoint ->
                if (currPoint == Pair(endX, endY)) {
                    null
                } else {
                    val newX = if (currPoint.first < endX) currPoint.first + 1 else currPoint.first - 1
                    val newY = if (currPoint.second < endY) currPoint.second + 1 else currPoint.second - 1
                    Pair(newX, newY)
                }
            }.toList()
        }
        return points
    }
}

private fun buildLine(input: String): Line {
    val pattern = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex()
    val (startX, startY, endX, endY) = pattern.find(input)!!.groupValues.drop(1).map(String::toInt)
    return Line(startX, startY, endX, endY)
}

private fun day05(input: List<String>, filter: (Line) -> Boolean): Int {
    val map = mutableMapOf<Pair<Int, Int>, Int>()
    input.asSequence().map(::buildLine).filter(filter)
        .flatMap(Line::buildPoints)
        .forEach { point ->
            map.compute(point) { _, currVal ->
                if (currVal == null) {
                    1
                } else {
                    currVal + 1
                }
            }
        }
    return map.values.filter { it > 1 }.size
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