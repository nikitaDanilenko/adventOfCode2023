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

        fun handTypeOf(cards: List<Card>): HandType {
            val groups = cards.groupBy { it }.mapValues { it.value.size }.values
            return when (groups.size) {
                1 -> HandType.FIVE_OF_A_KIND
                2 -> if (groups.contains(4)) HandType.FOUR_OF_A_KIND else HandType.FULL_HOUSE
                3 -> if (groups.contains(3)) HandType.THREE_OF_A_KIND else HandType.TWO_PAIR
                4 -> HandType.ONE_PAIR
                else -> HandType.HIGH_CARD
            }
        }

        private fun compareCardLists(list1: List<Card>, list2: List<Card>, comparison: (Card, Card) -> Int): Int {
            return list1
                .zip(list2).map { comparison(it.first, it.second) }
                .dropWhile { it == 0 }
                .firstOrNull() ?: 0
        }

        fun handTypeWithJokers(cards: List<Card>): HandType {
            val handType = handTypeOf(cards)
            return if (cards.contains(Card.JACK)) {
                val jokers = cards.count { it == Card.JACK }
                when (handType) {
                    HandType.FIVE_OF_A_KIND -> HandType.FIVE_OF_A_KIND
                    // There are exactly two card types, where one is the joker, hence all cards can be made identical.
                    HandType.FOUR_OF_A_KIND -> HandType.FIVE_OF_A_KIND
                    // Same reasoning as with four of a kind.
                    HandType.FULL_HOUSE -> HandType.FIVE_OF_A_KIND
                    // There are three card types, and one of those is the joker, i.e. four cards can be made identical.
                    HandType.THREE_OF_A_KIND -> HandType.FOUR_OF_A_KIND

                    HandType.TWO_PAIR ->
                        if (jokers == 1)
                        // One pair will turn into three of a kind, making the result a full house.
                            HandType.FULL_HOUSE
                        else
                        // The pair of jokers will turn into the cards from the other pair, making the result a four of a kind.
                            HandType.FOUR_OF_A_KIND

                    HandType.ONE_PAIR -> HandType.THREE_OF_A_KIND
                    HandType.HIGH_CARD -> HandType.ONE_PAIR
                }
            } else handType
        }

        fun compareWithJokers(hand1: Hand, hand2: Hand): Int {
            val handType1 = handTypeWithJokers(hand1.cards)
            val handType2 = handTypeWithJokers(hand2.cards)
            val handTypeResult = HandType.compare(handType1, handType2)
            return if (handTypeResult != 0)
                handTypeResult
            else compareCardLists(hand1.cards, hand2.cards, Card::compareJoker)
        }

    }


    override fun compareTo(other: Hand): Int {
        val handTypeResult = HandType.compare(handTypeOf(cards), handTypeOf(other.cards))
        return if (handTypeResult != 0)
            handTypeResult
        else compareCardLists(cards, other.cards, Card::compareNatural)
    }


}
