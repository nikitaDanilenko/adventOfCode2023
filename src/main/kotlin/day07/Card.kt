package day07

enum class Card(val symbol: String) : Comparable<Card> {
    ACE("A"),
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
    KING("K");

    companion object {
        private val shortHandMap = entries.associateBy(Card::symbol)
        fun parse(input: String): Result<Card> =
            if (shortHandMap.containsKey(input))
                Result.success(shortHandMap[input]!!)
            else Result.failure(IllegalArgumentException("Invalid card: $input"))


    }
}

