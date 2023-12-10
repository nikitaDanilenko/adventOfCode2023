package day01

import java.math.BigInteger

object Day01 {

    private fun digits(string: String): List<Int> =
        string.flatMap { runCatching { listOf(it.toString().toInt()) }.getOrDefault(listOf()) }

    private fun firstAndLast(ints: List<Int>): Result<Int> =
        kotlin.runCatching { 10 * ints.first() + ints.last() }

    private fun solutionWith(extractDigits: (String) -> List<Int>, input: String): BigInteger =
        input
            .lines()
            .sumOf { line ->
                firstAndLast(extractDigits(line)).fold(
                    { it.toBigInteger() },
                    { _ -> BigInteger.ZERO }
                )
            }

    // Words like "eightwo" or "oneight" need to be handled such that *both* numbers are found.
    // Hence, letters that are part of another digit are reinserted.
    private fun replace(line: String): String =
        line
            .replace("one", "o1e")
            .replace("two", "t2o")
            .replace("three", "t3e")
            .replace("four", "4")
            .replace("five", "5e")
            .replace("six", "6")
            .replace("seven", "7n")
            .replace("eight", "e8t")
            .replace("nine", "n9e")
            .replace("zero", "0o")


    private fun spelledToDigitInLine(string: String): List<Int> =
        digits(replace(string))


    fun solutions(input: String): Pair<BigInteger, BigInteger> =
        solutionWith(::digits, input) to solutionWith(::spelledToDigitInLine, input)

}
