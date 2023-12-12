package day12

import java.math.BigInteger

object Day12 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = parseInput(input)
        return solution1(parsed) to solution2(parsed)
    }

    private fun solution1(lines: List<Line>): BigInteger =
        lines.sumOf { countPossibleSolutions(it) }

    private fun solution2(lines: List<Line>): BigInteger =
        lines
            .sumOf { line ->
                val variantLine = Line(
                    springs = intersperse(List(5) { line.springs }, listOf(Spring.UNKNOWN)).flatten(),
                    groups = List(5) { line.groups }.flatten()
                )
                countPossibleSolutions(variantLine)
            }

    fun <A> intersperse(
        list: List<A>,
        separator: A
    ): List<A> =
        if (list.size <= 1) list
        else listOf(list.first(), separator) + intersperse(list.drop(1), separator)

    enum class Spring {
        OPERATIONAL, DAMAGED, UNKNOWN;
    }

    fun parseSpring(char: Char): Spring =
        when (char) {
            '.' -> Spring.OPERATIONAL
            '#' -> Spring.DAMAGED
            else -> Spring.UNKNOWN
        }


    private fun parseInput(input: String): List<Line> =
        input.lines().map(Line.Companion::parse)


    data class Line(
        val springs: List<Spring>,
        val groups: List<Int>
    ) {
        companion object {
            fun parse(input: String): Line {
                val parts = input.split(" ")
                val springs = parts[0].map(::parseSpring)
                val groups = parts[1].split(",").map { it.toInt() }
                return Line(springs, groups)
            }
        }
    }


    private val freePointCombinationsCache = mutableMapOf<Pair<Int, Int>, List<List<Int>>>()
    private fun generateFreePointCombinations(
        freePoints: Int,
        numberOfGroups: Int,
    ): List<List<Int>> {
        fun recur(
            freePoints: Int,
            numberOfGroups: Int,
        ): List<List<Int>> =
            if (numberOfGroups <= 1)
                freePointCombinationsCache.getOrPut(Pair(freePoints, numberOfGroups)) {
                    (0..freePoints).map { free -> listOf(freePoints - free, free) }
                }
            else {
                (0..freePoints).flatMap { first ->
                    val remaining = freePoints - first

                    freePointCombinationsCache.getOrPut(Pair(remaining, numberOfGroups - 1)) {
                        recur(remaining, numberOfGroups - 1)
                    }.map { rest ->
                        listOf(first) + rest
                    }
                }
            }

        return recur(freePoints, numberOfGroups)
    }

    fun possibleMatch(
        springs: List<Spring>,
        otherSprings: List<Spring>
    ): Boolean =
        springs.zip(otherSprings).all { (a, b) ->
            a == b || a == Spring.UNKNOWN || b == Spring.UNKNOWN
        }

    fun countPossibleSolutions(
        line: Line
    ): BigInteger {
        val damagedGroups = line.groups.map { List(it) { Spring.DAMAGED } }
        val freePoints = (line.springs.size - (line.groups.sumOf { it + 1 }) + 1)
        println(freePoints)
        println(line.groups.size)
        val baseExtensions = generateFreePointCombinations(freePoints, line.groups.size)
        val extensions = baseExtensions.map { free ->
            free.mapIndexed { index, f ->
                // There is a mandatory free space between groups, while the outermost spaces are optional.
                if (index > 0 && index < free.size - 1)
                    f + 1
                else
                    f
            }
        }
        val candidates = extensions.map { extension ->
            extension.zip(damagedGroups.plusElement(emptyList())).flatMap { (free, damaged) ->
                List(free) { Spring.OPERATIONAL } + damaged
            }
        }
        val responses = candidates.filter { possibleMatch(line.springs, it) }
        return responses.size.toBigInteger()
    }


    // Backtracking related
    data class RoseTree<A>(
        val node: A,
        val children: Lazy<List<RoseTree<A>>>
    )

    private fun <A> generate(
        initial: A,
        extend: (A) -> List<A>
    ): RoseTree<A> =
        RoseTree(
            initial,
            lazy { extend(initial).map { n -> generate(n, extend) } }
        )

    private fun <A> prune(
        tree: RoseTree<A>,
        canBeDismissed: (A) -> Boolean,
        isValid: (A) -> Boolean
    ): List<A> {
        fun countValid(
            tree: RoseTree<A>,
            valid: List<A>
        ): List<A> =
            if (canBeDismissed(tree.node)) valid
            else if (isValid(tree.node)) valid.plusElement(tree.node)
            else valid + tree.children.value.flatMap { countValid(it, valid) }

        return countValid(tree, emptyList())
    }

    private fun <A> pruneCountOnly(
        tree: RoseTree<A>,
        canBeDismissed: (A) -> Boolean,
        isValid: (A) -> Boolean
    ): BigInteger {
        fun collectValid(
            tree: RoseTree<A>,
            valid: BigInteger
        ): BigInteger =
            if (canBeDismissed(tree.node)) valid
            else if (isValid(tree.node)) BigInteger.ONE + valid
            else valid + tree.children.value.sumOf { collectValid(it, valid) }

        return collectValid(tree, BigInteger.ZERO)
    }
}