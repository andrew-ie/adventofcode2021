package dev.acraig.aoc.y2021

import java.nio.file.Files
import java.nio.file.Path

fun day03part1(input: List<String>): Int {
    val gammaString = generateSequence("") { iteration ->
        val bitIndex = iteration.length
        if (bitIndex >= input.first().length) {
            null
        } else {
            val oneCount = input.count { it[bitIndex] == '1' }
            val zeroCount = input.size - oneCount
            iteration + (if (oneCount > zeroCount) "1" else "0")
        }
    }.last()
    val epsilonString = gammaString.map { if (it == '0') '1' else '0' }.joinToString("")
    val gamma = gammaString.toInt(2)
    val epsilon = epsilonString.toInt(2)
    val result = gamma * epsilon
    println("$gamma * $epsilon = $result")
    return result
}

fun day03Part2(input: List<String>): Int {
    val o2 = generateSequence(0 to input) { (bitIndex, currentO2) ->
        val oneCount = currentO2.count { it[bitIndex] == '1' }
        val zeroCount = currentO2.size - oneCount
        val useZero = zeroCount > oneCount
        bitIndex + 1 to currentO2.filter { if (useZero) it[bitIndex] == '0' else it[bitIndex] == '1' }
    }.first { (_, list) -> list.size == 1 }.second.first().toInt(2)
    val c02 = generateSequence(0 to input) { (bitIndex, currentC02) ->
        val oneCount = currentC02.count { it[bitIndex] == '1' }
        val zeroCount = currentC02.size - oneCount
        val useOne = oneCount < zeroCount
        bitIndex + 1 to currentC02.filter { if (useOne) it[bitIndex] == '1' else it[bitIndex] == '0' }
    }.first { (_, list) -> list.size == 1 }.second.first().toInt(2)
    val result = o2 * c02
    println("$o2 * $c02 = $result")
    return result
}

fun main() {
    val testdata = Files.readAllLines(Path.of("src/test/resources/day03testdata.txt"))
    println("Test Data ${day03part1(testdata)}, ${day03Part2(testdata)}")
    val data = Files.readAllLines(Path.of("data/day03.txt"))
    println("Real Data ${day03part1(data)}, ${day03Part2(data)}")
}