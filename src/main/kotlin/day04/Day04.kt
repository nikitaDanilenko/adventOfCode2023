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
        val number: Int,
        val winning: Set<Int>,
        val selected: Set<Int>
    )

    // Todo: Parsing newlines creates odd issues, hence we only parse lines individually.
    object GameParser : Grammar<Game>() {
        val number by regexToken("\\d+")
        val spaces by regexToken("\\s*")
        val colon by literalToken(":")

        val numberParser = number use { text.toInt() }
        val numbersParser = separatedTerms(numberParser, spaces)

        val separator by regexToken("\\|")
        val card by literalToken("Card", ignore = true)

        override val rootParser: Parser<Game> = (
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
                ).map { t -> Game(t.t1, t.t2.toSet(), t.t3.toSet()) }
    }

    fun solution1(games: List<Game>): BigInteger =
        games
            .map { g -> g.selected.intersect(g.winning) }
            .sumOf { if (it.isNotEmpty()) BigInteger.valueOf(2L).pow(it.size - 1) else BigInteger.ZERO }

    fun part1(input: String): BigInteger =
        solution1(
            input
                .lines()
                .map(GameParser::parseToEnd)
        )

}