package day07

import java.math.BigInteger

data class Hand(
    val cards: List<Card>,
    val bid: BigInteger
) : Comparable<Hand> {

    companion object {

        fun parse(input: String): Result<Hand> = runCatching {
            val parts = input.split(" ")
            val cards = parts[0].map { c -> Card.parse(c.toString()).getOrThrow() }
            val bid = parts[1].toBigInteger()
            return Result.success(Hand(cards, bid))
        }

        fun kindOf(cards: List<Card>): Kind {
            val groups = cards.groupBy { it }.mapValues { it.value.size }.values
            return when (groups.size) {
                1 -> Kind.FIVE_OF_A_KIND
                2 -> if (groups.contains(4)) Kind.FOUR_OF_A_KIND else Kind.FULL_HOUSE
                3 -> if (groups.contains(3)) Kind.THREE_OF_A_KIND else Kind.TWO_PAIR
                4 -> Kind.ONE_PAIR
                else -> Kind.HIGH_CARD
            }
        }

        private fun compareCardLists(list1: List<Card>, list2: List<Card>): Int {
            return list1
                .zip(list2).map { (c1, c2) -> Card.compare(c1, c2) }
                .dropWhile { it == 0 }
                .firstOrNull() ?: 0
        }

    }


    override fun compareTo(other: Hand): Int {
        val kindResult = Kind.compare(kindOf(cards), kindOf(other.cards))
        return if (kindResult != 0)
            kindResult
        else compareCardLists(cards, other.cards)
    }

}