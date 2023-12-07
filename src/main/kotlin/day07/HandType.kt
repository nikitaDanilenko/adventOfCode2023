package day07

enum class HandType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND;

    companion object {
        fun compare(handType1: HandType, handType2: HandType): Int = handType1.ordinal.compareTo(handType2.ordinal)
    }
}