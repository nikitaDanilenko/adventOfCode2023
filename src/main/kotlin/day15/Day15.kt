package day15

import java.math.BigInteger

object Day15 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = parseInput(input)
        return solution1(parsed) to solution2(parsed)
    }

    private fun parseInput(input: String): List<String> =
        input.split(",")

    private fun solution1(input: List<String>): BigInteger =
        input.sumOf { hash(it).toBigInteger() }

    private fun solution2(input: List<String>): BigInteger = BigInteger.ZERO

    private fun hash(input: String): Int =
        input.fold(0) { acc, c -> (acc + c.code) * 17 % 256 }
    
}