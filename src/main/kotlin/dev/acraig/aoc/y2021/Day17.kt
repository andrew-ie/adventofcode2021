package dev.acraig.aoc.y2021

import java.io.File
import kotlin.math.abs

fun day17(input: String) {
    val (minX, maxX, minY, maxY) = """target area: x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)""".toRegex()
        .matchEntire(input)!!.groupValues.drop(1).map { it.toInt() }
    val targetWidth = IntRange(minX, maxX)
    val targetDepth = IntRange(minY, maxY)
    val possibleShots = (minY..abs(minY)).asSequence()
        .flatMap { yVelocity -> (0..maxX).map { xVelocity -> Pair(xVelocity, yVelocity) } }
        .filter { hit(it, targetWidth, targetDepth) }.toList()
    val highestVelocityUp = possibleShots.maxOf { it.second }
    val maxHeight = generateSequence(0 to highestVelocityUp) { (currHeight, currVelocity) ->
        currHeight + currVelocity to currVelocity - 1
    }.first { it.second == 0 }.first
    println("$input: Max Height: $maxHeight, Number of shots: ${possibleShots.size}")
}

fun hit(velocity: Pair<Int, Int>, targetWidth: IntRange, targetDepth: IntRange): Boolean {
    return fire(velocity).takeWhile { position -> position.first <= targetWidth.last && position.second >= targetDepth.first }
        .any { position -> targetWidth.contains(position.first) && targetDepth.contains(position.second) }
}

fun fire(velocity: Pair<Int, Int>): Sequence<Pair<Int, Int>> {
    return generateSequence(Pair(0, 0) to velocity) { (currPos, currVelocity) ->
        currPos.copy(currPos.first + currVelocity.first, currPos.second + currVelocity.second) to
                currVelocity.copy(
                    if (currVelocity.first > 0) currVelocity.first - 1 else 0,
                    currVelocity.second - 1
                )
    }.map { it.first }
}

fun main() {
    day17("target area: x=20..30, y=-10..-5")
    val data = File("data/day17.txt").readText().trim()
    day17(data)
}