package day07

import java.math.BigInteger

object Day07 {

    private fun parseInput(input: String): List<Hand> = input.lines().map { Hand.parse(it).getOrThrow() }
    fun part1(input: String): BigInteger =
        solutionWith(parseInput(input), NaturalComparator)

    private fun solutionWith(hands: List<Hand>, comparator: Comparator<Hand>): BigInteger =
        hands
            .sortedWith(comparator)
            .mapIndexed { index, hand ->
                (1 + index).toBigInteger() * hand.bid
            }.sumOf { it }


    fun part2(input: String): BigInteger =
        solution2(parseInput(input))

    private object NaturalComparator : Comparator<Hand> {
        override fun compare(o1: Hand?, o2: Hand?): Int = o1!!.compareTo(o2!!)
    }

    private object WithJokerComparator : Comparator<Hand> {
        override fun compare(o1: Hand?, o2: Hand?): Int = Hand.compareWithJokers(o1!!, o2!!)
    }

    private fun solution2(hands: List<Hand>): BigInteger =
        hands
            .sortedWith(WithJokerComparator)
            .mapIndexed { index, hand ->
                (1 + index).toBigInteger() * hand.bid
            }.sumOf { it }


}