package dev.acraig.aoc.y2021

import kotlinx.serialization.json.*
import java.io.File

sealed class SnailfishNode() {
    var parentSnailfish: SnailfishNode? = null
    abstract fun magnitude(): Long
    abstract fun toList(): List<SnailfishNode>
    fun depth() = generateSequence(parentSnailfish) {
        it.parentSnailfish
    }.count()
    abstract fun copy(): SnailfishNode
}
class SnailfishOrdinaryNumber(var ordinary: Long) : SnailfishNode() {
    override fun toString(): String {
        return "$ordinary"
    }

    override fun magnitude(): Long {
        return ordinary
    }

    override fun toList(): List<SnailfishNode> {
        return listOf(this)
    }

    override fun copy(): SnailfishNode {
        return SnailfishOrdinaryNumber(ordinary)
    }
}

class SnailfishNumber(
    var left: SnailfishNode,
    var right: SnailfishNode
) : SnailfishNode() {
    init {
        left.parentSnailfish = this
        right.parentSnailfish = this
    }

    override fun toString(): String {
        return "[$left, $right]"
    }

    override fun magnitude(): Long {
        return left.magnitude() * 3 + right.magnitude() * 2
    }

    override fun toList(): List<SnailfishNode> {
        return listOf(this) + left.toList() + right.toList()
    }

    override fun copy(): SnailfishNode {
        val leftCopy = left.copy()
        val rightCopy = right.copy()
        val newNode = SnailfishNumber(leftCopy, rightCopy)
        leftCopy.parentSnailfish = newNode
        rightCopy.parentSnailfish = newNode
        return newNode
    }
}

fun day18Part1(input: List<String>): Long {
    val numbers = input.map(::buildSnailfishNumber)
    val result = numbers.reduce { acc, snailfishNumber ->
        val next =
            SnailfishNumber(left = acc.copy(), right = snailfishNumber.copy())
        reduce(next)
    }
    println(result)
    return result.magnitude()
}

fun day18Part2(input: List<String>): Long {
    val numbers = input.map(::buildSnailfishNumber)
    return numbers.flatMap { left ->
        numbers.map { right ->
            if (left == right) 0L
            else {
                val result = reduce(
                    SnailfishNumber(
                        left = left.copy(),
                        right = right.copy()
                    )
                )
                result.magnitude()
            }
        }
    }.maxOrNull()!!
}

private tailrec fun reduce(input: SnailfishNumber): SnailfishNumber {
    val allNumbers = input.toList()
    val needsExploding = allNumbers.indexOfFirst { it is SnailfishNumber && it.depth() > 3 }
    if (needsExploding == -1) {
        val needsSplitting = allNumbers.filterIsInstance<SnailfishOrdinaryNumber>().find { it.ordinary > 9 }
        if (needsSplitting == null) {
            return input
        } else {
            val parent = needsSplitting.parentSnailfish as SnailfishNumber
            val newSnailfishNumber = SnailfishNumber(
                left = SnailfishOrdinaryNumber(needsSplitting.ordinary / 2),
                right = SnailfishOrdinaryNumber(needsSplitting.ordinary / 2 + needsSplitting.ordinary % 2)
            ).apply {
                parentSnailfish = parent
            }
            if (parent.left == needsSplitting) {
                parent.left = newSnailfishNumber
            } else {
                parent.right = newSnailfishNumber
            }
        }
    } else {
        val needsExplodingNumber = allNumbers[needsExploding] as SnailfishNumber
        val candidates = allNumbers.filter { it !== needsExplodingNumber.left && it !== needsExplodingNumber.right }
        val lastLeftNode = candidates.takeWhile { it !== needsExplodingNumber }.findLast { it is SnailfishOrdinaryNumber } as SnailfishOrdinaryNumber?
        lastLeftNode?.let {
            it.ordinary = it.ordinary + needsExplodingNumber.left.let { (it as SnailfishOrdinaryNumber).ordinary }
        }
        val firstRightNode = candidates.dropWhile { it !== needsExplodingNumber }.find { it is SnailfishOrdinaryNumber } as SnailfishOrdinaryNumber?
        firstRightNode?.let {
            it.ordinary = it.ordinary + needsExplodingNumber.right.let { (it as SnailfishOrdinaryNumber).ordinary }
        }
        needsExplodingNumber.parentSnailfish?.let { it ->
            val parent = it as SnailfishNumber
            val newNode = SnailfishOrdinaryNumber(0).apply {
                parentSnailfish = parent
            }
            if (parent.left == needsExplodingNumber) {
                parent.left = newNode
            } else {
                parent.right = newNode
            }
        }
    }
    return reduce(input)
}

private fun buildSnailfishNumber(input: String): SnailfishNumber {
    val json = Json.parseToJsonElement(input)
    return buildSnailfishNumber(json) as SnailfishNumber
}
private fun buildSnailfishNumber(input: JsonElement): SnailfishNode {
    return when (input) {
        is JsonArray -> SnailfishNumber(buildSnailfishNumber(input[0]), buildSnailfishNumber(input[1]))
        is JsonPrimitive -> SnailfishOrdinaryNumber(input.long)
        else -> throw UnsupportedOperationException("Don't recognize $input")
    }
}

fun main() {
    val test = """
        [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
        [[[5,[2,8]],4],[5,[[9,9],0]]]
        [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
        [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
        [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
        [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
        [[[[5,4],[7,7]],8],[[8,3],8]]
        [[9,3],[[9,9],[6,[4,9]]]]
        [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
        [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
    """.trimIndent().split("\n").toList()

    println("Total Magnitude: ${day18Part1(test)}, Highest Pair: ${day18Part2(test)}")
    val data = File("data/day18.txt").readLines()
    println("Total Magnitude: ${day18Part1(data)}, Highest Pair: ${day18Part2(data)}")
}