package dev.acraig.aoc.y2021

import java.io.File

data class Dice(val value: Int) {
    private fun roll(): Dice {
        val newValue = value.move(1, 100)
        return Dice(newValue)
    }

    fun roll(count: Int): List<Dice> {
        return generateSequence(roll()) { previous -> previous.roll() }.take(count).toList()
    }
}

data class Player(val position: Int, val score: Int) {
    fun move(amount: Int, updateScore: Boolean): Player {
        val newPosition = ((position + amount) % 10).let {
            if (it == 0) {
                10
            } else {
                it
            }
        }
        return if (updateScore) {
            Player(newPosition, score + newPosition)
        } else {
            copy(position = newPosition)
        }
    }
}

fun Int.move(count: Int, around: Int): Int {
    return ((this + count) % around).let {
        if (it == 0) {
            around
        } else {
            it
        }
    }
}

data class Universe(
    val player1PawnPosition: Int,
    val player2PawnPosition: Int,
    val scores: Map<List<Int>, Long>
) {
    private fun step1(): List<Universe> {
        return listOf(
            copy(player1PawnPosition = player1PawnPosition.move(1, 10)),
            copy(player1PawnPosition = player1PawnPosition.move(2, 10)),
            copy(player1PawnPosition = player1PawnPosition.move(3, 10))
        )
    }

    private fun step2(): List<Universe> {
        return listOf(
            copy(player2PawnPosition = player2PawnPosition.move(1, 10)),
            copy(player2PawnPosition = player2PawnPosition.move(2, 10)),
            copy(player2PawnPosition = player2PawnPosition.move(3, 10))
        )
    }

    fun roll1(): Collection<Universe> {
        val list = step1().flatMap { it.step1() }.flatMap { it.step1() }
        val newUniverses = list.groupBy { it }.mapValues { it.value.size }
        return newUniverses.mapKeys { (universe, count) ->
            val updatedScores = universe.scores.map { (key, value) ->
                listOf(key[0] + universe.player1PawnPosition, key[1]) to value
            }.groupBy { it.first }.mapValues { (_, value) -> value.map { it.second }.sumOf { it * count } }
            universe.copy(scores = updatedScores)
        }.keys
    }

    fun roll2(): Collection<Universe> {
        val list = step2().flatMap { it.step2() }.flatMap { it.step2() }
        val newUniverses = list.groupBy { it }.mapValues { it.value.size }
        return newUniverses.mapKeys { (universe, count) ->
            val updatedScores = universe.scores.map { (key, value) ->
                listOf(key[0], key[1] + universe.player2PawnPosition) to value
            }.groupBy { it.first }.mapValues { (_, value) -> value.map { it.second }.sumOf { it * count } }
            universe.copy(scores = updatedScores)
        }.keys
    }
}

fun day21Part1(input: List<String>): Int {
    val startPositions = input.map { it.substringAfterLast(':').trim().toInt() }.map { Player(it, 0) }
    val dice = Dice(0)
    val moves = generateSequence(startPositions to dice) { (playerPositions, currentDice) ->
        playerPositions.foldIndexed(emptyList<Player>() to currentDice) { index, (previousPlayers, previousDice), player ->
            val roll = previousDice.roll(3)
            val newPlayer = player.move(roll.sumOf { it.value }, true)
            Pair(previousPlayers + newPlayer, roll.last())
        }
    }.takeWhile { (player, _) -> player.none { (_, score) -> score >= 1000 } }.map { it.first }.toList()
    val moveCount = moves.count()
    val diceRollCount = if (moves.last()[0].score >= 1000) {
        moveCount * 6
    } else {
        moveCount * 6 - 3
    }
    val loserScore = moves.last().minOf { it.score }
    return diceRollCount * loserScore
}

fun day21Part2(input: List<String>): Long {
    val players = input.map { it.substringAfterLast(':').trim().toInt() }
    val startUniverse = Universe(players[0], players[1], mapOf(listOf(0, 0) to 1L))
    val universeCounts =
        generateSequence(Triple(listOf(startUniverse), listOf(0L, 0L), 0)) { (universes, winners, player) ->
            if (universes.isEmpty()) {
                null
            } else {
                val thisRound = if (player == 0) {
                    universes.flatMap { universe -> universe.roll1() }
                } else {
                    universes.flatMap { universe -> universe.roll2() }
                }
                val updatedUniverses = thisRound.groupBy { listOf(it.player1PawnPosition, it.player2PawnPosition) }
                val thisStep = updatedUniverses.values.map { universeList ->
                    universeList.reduce { left, right ->
                        val updatedScores = left.scores.toMutableMap().apply {
                            right.scores.forEach { (k, v) -> merge(k, v) { oldV, newV -> oldV + newV } }
                        }
                        left.copy(scores = updatedScores)
                    }
                }
                val thisStepWinners = thisStep.flatMap { it.scores.entries }.filter { (k, _) -> k.any { it >= 21 } }
                val p2WinnerCount = thisStepWinners.filter { (key, _) -> key[1] >= 21 }.sumOf { (_, value) -> value }
                val p1WinnerCount = thisStepWinners.filter { (key, _) -> key[0] >= 21 }.sumOf { (_, value) -> value }
                val nextRound = thisStep.map { universe ->
                    universe.copy(scores = universe.scores.filterKeys { score -> score.all { it < 21 } })
                }.filterNot { it.scores.isEmpty() }
                Triple(nextRound, listOf(winners[0] + p1WinnerCount, winners[1] + p2WinnerCount), (player + 1) % 2)
            }
        }.last().second
    return universeCounts.maxOrNull()!!
}

fun main() {
    val testData = """
        Player 1 starting position: 4
        Player 2 starting position: 8
    """.trimIndent().split("\n")
    println("Test Data: Deterministic: ${day21Part1(testData)}, Dirac: ${day21Part2(testData)}")
    val data = File("data/day21.txt").readLines()
    println("Real Data: Deterministic: ${day21Part1(data)}, Dirac: ${day21Part2(data)}")
}