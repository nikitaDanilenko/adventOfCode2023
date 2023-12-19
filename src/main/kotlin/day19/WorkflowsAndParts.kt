package day19

data class WorkflowsAndParts(
    val workflows: List<Workflow>,
    val parts: List<Part>,
) {
    companion object {
        fun parse(input: String): WorkflowsAndParts {
            val (workflows, parts) = input.split("\n\n")
            return WorkflowsAndParts(
                workflows = workflows.lines().map(Workflow::parse),
                parts = parts.lines().map(Part::parse)
            )
        }
    }
}