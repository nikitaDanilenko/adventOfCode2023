package day07

enum class Kind {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND;

    companion object {
        fun compare(kind1: Kind, kind2: Kind): Int = kind1.ordinal.compareTo(kind2.ordinal)
    }
}