package day04

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import java.math.BigInteger

object Day04 {

    data class Game(
        val winning: Set<Int>,
        val selected: Set<Int>
    )

    // Todo: Parsing newlines creates odd issues, hence we only parse lines individually.
    object GameParser : Grammar<Pair<Int, Game>>() {
        private val number by regexToken("\\d+")
        private val spaces by regexToken("\\s*")
        private val colon by literalToken(":")

        private val numberParser = number use { text.toInt() }
        private val numbersParser = separatedTerms(numberParser, spaces)

        private val separator by regexToken("\\|")
        private val card by literalToken("Card", ignore = true)

        override val rootParser: Parser<Pair<Int, Game>> = (
                skip(card)
                        and skip(spaces)
                        and numberParser
                        and skip(colon)
                        and skip(spaces)
                        and numbersParser
                        and skip(spaces)
                        and skip(separator)
                        and skip(spaces)
                        and numbersParser
                ).map { t -> t.t1 to Game(t.t2.toSet(), t.t3.toSet()) }
    }

    private fun solution1(games: List<Game>): BigInteger =
        games
            .map { g -> g.selected.intersect(g.winning) }
            .sumOf { if (it.isNotEmpty()) BigInteger.valueOf(2L).pow(it.size - 1) else BigInteger.ZERO }


    private fun solution2(games: List<Pair<Int, Game>>): BigInteger {
        val map = mutableMapOf(*games.map { it.first to BigInteger.valueOf(1L) }.toTypedArray())
        val result = games.fold(map) { acc, (number, game) ->
            // TODO: This feels very wrong due to mutability.
            List(game.selected.intersect(game.winning).size) { it + 1 + number }
                .forEach { index ->
                    acc[index] = acc.getOrDefault(index, BigInteger.ZERO) + acc.getOrDefault(number, BigInteger.ZERO)
                }
            acc
        }
            .values
            .sumOf { it }
        return result
    }


    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = input
            .lines()
            .map(GameParser::parseToEnd)
        return solution1(parsed.map { it.second }) to solution2(parsed)
    }
}