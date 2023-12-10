package day07

import java.math.BigInteger

object Day07 {

    private fun parseInput(input: String): List<Hand> = input.lines().map { Hand.parse(it).getOrThrow() }

    private fun solutionWith(hands: List<Hand>, comparator: Comparator<Hand>): BigInteger =
        hands
            .sortedWith(comparator)
            .mapIndexed { index, hand ->
                (1 + index).toBigInteger() * hand.bid
            }.sumOf { it }

    private object NaturalComparator : Comparator<Hand> {
        override fun compare(o1: Hand?, o2: Hand?): Int = o1!!.compareTo(o2!!)
    }

    private object WithJokerComparator : Comparator<Hand> {
        override fun compare(o1: Hand?, o2: Hand?): Int = Hand.compareWithJokers(o1!!, o2!!)
    }

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = parseInput(input)
        return solutionWith(parsed, NaturalComparator) to solutionWith(parsed, WithJokerComparator)
    }

}