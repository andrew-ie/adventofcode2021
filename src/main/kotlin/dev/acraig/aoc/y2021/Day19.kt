package dev.acraig.aoc.y2021

import java.io.File
import kotlin.math.abs

data class Scanner(val id: Int, val coordinates: List<List<Int>>, val scannerPositions: Set<List<Int>> = emptySet()) {
    val pointDistances by lazy {
        coordinates.associateWith { point ->
            coordinates.filterNot { point == it }.map { point2 ->
                point.mapIndexed { direction, value ->
                    value - point2[direction]
                }
            }.toSet()
        }
    }

    private fun rotateX(): Scanner {
        val newCoords = coordinates.map { (x, y, z) ->
            listOf(x, z, -1 * y)
        }
        return copy(coordinates = newCoords)
    }

    private fun rotateY(): Scanner {
        val newCoords = coordinates.map { (x, y, z) ->
            listOf(-1 * z, y, x)
        }
        return copy(coordinates = newCoords)
    }

    private fun rotateZ(): Scanner {
        val newCoords = coordinates.map { (x, y, z) ->
            listOf(-1 * y, x, z)
        }
        return copy(coordinates = newCoords)
    }

    fun genRotationCombos(): Set<Scanner> {
        return generateSequence(this) {
            it.rotateX()
        }.take(4).flatMap { rotatedX -> generateSequence(rotatedX) { it.rotateY() }.take(4) }
            .flatMap { rotatedXAndY -> generateSequence(rotatedXAndY) { it.rotateZ() }.take(4) }.toSet()
    }
}

private fun buildScanners(input: String): List<Scanner> {
    val scannerDefs = input.split("\n\n")
    return scannerDefs.map { def -> def.split("\n") }.map { def ->
        val header = def.first()
        val id = header.filter { it.isDigit() }.toInt()
        val coordinates = def.drop(1).map { scannerLine ->
            val (x, y, z) = scannerLine.split(",").map { it.toInt() }
            listOf(x, y, z)
        }
        Scanner(id, coordinates)
    }
}

fun day19(input: String) {
    val scannerInput = buildScanners(input)
    val startScanner = scannerInput.first().copy(scannerPositions = setOf(listOf(0, 0, 0)))
    val remainingScanners = scannerInput.drop(1).flatMap { it.genRotationCombos() }
    val result = generateSequence(startScanner to remainingScanners) { (scanner, scanners) ->
        val nextMatch = scanners.associateWith { candidate ->
            val newDistances = candidate.pointDistances
            newDistances.mapValues { (_, distances) ->
                scanner.pointDistances.filterValues { (distances intersect it).size >= 11 }.keys
            }.filterValues { it.isNotEmpty() }
        }.filterValues { it.size >= 12 }
        val foundScanners = nextMatch.keys.map { it.id }
        val result = nextMatch.entries.fold(scanner) { oldScanner, (newScanner, foundPoints) ->
            val entry = foundPoints.entries.first()
            combineScanners(oldScanner, newScanner, entry.value.first(), entry.key)
        }
        if (foundScanners.isEmpty()) {
            null
        } else {
            Pair(result, scanners.filterNot { foundScanners.contains(it.id) })
        }
    }.last().first
    val part1 = result.coordinates.size
    val part2 = result.scannerPositions.flatMapIndexed { index, left ->
        result.scannerPositions.drop(index + 1).map { right ->
            "$left - $right" to distance(left, right)
        }
    }.maxByOrNull { it.second }!!
    println("Number of beacons: $part1. Max distance between two sensors: $part2")
}

fun distance(pos1: List<Int>, pos2: List<Int>): Long {
    return pos1.foldIndexed(0L) { index, sum, value ->
        sum + abs(value - pos2[index]).toLong()
    }
}

fun combineScanners(
    scanner0: Scanner,
    scanner1: Scanner,
    scanner0Location: List<Int>,
    thisScannerLocation: List<Int>
): Scanner {
    val offset = scanner0Location.mapIndexed { index, axisValue ->
        axisValue - thisScannerLocation[index]
    }
    val newPoints = scanner1.coordinates.map { value ->
        value.addOffset(offset)
    }
    return scanner0.copy(
        coordinates = (scanner0.coordinates + newPoints).distinct(),
        scannerPositions = scanner0.scannerPositions + setOf(offset)
    )
}

fun List<Int>.addOffset(extra: List<Int>): List<Int> {
    return mapIndexed { index, original -> original + extra[index] }
}

fun main() {
    val testdata = File("src/test/resources/day19testdata.txt").readText()
    day19(testdata)
    val data = File("data/day19.txt").readText().trim()
    day19(data)
}