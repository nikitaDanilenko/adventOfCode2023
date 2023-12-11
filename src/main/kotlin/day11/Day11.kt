package day11

import java.math.BigInteger
import kotlin.math.absoluteValue

object Day11 {

    data class Position(
        val line: Int, val column: Int
    )

    data class Image(
        val positions: Set<Position>,
        val emptyLines: Set<Int>,
        val emptyColumns: Set<Int>
    )

    private fun indicesOfEmptySpaces(lines: List<String>): Set<Int> =
        lines
            .mapIndexed { index, s -> index to s }
            .filter { it.second.all { c -> c == '.' } }
            .map { it.first }
            .toSet()

    private fun parseInput(input: String): Image {
        val lines = input.lines()
        val emptyLines = indicesOfEmptySpaces(lines)

        val columns =
            lines.first().indices.map { index ->
                lines.map { it[index] }.joinToString("")
            }
        val emptyColumns = indicesOfEmptySpaces(columns)
        val positions = lines
            .flatMapIndexed { lineIndex, line ->
                line.mapIndexed { columnIndex, c ->
                    Position(lineIndex, columnIndex) to c
                }
            }
            .filter { it.second == '#' }
            .map { it.first }
            .toSet()

        return Image(positions, emptyLines, emptyColumns)
    }

    private fun distance(
        a: Position,
        b: Position,
        emptyLines: Set<Int>,
        emptyColumns: Set<Int>,
        multiplier: BigInteger
    ): BigInteger {
        val lines = (minOf(a.line, b.line)).rangeTo(maxOf(a.line, b.line)).toSet()
        val columns = (minOf(a.column, b.column)).rangeTo(maxOf(a.column, b.column)).toSet()
        val emptyLs = lines.intersect(emptyLines)
        val emptyCs = columns.intersect(emptyColumns)
        val manhattanDistance = (a.line - b.line).absoluteValue + (a.column - b.column).absoluteValue
        return manhattanDistance.toBigInteger() + ((emptyLs.size + emptyCs.size).toBigInteger() * multiplier)
    }

    private fun <A> tails(list: List<A>): List<List<A>> {
        tailrec fun recur(tail: List<A>, tails: List<List<A>>): List<List<A>> =
            if (tail.isEmpty()) tails
            else recur(tail.drop(1), tails.plusElement(tail))

        return recur(list, emptyList())
    }


    private fun solution(image: Image, multiplier: BigInteger): BigInteger {
        val positionsList = image.positions.toList()

        return positionsList.zip(tails(positionsList).drop(1))
            .sumOf { (a, bs) ->
                bs.sumOf { b ->
                    distance(a, b, image.emptyLines, image.emptyColumns, multiplier)
                }
            }
    }

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val image = parseInput(input)
        // The modifiers are one smaller than the actual multiplier in the task,
        // because one occurrence is counted via the Manhattan distance already.
        return solution(image, BigInteger.ONE) to solution(image, BigInteger.valueOf(999999L))
    }


}