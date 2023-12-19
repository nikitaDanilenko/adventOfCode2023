package day19

data class Categories(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int
) {
    companion object {
        fun selectorByName(char: Char): (Categories) -> Int = when (char) {
            'x' -> Categories::x
            'm' -> Categories::m
            'a' -> Categories::a
            's' -> Categories::s
            else -> { _ -> 0 }
        }

        fun parse(input: String): Categories {
            val values = input.drop(1).dropLast(1).split(",").map { it.drop(2).toInt() }
            return Categories(values[0], values[1], values[2], values[3])
        }
    }
}