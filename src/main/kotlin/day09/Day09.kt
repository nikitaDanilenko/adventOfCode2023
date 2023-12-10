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

    private fun previous(numbers: List<BigInteger>): BigInteger =
        generateSequence(numbers, ::differences)
            .takeWhile { !it.all { n -> n == BigInteger.ZERO } }
            .toList()
            .map { it.first() }
            .reversed()
            .reduce { acc, n ->
                n - acc
            }


    private fun solution1(lists: List<List<BigInteger>>): BigInteger =
        lists.sumOf(::next)

    private fun solution2(lists: List<List<BigInteger>>): BigInteger =
        lists.sumOf(::previous)

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = parseInput(input)
        return solution1(parsed) to solution2(parsed)
    }
}