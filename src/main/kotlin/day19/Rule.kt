package day19

sealed interface Rule {

    data class Comparison(
        val predicate: (Categories) -> Boolean,
        val target: String
    ) : Rule

    data class GoTo(
        val target: String
    ) : Rule

    data object Accept : Rule

    data object Reject : Rule

    companion object {
        fun parse(input: String): Rule =
            if (input.contains(":")) {
                val parts = input.split(":")
                val comparatorString = parts[0]
                val selector = Categories.selectorByName(comparatorString.first())
                val comparator = comparatorBySymbol(comparatorString[1])
                val value = comparatorString.drop(2).toInt()
                Comparison(
                    predicate = { comparator(selector(it), value) },
                    target = parts[1]
                )
            } else when (input) {
                "A" -> Accept
                "R" -> Reject
                else -> GoTo(input)
            }

        private fun comparatorBySymbol(symbol: Char): (Int, Int) -> Boolean = when (symbol) {
            '>' -> { a, b -> a > b }
            '<' -> { a, b -> a < b }
            else -> { _, _ -> false }
        }
    }
}