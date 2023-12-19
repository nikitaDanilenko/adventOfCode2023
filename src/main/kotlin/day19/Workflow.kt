package day19

data class Workflow(
    val name: String,
    val rules: List<Rule>
) {
    companion object {
        fun parse(input: String): Workflow {
            val name = input.takeWhile { it != '{' }
            val rules = input
                .drop(name.length + 1)
                .dropLast(1)
                .split(",")
                .map(Rule::parse)
            return Workflow(name, rules)
        }
    }
}