package day09

import java.math.BigInteger

object Day09 {

    private fun parseInput(input: String): List<List<BigInteger>> =
        input
            .lines()
            .map { it.split(" ").map { s -> s.toBigInteger() } }

    private fun differences(numbers: List<BigInteger>): List<BigInteger> =
        numbers
            .zipWithNext { a, b -> b - a }


    private fun next(numbers: List<BigInteger>): BigInteger =
        generateSequence(numbers, ::differences)
            .takeWhile { !it.all { n -> n == BigInteger.ZERO } }
            .toList()
            .sumOf { it.last() }

    fun solution1(lists: List<List<BigInteger>>): BigInteger =
        lists.sumOf(::next)

    fun part1(input: String): BigInteger =
        solution1(parseInput(input))

    fun part2(input: String): BigInteger =
        BigInteger.ZERO
}