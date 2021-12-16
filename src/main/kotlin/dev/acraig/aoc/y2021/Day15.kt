package dev.acraig.aoc.y2021

import java.io.File
import java.util.*

private typealias Point = Pair<Int, Int>
private data class RiskMap(val points: Map<Point, Int>, val maxWidth: Int, val maxHeight: Int, val tileWidth:Int = points.maxOf { it.key.first } + 1, val tileHeight:Int = points.maxOf { it.key.second } + 1) {
    fun getRisk(point: Point):Int {
        val tileWidthShift = point.first / tileWidth
        val tileHeightShift = point.second / tileHeight
        val updatedPosition = Point(point.first % tileWidth, point.second % tileHeight)
        val basicScore = points[updatedPosition]!!
        return generateSequence(basicScore) { currScore -> if (currScore + 1 > 9) 1 else currScore + 1}.drop(tileWidthShift + tileHeightShift).first()
    }
}
fun day15Part1(input:List<String>):Int {
    val riskMap = input.flatMapIndexed { y, row -> row.mapIndexed { x, risk -> Point(x, y) to "$risk".toInt() } }.toMap()
    val startPoint = Point(0, 0)
    val endPoint = Point(riskMap.maxOf { it.key.first }, riskMap.maxOf { it.key.second })
    return route(startPoint, endPoint, RiskMap(riskMap, endPoint.first + 1, endPoint.second + 1))
}

fun day15Part2(input: List<String>): Int {
    val riskMap = input.flatMapIndexed { y, row -> row.mapIndexed { x, risk -> Point(x, y) to "$risk".toInt() } }.toMap()
    val startPoint = Point(0, 0)
    val endPoint = Point(riskMap.maxOf { it.key.first + 1 } * 5 - 1, riskMap.maxOf { it.key.second + 1} * 5 - 1)
    val map = RiskMap(riskMap, endPoint.first, endPoint.second)
    return route(startPoint, endPoint, map)
}

private fun route(start: Point, goal: Point, riskMap: RiskMap):Int {
    val score = mutableMapOf(start to 0)
    val openNodes = PriorityQueue<Point> { point1, point2 ->
        val p1 = score[point1]!! + (goal.first - point1.first) + (goal.second - point1.second)
        val p2 = score[point2]!! + (goal.first - point2.first) + (goal.second - point2.second)
        p1.compareTo(p2)
    }
    openNodes.add(start)
    while (openNodes.isNotEmpty()) {
        val current = openNodes.remove()
        if (current == goal) {
            return score[goal]!!
        }
        val currentScore = score.getOrDefault(current, Int.MAX_VALUE)
        current.getNeighbours().filter { it.first >= 0 && it.second >= 0 && it.first <= goal.first && it.second <= goal.second }.forEach { neighbour ->
            val tentativescore = currentScore + riskMap.getRisk(neighbour)
            if (tentativescore < score.getOrDefault(neighbour, Int.MAX_VALUE)) {
                score[neighbour] = tentativescore
                openNodes.add(neighbour)
            }
        }
    }
    throw Exception("Couldn't find way from $start to $goal")
}

private fun Point.getNeighbours(): List<Point> {
    return listOf(copy(first - 1, second), copy(first + 1, second), copy(first, second -1), copy(first, second + 1))
}
fun main() {
    val testdata = File("src/test/resources/day15testdata.txt").readLines()
    println("Test Data: ${day15Part1(testdata)}, ${day15Part2(testdata)}")
    val data = File("data/day15.txt").readLines()
    println("Actual Data: ${day15Part1(data)}, ${day15Part2(data)}")
}