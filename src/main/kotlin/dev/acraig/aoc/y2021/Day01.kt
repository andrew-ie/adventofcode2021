package dev.acraig.aoc.y2021

import java.nio.file.Files
import java.nio.file.Path

fun day01Part1(input: List<Long>): Int {
    return input.windowed(2).count { it.first() < it.last() }
}
fun day01Part2(input: List<Long>): Int {
    val newInput = input.windowed(3).map { it.sum() }
    return day01Part1(newInput)
}

fun main() {
    val testData = Files.readAllLines(Path.of("src/test/resources/day01testdata.txt")).map { it.toLong() }
    val data = Files.readAllLines(Path.of("data/day01.txt")).map { it.toLong() }
    println("Test: ${day01Part1(testData)}, ${day01Part2(testData)}")
    println("Result: ${day01Part1(data)}, ${day01Part2(data)}")
}
