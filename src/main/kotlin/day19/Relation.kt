package day19

sealed interface Relation {

    data object MoreThan : Relation
    data object LessThan : Relation

    companion object {
        fun parse(input: Char): Relation = when (input) {
            '>' -> MoreThan
            else -> LessThan
        }

        fun compare(relation: Relation, a: Int, b: Int): Boolean = when (relation) {
            MoreThan -> a > b
            LessThan -> a < b
        }
    }
}
