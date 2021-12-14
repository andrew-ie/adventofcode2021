package dev.acraig.aoc.y2021

import java.io.File

private data class Key(val value: String, val isLeft: Boolean = false)

fun day14(input: List<String>, vararg count: Int): List<Long> {
    val start = input.first()
    val template = input.drop(2).map {
        it.split(" -> ")
    }.associate { (rule, next) -> rule to next }
    val startPoint = start.windowed(2).mapIndexed { index, window ->
        Key(window, index == 0)
    }
    return generateSequence(startPoint.associateWith { 1L }) { previous ->
        previous.flatMap { (key, count) ->
            val infix = template[key.value]!!
            val left = key.copy(value = "${key.value.substring(0, 1)}$infix")
            val right = Key("$infix${key.value.substring(1)}")
            listOf(left to count, right to count)
        }.groupBy { it.first }.mapKeys { (key, _) -> key }.mapValues { (_, value) -> value.sumOf { it.second } }
    }.filterIndexed { index, _ -> count.contains(index) }.take(count.size).map { counts ->
        val chCounts =
            counts.flatMap { (key, value) -> if (key.isLeft) key.value.map { it to value } else listOf(key.value.last() to value) }
                .groupBy { it.first }.mapValues { (_, pair) -> pair.sumOf { it.second } }
        val max = chCounts.maxOf { it.value }
        val min = chCounts.minOf { it.value }
        max - min
    }.toList()
}

fun main() {
    val testdata = File("src/test/resources/day14testdata.txt").readLines()
    println("Test Data: ${day14(testdata, 10, 40)}")
    val data = File("data/day14.txt").readLines()
    println("Real Data: ${day14(data, 10, 40)}")
}