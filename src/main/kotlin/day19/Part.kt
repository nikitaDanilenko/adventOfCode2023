package day19

data class Part(
    val categories: Categories
) {
    companion object {
        fun parse(input: String): Part = Part(Categories.parse(input))

    }
}