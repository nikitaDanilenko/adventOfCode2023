package day07

import java.math.BigInteger

object Day07 {

    fun part1(input: String): BigInteger =
        solution1(input.lines().map { Hand.parse(it).getOrThrow() })

    private fun solution1(hands: List<Hand>): BigInteger =
        hands
            .sorted()
            .mapIndexed { index, hand ->
                println("Hand $index: $hand")
                (1 + index).toBigInteger() * hand.bid
            }.sumOf { it }


    fun part2(input: String): BigInteger {
        return BigInteger.ZERO
    }
}