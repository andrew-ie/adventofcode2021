package dev.acraig.aoc.y2021

import java.io.File

fun day08Part1(input: List<String>): Int {
    val entries = input.map { it.split("|") }.map { it[1] }
    return entries.flatMap { it.genSets() }.count { it.size == 2 || it.size == 3 || it.size == 4 || it.size == 7 }
}

fun day08Part2(input: List<String>): Long {
    return input.map { it.split("|") }.sumOf { (patterns, digits) ->
        evaluateDay(patterns, digits)
    }
}

private fun String.genSets(): List<Set<Char>> {
    return split(" ").map(String::trim).filterNot(String::isBlank).map(String::toSet)
}

@Suppress("UNCHECKED_CAST")
private fun evaluateDay(patternString: String, digits: String): Long {
    val patterns = patternString.genSets()
    val encoding = Array(10) { index ->
        when (index) {
            1 -> patterns.first { it.size == 2 }
            7 -> patterns.first { it.size == 3 }
            4 -> patterns.first { it.size == 4 }
            8 -> patterns.first { it.size == 7 }
            else -> null
        }
    }
    encoding[6] = patterns.first { it.size == 6 && !it.containsAll(encoding[1]!!) }
    encoding[9] = patterns.first { it.size == 6 && it.containsAll(encoding[4]!!) }
    encoding[0] = patterns.first { it.size == 6 && it != encoding[6] && it != encoding[9] }
    encoding[3] = patterns.first { it.size == 5 && it.containsAll(encoding[1]!!) }
    encoding[5] = patterns.first { it.size == 5 && encoding[6]!!.containsAll(it) }
    encoding[2] = patterns.first { it.size == 5 && it != encoding[5] && it != encoding[3] }
    val map = encoding.mapIndexed { index, set ->
        set as Set<Char> to index
    }.toMap()
    return digits.genSets().map {
        map[it]!!
    }.joinToString("").toLong()
}

fun main() {
    val testData = File("src/test/resources/day08testdata.txt").readLines()
    println("Test Data: ${day08Part1(testData)}, ${day08Part2(testData)}")
    val data = File("data/day08.txt").readLines()
    println("Actual Data: ${day08Part1(data)}, ${day08Part2(data)}")
}