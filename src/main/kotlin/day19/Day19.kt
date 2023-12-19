package day19

import java.math.BigInteger

object Day19 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val instructions = WorkflowsAndParts.parse(input)
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
                if (Relation.compare(
                        rule.relation,
                        Categories.withSelector(rule.selector)(part.categories),
                        rule.boundary
                    )
                )
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


    data class Line(
        val from: Int,
        val to: Int
    ) {

        data class Split(
            val before: Line?,
            val after: Line?
        )

        companion object {
            fun split(line: Line, boundary: Int, relation: Relation): Split =
                if (boundary <= line.from)
                    Split(null, line)
                else if (boundary >= line.to)
                    Split(line, null)
                else
                    when (relation) {
                        Relation.LessThan -> Split(Line(line.from, boundary - 1), Line(boundary, line.to))
                        Relation.MoreThan -> Split(Line(line.from, boundary), Line(boundary + 1, line.to))
                    }


            fun length(line: Line): Int = if (line.from <= line.to) line.to - line.from + 1 else 0
        }

    }

    data class Cube(
        val x: Line,
        val m: Line,
        val a: Line,
        val s: Line
    ) {
        companion object {
            fun replaceAtSelector(cube: Cube, selector: Selector, line: Line): Cube = when (selector) {
                Selector.X -> cube.copy(x = line)
                Selector.M -> cube.copy(m = line)
                Selector.A -> cube.copy(a = line)
                Selector.S -> cube.copy(s = line)
            }

            fun volume(cube: Cube): BigInteger =
                listOf(cube.x, cube.m, cube.a, cube.s).map(Line::length)
                    .fold(BigInteger.ONE) { acc, length -> acc * length.toBigInteger() }
        }
    }

    sealed interface CubeStepResult {
        data class Accepted(val cube: Cube) : CubeStepResult
        data object Rejected : CubeStepResult
        data class Next(val rules: List<Rule>, val cube: Cube) : CubeStepResult
    }

    /*
       The idea is to iterate via 4-dimensional cubes, instead of single parts.
       The four dimensions are x, m, a, s for the individual categories.
       For every rule, and any given cube the following steps are performed:
       1. Split the cube into two sub-cubes, where the first one can be accepted, i.e. the sub-cube matches the rule,
          and the complementary cube, which cannot be accepted by this rule, but can still be accepted by the remaining rules.
       2. Trace the successful sub-cube, and collect all accepted sub-cubes from there.
          Then trace the possibly existing complementary sub-cube for all possibly existing remaining rules.
          Take the union of all accepted sub-cubes.
       3. Compute the volume of all accepted sub-cubes, and sum them up.

       N.B.: All accepted sub-cubes are pairwise disjoint, because every splitting step creates two disjoint sub-cubes,
             and all sub-cubes in the result are reached via splitting steps.
             Since the cubes are disjoint, the cardinality of their union is simply the sum of their individual cardinalities.

       The algorithm is likely poorly feasible for very large inputs, because the number of sub-cubes may grow very quickly.
       Since the cubes are pairwise disjoint, one cannot simply cache on the cubes.
       However, the input from AOC is benevolent, and yields only a few hundred sub-cubes,
       so that the entire computation takes far less than a second.
     */
    private fun solution2(workflowsAndParts: WorkflowsAndParts): BigInteger {
        val workflowMap = workflowsAndParts.workflows.associateBy { it.name }

        fun goToTarget(targetName: String, cube: Cube): CubeStepResult = when (targetName) {
            "A" -> CubeStepResult.Accepted(cube)
            "R" -> CubeStepResult.Rejected
            else -> CubeStepResult.Next(workflowMap[targetName]!!.rules, cube)
        }

        fun selectorToFunction(selector: Selector): (Cube) -> Line = when (selector) {
            Selector.X -> Cube::x
            Selector.M -> Cube::m
            Selector.A -> Cube::a
            Selector.S -> Cube::s
        }


        fun step(cube: Cube, rule: Rule): Pair<CubeStepResult, Cube?> =
            when (rule) {
                is Rule.Accept -> CubeStepResult.Accepted(cube) to null
                is Rule.Reject -> CubeStepResult.Rejected to null
                is Rule.GoTo -> goToTarget(rule.target, cube) to null
                is Rule.Comparison -> {
                    val selector = rule.selector
                    val lineToSplit = selectorToFunction(selector)(cube)
                    val split = Line.split(lineToSplit, rule.boundary, rule.relation)
                    val (subLine, subLineComplement) = when (rule.relation) {
                        Relation.LessThan -> split.before to split.after
                        Relation.MoreThan -> split.after to split.before
                    }
                    val subCube = subLine?.let { Cube.replaceAtSelector(cube, selector, it) }
                    val subCubeComplement = subLineComplement?.let { Cube.replaceAtSelector(cube, selector, it) }
                    val nextSubCube = subCube?.let { goToTarget(rule.target, it) } ?: CubeStepResult.Rejected
                    nextSubCube to subCubeComplement
                }
            }

        fun iterateStep(cube: Cube, rules: List<Rule>, accepted: List<Cube>): List<Cube> =
            if (rules.isEmpty())
                accepted
            else {
                val first = rules.first()
                val rest = rules.drop(1)
                val (result, complement) = step(cube, first)
                val acceptedViaSuccess = when (result) {
                    is CubeStepResult.Accepted -> accepted.plusElement(result.cube)
                    is CubeStepResult.Rejected -> accepted
                    is CubeStepResult.Next -> iterateStep(result.cube, result.rules, accepted)
                }
                val acceptedViaFailure = complement?.let { iterateStep(it, rest, emptyList()) } ?: emptyList()
                acceptedViaSuccess + acceptedViaFailure
            }

        val initialLine = Line(1, 4000)
        val initialCube = Cube(initialLine, initialLine, initialLine, initialLine)
        val accepted = iterateStep(initialCube, workflowMap["in"]!!.rules, emptyList())
        val sum = accepted.sumOf(Cube::volume)
        return sum
    }


}