package dev.acraig.aoc.y2021

import java.io.File

private sealed class Packet(val version: Int, val type: Int) {
    open fun versionSum():Int {
        return version
    }
    abstract fun evaluate():Long
}
private class LiteralPacket(version: Int, type: Int, val value: Long) : Packet(version, type) {
    override fun toString(): String {
        return "LiteralPacket(version=$version, type=$type, value=$value)"
    }

    override fun evaluate(): Long {
        return value
    }
}
private class OperatorPacket(version: Int, type: Int, val subPackets: List<Packet>) : Packet(version, type) {
    override fun toString(): String {
        return "OperatorPacket(version=$version, type=$type, subPackets=$subPackets)"
    }

    override fun versionSum(): Int {
        return super.versionSum() + subPackets.sumOf { it.versionSum() }
    }
    override fun evaluate():Long {
        return when (type) {
            0 -> subPackets.sumOf { it.evaluate() }
            1 -> subPackets.map { it.evaluate() }.reduce {a, b -> a * b }
            2 -> subPackets.minOf { it.evaluate() }
            3 -> subPackets.maxOf { it.evaluate() }
            5 -> if (subPackets[0].evaluate() > subPackets[1].evaluate()) 1L else 0L
            6 -> if (subPackets[0].evaluate() < subPackets[1].evaluate()) 1L else 0L
            7 -> if (subPackets[0].evaluate() == subPackets[1].evaluate()) 1L else 0L
            else -> throw UnsupportedOperationException("Unrecognized type $type")
        }
    }
}

private fun getPacket(input: String): Packet {
    val binaryString = input.map { "$it".toInt(16).toString(2).padStart(4, '0') }.joinToString("")
    return parsePacket(binaryString).first
}

private fun parsePacket(input:String): Pair<Packet, String> {
    val header = input.take(6)
    val version = header.take(3).toInt(2)
    val type = header.drop(3).take(3).toInt(2)
    val tail = input.drop(6)
    if (type == 4) {
        val data = tail.chunked(5)
        val blocks = data.subList(0, data.indexOfFirst { it.startsWith("0") } + 1)
        val value = blocks.joinToString("") { it.drop(1) }.toLong(2)
        val packet = LiteralPacket(version, type, value)
        val remainder = tail.drop(5 * blocks.size)
        return Pair(packet, remainder)
    } else {
        val lengthType = tail.first()
        if (lengthType == '0') {
            val length = tail.drop(1).take(15).toInt(2)
            val substring = tail.drop(16).take(length)
            val packets = generateSequence(parsePacket(substring)) { (_, remainder) ->
                if (remainder.isNotEmpty()) {
                    parsePacket(remainder)
                } else {
                    null
                }
            }.map { (packet, _) -> packet }.toList()
            val packet = OperatorPacket(version, type, packets)
            return Pair(packet, tail.drop(length + 16))
        } else {
            val numSubPackets = tail.drop(1).take(11).toInt(2)
            val substring = tail.drop(12)
            val packetAndRemainders = generateSequence(parsePacket(substring)) { (_, remainder) ->
                parsePacket(remainder)
            }.take(numSubPackets).toList()
            val packet = OperatorPacket(version, type, packetAndRemainders.map { it.first })
            return Pair(packet, packetAndRemainders.last().second)
        }
    }
}

fun main() {
    listOf("8A004A801A8002F478", "620080001611562C8802118E34", "C0015000016115A2E0802F182340", "A0016C880162017C3686B18A3D4780").associateWith(::getPacket)
        .mapValues { (_, value) -> value.versionSum() }.forEach { (testData, sum) -> println("Test Data (Part1): $testData = $sum") }
    listOf("C200B40A82", "04005AC33890", "880086C3E88112", "CE00C43D881120", "D8005AC2A8F0", "F600BC2D8F", "9C005AC2F8F0", "9C0141080250320F1802104A08").associateWith(::getPacket)
        .mapValues { (_, value) -> value.evaluate() }.forEach { (testData, result) -> println("Test Data (Part2): $testData = $result") }
    val packet = File("data/day16.txt").readText().trim().let(::getPacket)
    println("Version Sum: ${packet.versionSum()}, Evaluated result: ${packet.evaluate()}")
}