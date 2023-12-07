package day07

enum class Card(val symbol: String) {
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("T"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A");

    companion object {
        private val shortHandMap = entries.associateBy(Card::symbol)
        fun parse(input: String): Result<Card> =
            if (shortHandMap.containsKey(input))
                Result.success(shortHandMap[input]!!)
            else Result.failure(IllegalArgumentException("Invalid card: $input"))

        fun compareNatural(card1: Card, card2: Card): Int = card1.ordinal.compareTo(card2.ordinal)

        private fun jokerOrdinalOf(card: Card): Int = when (card) {
            JACK -> 0
            else -> card.ordinal + 1
        }

        fun compareJoker(card1: Card, card2: Card): Int =
            jokerOrdinalOf(card1).compareTo(jokerOrdinalOf(card2))
    }
}

