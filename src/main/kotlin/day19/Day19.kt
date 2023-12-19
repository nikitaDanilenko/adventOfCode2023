package day19

import java.math.BigInteger

object Day19 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val instructions = WorkflowsAndParts.parse(input)
        println(instructions.workflows)
        return solution1(instructions) to solution2(instructions)
    }


    sealed interface StepResult {
        data object Accepted : StepResult
        data object Rejected : StepResult

        data object NextLocal : StepResult

        data class NextExternal(val rules: List<Rule>) : StepResult
    }

    private fun solution1(workflowsAndParts: WorkflowsAndParts): BigInteger {
        val workflowMap = workflowsAndParts.workflows.associateBy { it.name }
        val parts = workflowsAndParts.parts

        fun goToTarget(targetName: String): StepResult = when (targetName) {
            "A" -> StepResult.Accepted
            "R" -> StepResult.Rejected
            else -> StepResult.NextExternal(workflowMap[targetName]!!.rules)
        }

        fun step(part: Part, rule: Rule): StepResult = when (rule) {
            is Rule.Accept -> StepResult.Accepted
            is Rule.Reject -> StepResult.Rejected
            is Rule.GoTo -> goToTarget(rule.target)

            is Rule.Comparison -> {
                if (rule.predicate(part.categories))
                    goToTarget(rule.target)
                else StepResult.NextLocal
            }
        }

        tailrec fun iterateStep(part: Part, rules: List<Rule>, accepted: List<Part>): List<Part> =
            if (rules.isEmpty())
                accepted
            else {
                when (val result = step(part, rules.first())) {
                    StepResult.Accepted -> accepted.plus(part)
                    StepResult.Rejected -> accepted
                    StepResult.NextLocal -> iterateStep(part, rules.drop(1), accepted)
                    is StepResult.NextExternal -> iterateStep(part, result.rules, accepted)
                }
            }

        val accepted = parts.fold(emptyList<Part>()) { acc, part ->
            iterateStep(part, workflowMap["in"]!!.rules, acc)
        }

        val sum = accepted.sumOf { part ->
            listOf(Categories::x, Categories::m, Categories::a, Categories::s).map {
                it(part.categories)
            }.sumOf { it.toBigInteger() }
        }
        return sum
    }


    private fun solution2(workflowsAndParts: WorkflowsAndParts): BigInteger = BigInteger.ZERO


}