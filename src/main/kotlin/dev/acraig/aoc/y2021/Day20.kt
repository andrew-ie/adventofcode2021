package dev.acraig.aoc.y2021

import java.io.File

fun day20(input: List<String>, vararg count: Int): List<Int> {
    val pixelList = input.first()
    val image = input.drop(2).filterNot { it.isBlank() }.map { ".$it" }
    return generateSequence(image) { previousRun ->
        val edgeChar = previousRun.first().first()
        val edge = String(CharArray(previousRun.first().length) { edgeChar })
        val nextImage = (-1..previousRun.size).map { rowIdx ->
            (-1..previousRun.first().length).joinToString("") { colIdx ->
                val pixelId =
                    ((rowIdx - 1)..(rowIdx + 1)).map { if (it in previousRun.indices) previousRun[it] else edge }
                        .flatMap { row -> ((colIdx - 1)..(colIdx + 1)).map { if (it in row.indices) row[it] else edgeChar } }
                        .joinToString("")
                val id = pixelId.replace('.', '0').replace('#', '1').toInt(2)
                "${pixelList[id]}"
            }
        }
        nextImage
    }.filterIndexed { index, _ -> count.contains(index) }.take(count.size)
        .map { resultImage -> resultImage.sumOf { row -> row.count { it == '#' } } }.toList()
}

fun main() {
    val testdata = File("src/test/resources/day20testdata.txt").readLines()
    println("Test Data: ${day20(testdata, 2, 50)}")
    val data = File("data/day20.txt").readLines()
    println("Actual Data: ${day20(data, 2, 50)}")
}