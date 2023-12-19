package day19

data class Categories(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int
) {
    companion object {

        fun parse(input: String): Categories {
            val values = input.drop(1).dropLast(1).split(",").map { it.drop(2).toInt() }
            return Categories(values[0], values[1], values[2], values[3])
        }

        fun withSelector(selector: Selector): (Categories) -> Int = when (selector) {
            Selector.X -> Categories::x
            Selector.M -> Categories::m
            Selector.A -> Categories::a
            Selector.S -> Categories::s
        }
    }
}