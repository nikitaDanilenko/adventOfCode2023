package day01

import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import java.math.BigInteger

object Day01 {

    fun digits(string: String): List<Int> =
        string.flatMap { runCatching { listOf(it.toString().toInt()) }.getOrDefault(listOf()) }

    fun firstAndLast(ints: List<Int>): Result<Int> {
        println(ints)
        if (ints.isNotEmpty()) {
            println(ints.first())
            println(ints.last())
        }
        return kotlin.runCatching { 10 * ints.first() + ints.last() }
    }

    fun solutionWith(extractDigits: (String) -> List<Int>, input: String): BigInteger =
        input
            .lines()
            .sumOf { line ->
                firstAndLast(extractDigits(line)).fold(
                    { it.toBigInteger() },
                    { _ -> BigInteger.ZERO }
                )
            }

    fun part1(input: String): BigInteger =
        solutionWith(::digits, input)

    interface Entry
    class Digit(val value: Int) : Entry

    class Character(val char: Char) : Entry

    object EntryParser : Grammar<List<Entry>>() {

        // Todo: Understand 'by'. If 'use' is inlined after 'by' directly, no matches occur.
        val zero by regexToken("zero")
        val one by regexToken("one")
        val two by regexToken("two")
        val three by regexToken("three")
        val four by regexToken("four")
        val five by regexToken("five")
        val six by regexToken("six")
        val seven by regexToken("seven")
        val eight by regexToken("eight")
        val nine by regexToken("nine")

        val plain by regexToken("[0-9]")
        val char by regexToken("[a-z]")

        override val rootParser: Parser<List<Entry>>
                by zeroOrMore(
                    zero.use { Digit(0) }
                        .or(one.use { Digit(1) })
                        .or(two.use { Digit(2) })
                        .or(three.use { Digit(3) })
                        .or(four.use { Digit(4) })
                        .or(five.use { Digit(5) })
                        .or(six.use { Digit(6) })
                        .or(seven.use { Digit(7) })
                        .or(eight.use { Digit(8) })
                        .or(nine.use { Digit(9) })
                        .or(plain.use { Digit(text.toInt()) })
                        .or(char.use { Character(text[0]) })
                )
    }

    fun digitOf(entry: Entry): Result<Int> =
        when (entry) {
            is Digit -> Result.success(entry.value)
            is Character -> Result.failure(Exception("Character"))
            else -> Result.failure(Exception("Unknown"))
        }

    fun spelledToDigitInLine(string: String): List<Int> =
        EntryParser.parseToEnd(string).flatMap {
            digitOf(it).fold(
                { d -> listOf(d) },
                { _ -> listOf() }
            )
        }


    fun part2(input: String): BigInteger =
        solutionWith(::spelledToDigitInLine, input)


}
