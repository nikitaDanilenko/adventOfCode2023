package day19

sealed interface Selector {
    data object X : Selector
    data object M : Selector
    data object A : Selector
    data object S : Selector

    companion object {
        fun parse(input: Char): Selector = when (input) {
            'x' -> X
            'm' -> M
            'a' -> A
            else -> S
        }
    }
}