package day19

sealed interface Rule {

    data class Comparison(
        val selector: Selector,
        val relation: Relation,
        val boundary: Int,
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
                val selector = Selector.parse(comparatorString.first())
                val relation = Relation.parse(comparatorString[1])
                val value = comparatorString.drop(2).toInt()
                Comparison(
                    selector = selector,
                    relation = relation,
                    boundary = value,
                    target = parts[1]
                )
            } else when (input) {
                "A" -> Accept
                "R" -> Reject
                else -> GoTo(input)
            }


    }
}