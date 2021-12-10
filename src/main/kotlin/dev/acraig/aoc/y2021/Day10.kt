package dev.acraig.aoc.y2021

import java.io.File

private fun scoreLine(input: String): Pair<List<Char>, Long> {
    val stack = input.runningFold(emptyList<Char>() to 0L) { (currentStack, _), nextCh ->
        val isOpen = "[(<{".contains(nextCh)
        if (isOpen) {
            currentStack + nextCh to 0L
        } else {
            val last = currentStack.last()
            val newScore = when (nextCh) {
                ']' -> if (last == '[') 0L else 57L
                ')' -> if (last == '(') 0L else 3L
                '}' -> if (last == '{') 0L else 1197L
                '>' -> if (last == '<') 0L else 25137L
                else -> 0
            }
            currentStack.dropLast(1) to newScore
        }
    }
    return stack.firstOrNull { (_, score) -> score > 0L } ?: stack.last()
}

fun day10Part1(input: List<String>): Long {
    return input.sumOf { line -> scoreLine(line).second }
}

fun day10Part2(input: List<String>): Long {
    val results = input.asSequence().map { scoreLine(it) }.filter { it.second == 0L }.map { it.first }.map { stack ->
        stack.foldRight(0L) { ch, score ->
            5L * score + when (ch) {
                '(' -> 1L
                '[' -> 2L
                '{' -> 3L
                else -> 4L
            }
        }
    }.sorted().toList()
    return results[results.size / 2]
}

fun main() {
    val testdata = File("src/test/resources/day10testdata.txt").readLines()
    println("Corrupt: ${day10Part1(testdata)}, Incomplete: ${day10Part2(testdata)}")
    val data = File("data/day10.txt").readLines()
    println("Corrupt: ${day10Part1(data)}, Incomplete: ${day10Part2(data)}")
}

