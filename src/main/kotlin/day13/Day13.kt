package day13

import day03.Day03
import java.math.BigInteger

object Day13 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = parseInput(input)
        return solution1(parsed) to solution2(parsed)
    }

    private fun solution1(patterns: List<Pattern>): BigInteger =
        patterns.sumOf { findFirstReflection(it).toBigInteger() }

    private fun solution2(lines: List<Pattern>): BigInteger = BigInteger.ZERO

    data class Position(
        val line: Int,
        val column: Int
    )

    data class Pattern(
        val positions: Set<Position>,
        val height: Int,
        val width: Int
    )

    private fun parseInput(input: String): List<Pattern> {
        fun parsePattern(lines: List<String>): Pattern {
            val positions = lines.flatMapIndexed { lineIndex, line ->
                line.flatMapIndexed { columnIndex, c ->
                    if (c == '#') listOf(Position(lineIndex, columnIndex))
                    else emptyList()

                }
            }
            return Pattern(positions.toSet(), lines.size, lines.first().length)
        }

        return Day03.collectGroupsBy(
            input.lines(),
        ) { it.isNotBlank() }
            .map(::parsePattern)
    }

    private fun findHorizontalReflection(pattern: Pattern): Int? {
        val lines = pattern.positions.groupBy { it.line }.mapValues { it.component2().map { pos -> pos.column } }
        val candidates =
            (0..pattern.height)
                .zipWithNext()
                .filter { (i1, i2) ->
                    val line1 = lines.getOrDefault(i1, emptyList())
                    val line2 = lines.getOrDefault(i2, emptyList())
                    line1 == line2
                }
                .map { it.first }
        val reflectionLines = candidates.filter { reflectsAlongLine(it, lines) }
        return reflectionLines.firstOrNull()
    }

    private fun transposePattern(pattern: Pattern): Pattern =
        Pattern(
            positions = pattern.positions.map { Position(line = it.column, column = it.line) }.toSet(),
            height = pattern.width,
            width = pattern.height
        )

    private fun findVerticalReflection(pattern: Pattern): Int? =
        findHorizontalReflection(transposePattern(pattern))

    private fun reflectsAlongLine(
        line: Int,
        lines: Map<Int, List<Int>>,
    ): Boolean {
        val counterLine = 2 * line + 1
        return (0..line).all { index ->
            // Assumption: There are no empty lines.
            // The assumption occurs twice (one time per line).
            val beforeReflection = lines[index]
            val counterIndex = counterLine - index
            val counterResult = lazy {
                val afterReflection = lines[counterIndex]
                beforeReflection == afterReflection
            }

            !lines.containsKey(counterIndex) || counterResult.value
        }
    }

    private fun findFirstReflection(pattern: Pattern): Int {
        val horizontal = findHorizontalReflection(pattern)
        val vertical = findVerticalReflection(pattern)
        return (horizontal?.let { (1 + it) * 100 }) ?: (1 + vertical!!)
    }

}