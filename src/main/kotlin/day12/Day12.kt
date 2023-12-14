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


    // 1. Iteration: Power set construction. Takes about 30 seconds for my input, unusable for part 2.
    // 2. Iteration: Smart solution construction. Takes about 0.3 seconds for my input, unusable for part 2.
    // 3. Iteration: Based on https://pastebin.com/djb8RJ85
    private fun countPossibleSolutions(
        line: Line
    ): BigInteger {

        val cache = mutableMapOf<Pair<List<Spring>, List<Int>>, BigInteger>()

        fun recur(
            springs: List<Spring>,
            groups: List<Int>,
        ): BigInteger =
            cache.getOrPut(springs to groups) {
                if (springs.isEmpty())
                // No springs, and no groups, i.e. the end has been reached -> one match
                    if (groups.isEmpty()) BigInteger.ONE
                    // No springs, but at least one group -> no match
                    else BigInteger.ZERO
                else {
                    when (springs.first()) {
                        Spring.OPERATIONAL ->
                            recur(springs.dropWhile { it == Spring.OPERATIONAL }, groups)

                        Spring.UNKNOWN ->
                            listOf(Spring.OPERATIONAL, Spring.DAMAGED).sumOf { spring ->
                                recur(listOf(spring) + springs.drop(1), groups)
                            }

                        Spring.DAMAGED ->
                            if (groups.isEmpty())
                            // No groups, and operational springs or question marks -> one match (all question marks become operational springs)
                                if (springs.all { it != Spring.DAMAGED }) BigInteger.ONE
                                // No groups, and at least one damaged spring -> no match
                                else BigInteger.ZERO
                            else {
                                val firstGroup = groups.first()
                                val remainingGroups = groups.drop(1)
                                if (firstGroup > springs.size)
                                // A previous group was too large, and the next group cannot be placed -> no match
                                    BigInteger.ZERO
                                else {
                                    val firstSpringGroup = springs.take(firstGroup)
                                    val remainingSprings = springs.drop(firstGroup)
                                    if (firstSpringGroup.any { it == Spring.OPERATIONAL })
                                    // The next group would contain an operational spring -> no match
                                        BigInteger.ZERO
                                    else if (remainingGroups.isEmpty()) {
                                        recur(remainingSprings, remainingGroups)
                                    } else {
                                        // First condition: There is more than one group left, but not enough springs to create the second one.
                                        // Second condition: After the first group there needs to be a non-damaged spring.
                                        if (springs.size < firstGroup + 1 || remainingSprings.first() == Spring.DAMAGED)
                                            BigInteger.ZERO
                                        else
                                            recur(remainingSprings.drop(1), remainingGroups)
                                    }
                                }

                            }
                    }
                }
            }

        return recur(line.springs, line.groups)
    }

}