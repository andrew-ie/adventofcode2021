package dev.acraig.aoc.y2021

import java.io.File

fun day06(startState: List<Int>, vararg numDays: Int = intArrayOf(80, 256)) {
    val fishCounts = startState.groupBy { it }.mapValues { (_, value) -> value.size.toLong() }
    val map = generateSequence(Pair(fishCounts, 0L)) { (currentState, toSpawn) ->
        val newState = (0..7).associateWith { index ->
            if (index == 6) {
                (currentState[0] ?: 0L) + (currentState[7] ?: 0L)
            } else {
                currentState[index + 1] ?: 0L
            }
        } + mapOf(8 to toSpawn)
        newState to (newState[0] ?: 0)
    }.mapIndexed { index, pair -> index to pair.first.values.sum() }.filter { (index, _) -> numDays.contains(index) }
        .take(numDays.size).toMap()
    println(map)
}

fun main() {
    day06(listOf(3, 4, 3, 1, 2))
    val data = File("data/day06.txt").readText().split(",").map { it.trim().toInt() }
    day06(data)
}