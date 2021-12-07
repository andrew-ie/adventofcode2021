package dev.acraig.aoc.y2021

import java.io.File
import kotlin.math.abs

fun day07Part1(input: List<Int>):Int {
    return day07(input) {
        it
    }
}
fun day07Part2(input: List<Int>):Int {
    return day07(input) { distance ->
        if (distance == 0) {
            0
        } else {
            (1..distance).sum()
        }
    }
}
fun day07(input: List<Int>, fuelCounter: (Int) -> Int):Int {
    val values = input.sorted()
    val result = (values.first()..values.last()).associateWith { destination ->
        input.sumOf { fuelCounter(abs(it - destination)) }
    }
    println(result)
    return result.values.minOrNull()!!
}

fun main() {
    val testData = listOf(16,1,2,0,4,2,7,1,2,14)
    println("${day07Part1(testData)}, ${day07Part2(testData)}")
    val data = File("data/day07.txt").readText().split(",").map { it.trim().toInt() }
    println("${day07Part1(data)}, ${day07Part2(data)}")
}