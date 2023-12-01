package day01

import java.math.BigInteger

class Day01 {

    fun digits(string: String): List<Int> =
        string.flatMap { runCatching { listOf(it.toString().toInt()) }.getOrDefault(listOf()) }

    fun firstAndLast(ints: List<Int>): Result<Int> =
        kotlin.runCatching { 10 * ints.first() + ints.last() }

    fun part1(input: String): BigInteger =
        input
            .lines()
            .sumOf { line ->
                firstAndLast(digits(line))
                    .fold(
                        { it.toBigInteger() },
                        { _ -> BigInteger.ZERO }
                    )
            }
}
