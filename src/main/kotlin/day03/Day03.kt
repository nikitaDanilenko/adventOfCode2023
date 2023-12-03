package day03

import java.math.BigInteger

object Day03 {

    data class Position(
        val line: Int,
        val column: Int
    )

    fun findSymbols(input: String): Map<Position, Char> =
        input
            .lines()
            .mapIndexed { lineNumber, line ->
                line
                    .mapIndexed { columnNumber, column ->
                        Position(line = lineNumber, column = columnNumber) to column
                    }
                    .filter { it.second != '.' && !it.second.isDigit() }
            }
            .flatten()
            .toMap()

    // TODO: Not very efficient, but good enough for the moment
    fun <A> span(xs: List<A>, f: (A) -> Boolean): Pair<List<A>, List<A>> =
        xs.takeWhile(f) to xs.dropWhile(f)


    fun <A> collectGroupsBy(xs: List<A>, f: (A) -> Boolean): List<List<A>> {

        fun recur(list: List<A>): List<List<A>> {
            return if (list.isEmpty()) {
                emptyList()
            } else {
                val (first, second) = span(list, f)
                val recursive = recur(second.dropWhile { !f(it) })
                if (first.isEmpty())
                    recursive
                else
                    recursive.plusElement(first)
            }
        }

        return recur(xs).reversed()
    }

    data class NumberWithPosition(
        val number: Int,
        val position: Position,
        val length: Int
    )

    fun neighboursOf(numberWithPosition: NumberWithPosition): List<Position> {
        val leftMost = numberWithPosition.position.column - 1
        val rightMost = numberWithPosition.position.column + numberWithPosition.length
        val range = leftMost.rangeTo(rightMost)
        val leftRight = listOf(
            Position(numberWithPosition.position.line - 1, leftMost),
            Position(numberWithPosition.position.line, leftMost),
            Position(numberWithPosition.position.line + 1, leftMost),
            Position(numberWithPosition.position.line - 1, rightMost),
            Position(numberWithPosition.position.line, rightMost),
            Position(numberWithPosition.position.line + 1, rightMost),
        )
        val topBottom = range.flatMap {
            listOf(
                Position(numberWithPosition.position.line - 1, it),
                Position(numberWithPosition.position.line + 1, it)
            )
        }

        return leftRight + topBottom
    }

    fun findNumbers(input: String): List<NumberWithPosition> =
        input
            .lines()
            .flatMapIndexed { lineNumber, line ->
                val linesWithPositions =
                    line
                        .mapIndexed { columnNumber, column ->
                            Position(lineNumber, columnNumber) to column
                        }
                val groups = collectGroupsBy(linesWithPositions) { it.second.isDigit() }
                if (groups.all { it.isEmpty() })
                    emptyList()
                else
                    groups
                        .map { group ->
                            NumberWithPosition(
                                number = group
                                    .map { it.second }
                                    .joinToString("")
                                    .toInt(),
                                position = group.first().first,
                                length = group.size
                            )
                        }
            }

    fun solution1(numbersWithPosition: List<NumberWithPosition>, symbols: Map<Position, Char>): BigInteger =
        numbersWithPosition
            .filter {
                val neighbours = neighboursOf(it)
                neighbours.any { p -> symbols.containsKey(p) }
            }
            .sumOf { it.number.toBigInteger() }


    fun part1(input: String): BigInteger {
        val symbols = findSymbols(input)
        val numbersWithPosition = findNumbers(input)
        return solution1(numbersWithPosition, symbols)
    }
}