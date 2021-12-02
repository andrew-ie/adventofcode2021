package dev.acraig.aoc.y2021

import java.nio.file.Files
import java.nio.file.Path

private data class Position(val horizontal: Long = 0L, val vertical: Long = 0L, val aim: Long = 0L)
private enum class Direction {
    FORWARD,
    UP,
    DOWN
}

private data class Command(val direction: Direction, val magnitude: Long)

private fun parseCommand(string: String): Command {
    val (direction, magnitudeString) = string.split(" ")
    return Command(Direction.valueOf(direction.uppercase()), magnitudeString.toLong())
}

fun day02Part1(commands: List<String>): Long {
    val finalPosition = commands.map(::parseCommand).fold(Position()) { currPosition, nextCommand ->
        when (nextCommand.direction) {
            Direction.FORWARD -> currPosition.copy(horizontal = currPosition.horizontal + nextCommand.magnitude)
            Direction.UP -> currPosition.copy(vertical = currPosition.vertical - nextCommand.magnitude)
            Direction.DOWN -> currPosition.copy(vertical = currPosition.vertical + nextCommand.magnitude)
        }
    }
    return finalPosition.horizontal * finalPosition.vertical
}

fun day02Part2(commands: List<String>): Long {
    val finalPosition = commands.map(::parseCommand).fold(Position()) { currPosition, nextCommand ->
        when (nextCommand.direction) {
            Direction.FORWARD -> currPosition.copy(
                horizontal = currPosition.horizontal + nextCommand.magnitude,
                vertical = currPosition.vertical + (nextCommand.magnitude * currPosition.aim)
            )
            Direction.UP -> currPosition.copy(aim = currPosition.aim - nextCommand.magnitude)
            Direction.DOWN -> currPosition.copy(aim = currPosition.aim + nextCommand.magnitude)
        }
    }
    return finalPosition.horizontal * finalPosition.vertical
}

fun main() {
    val testData = Files.readAllLines(Path.of("src/test/resources/day02testdata.txt"))
    val data = Files.readAllLines(Path.of("data/day02.txt"))
    println("Test: Part1: ${day02Part1(testData)}, Part2: ${day02Part2(testData)}")
    println("Result: Part1: ${day02Part1(data)}, Part2: ${day02Part2(data)}")
}