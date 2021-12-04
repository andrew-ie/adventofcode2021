package dev.acraig.aoc.y2021

import java.io.File

const val SIZE = 5

data class Board(val numbers: List<Int?>, val completedOrder: Int = -1) {
    val isCompletedRowOrColumn by lazy { numbers.isCompletedRowOrColumn() }
}

private fun List<Int?>.isCompletedRowOrColumn(): Boolean {
    return count { it == null } >= SIZE && (chunked(SIZE)
        .any { column -> column.all { it == null } }) || (
            (0 until SIZE).map(::getRow).any { row -> row.all { it == null } })
}
private fun List<Int?>.getRow(index: Int): List<Int?> {
    return (0 until SIZE).map { this[(it * SIZE) + index] }
}

private fun parseBoard(input: List<String>): Board {
    val numbers =
        input.filterNot(String::isBlank).map { it.split(" ").filterNot(String::isBlank).map(String::toInt) }.flatten()
    return Board(numbers)
}

fun day04Part1(callout: List<Int>, boards: List<Board>):Int {
    val complete = callout.asSequence().runningFold(Pair(boards, 0)) { (currentBoards, _), currentValue ->
        val newBoards =
            currentBoards.map { board -> board.copy(numbers = board.numbers.map { if (it == currentValue) null else it }) }
        Pair(newBoards, currentValue)
    }.first { (result, _) -> result.any(Board::isCompletedRowOrColumn) }
    val (completedBoards, lastCall) = complete
    val completedBoard = completedBoards.first(Board::isCompletedRowOrColumn)
    val boardScore = completedBoard.numbers.filterNotNull().sum()
    val result = lastCall * boardScore
    println("$lastCall * $boardScore = $result")
    return result
}

fun day04Part2(callout: List<Int>, boards: List<Board>):Int {
    val complete = callout.asSequence().runningFold(Pair(boards, 0)) { (currentBoards, _), currentValue ->
        val newBoards = currentBoards.map { board ->
            if (!board.isCompletedRowOrColumn) {
                val newNumbers = board.numbers.map { if (it == currentValue) null else it  }
                if (newNumbers.isCompletedRowOrColumn()) {
                    board.copy(numbers = newNumbers, completedOrder = currentBoards.maxOf { it.completedOrder } + 1)
                } else {
                    board.copy(numbers = newNumbers)
                }
            } else {
                board
            }
        }
        Pair(newBoards, currentValue)
    }.first { (result, _) -> result.all { it.completedOrder >= 0 } }
    val (completedBoards, lastCall) = complete
    val completedBoard = completedBoards.maxByOrNull { it.completedOrder }!!
    val boardScore = completedBoard.numbers.filterNotNull().sum()
    val result = lastCall * boardScore
    println("$lastCall * $boardScore = $result")
    return result
}

fun main() {
//    val input = File("src/test/resources/day04testdata.txt").readLines()
    val input = File("data/day04.txt").readLines()
    val callout = input[0].split(",").map(String::toInt)
    val boards = input.drop(2).chunked(SIZE + 1).map(::parseBoard)
    println("Part1: ${day04Part1(callout, boards)}, Part2: ${day04Part2(callout, boards)}")
}