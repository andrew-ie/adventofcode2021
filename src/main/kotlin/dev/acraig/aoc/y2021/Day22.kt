package dev.acraig.aoc.y2021

import java.io.File

private data class RebootBlock(val on: Boolean, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    fun removeBlock(other: RebootBlock): Collection<RebootBlock> {
        val overlapX = maxOf(xRange.first, other.xRange.first)..minOf(xRange.last, other.xRange.last)
        val overlapY = maxOf(yRange.first, other.yRange.first)..minOf(yRange.last, other.yRange.last)
        val overlapZ = maxOf(zRange.first, other.zRange.first)..minOf(zRange.last, other.zRange.last)
        val result = if (overlapX.isEmpty() || overlapY.isEmpty() || overlapZ.isEmpty()) {
            listOf(this)
        } else {
            listOf(
                //above
                copy(yRange = yRange.first until overlapY.first),
                //below
                copy(yRange = overlapY.last + 1..yRange.last),
                //left
                copy(yRange = overlapY, xRange = xRange.first until overlapX.first),
                //right
                copy(yRange = overlapY, xRange = overlapX.last + 1..xRange.last),
                //front
                copy(xRange = overlapX, yRange = overlapY, zRange = zRange.first until overlapZ.first),
                //behind
                copy(xRange = overlapX, yRange = overlapY, zRange = overlapZ.last + 1..zRange.last)
            )
                .filterNot { it.xRange.isEmpty() || it.yRange.isEmpty() || it.zRange.isEmpty() }
        }
        return result
    }

    fun count(): Long {
        return xRange.count().toLong() * yRange.count().toLong() * zRange.count().toLong()
    }
}

fun day22Part1(input: List<String>): Long {
    return day22(input) {
        it.xRange.first < 50 && it.xRange.last > -50 && it.yRange.first < 50 && it.yRange.last > -50 && it.zRange.first < 50 && it.zRange.last > -50
    }
}

fun day22Part2(input: List<String>): Long {
    return day22(input) { true }
}

private fun day22(input: List<String>, filter: (RebootBlock) -> Boolean): Long {
    val pattern = """(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""".toRegex()
    val reboots = input.map { line ->
        pattern.matchEntire(line)!!.groupValues.drop(1).let { entries ->
            val rangeEntries = entries.drop(1).map { it.toInt() }
            RebootBlock(
                entries[0] == "on",
                minOf(rangeEntries[0], rangeEntries[1])..maxOf(rangeEntries[0], rangeEntries[1]),
                minOf(rangeEntries[2], rangeEntries[3])..maxOf(rangeEntries[2], rangeEntries[3]),
                minOf(rangeEntries[4], rangeEntries[5])..maxOf(rangeEntries[4], rangeEntries[5])
            )
        }
    }
    val blocks = reboots.filter(filter).fold(emptySet<RebootBlock>()) { currBlock, group ->
        val blocks = currBlock.flatMap { it.removeBlock(group) }.toSet()
        if (group.on) {
            blocks + group
        } else {
            blocks
        }
    }
    return blocks.fold(0L) { acc, block -> acc + block.count() }
}

fun main() {
    val testData = File("src/test/resources/day22testdata.txt").readLines()
    println("Test Data Initialization: ${day22Part1(testData)}, Reboot: ${day22Part2(testData)}")
    val data = File("data/day22.txt").readLines()
    println("Actual Data Initialization: ${day22Part1(data)}, Reboot: ${day22Part2(data)}")
}