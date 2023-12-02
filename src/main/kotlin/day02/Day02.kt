package day02

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

object Day02 {

    enum class Color {
        RED, GREEN, BLUE
    }

    data class Reveal(
        val color: Color,
        val amount: Int
    )

    data class RevealSet(
        val reveals: List<Reveal>
    )

    data class NumberedReveals(
        val number: Int,
        val reveals: List<RevealSet>
    )

    object NumberedRevealParser : Grammar<NumberedReveals>() {
        private val game by literalToken("Game")
        private val number by regexToken("\\d+")
        private val red by literalToken("red")
        private val green by literalToken("green")
        private val blue by literalToken("blue")

        private val spaces by regexToken("\\s*")
        private val comma by regexToken(",\\s+")
        private val semicolon by regexToken(";\\s+")
        private val colon by regexToken(":\\s*")

        private val colorParser = (red.use { Color.RED }) or (green use { Color.GREEN }) or (blue use { Color.BLUE })
        private val revealParser =
            ((number use { text.toInt() }) and spaces and colorParser).map { t -> Reveal(t.t3, t.t1) }
        private val revealSetParser = separatedTerms(revealParser, comma).map { t -> RevealSet(t) }
        private val gameNumberParser = (
                game
                        and spaces
                        and (number use { text.toInt() })
                        and colon
                ).map { t -> t.t3 }
        private val numberedRevealsParser =
            (gameNumberParser and separatedTerms(revealSetParser, semicolon)).map { t -> NumberedReveals(t.t1, t.t2) }

        override val rootParser: Parser<NumberedReveals> by numberedRevealsParser
    }

    fun parseNumberedReveals(input: String): List<NumberedReveals> =
        input.lines().map(NumberedRevealParser::parseToEnd)

    data class Limit(
        val red: Int,
        val green: Int,
        val blue: Int
    )

    fun fitsInto(reveal: Reveal, limit: Int): Boolean = reveal.amount <= limit

    fun fitsIntoAll(reveal: Reveal, limit: Limit): Boolean =
        when (reveal.color) {
            Color.RED -> fitsInto(reveal, limit.red)
            Color.GREEN -> fitsInto(reveal, limit.green)
            Color.BLUE -> fitsInto(reveal, limit.blue)
        }

    fun part1(input: String): Int =
        solution1(parseNumberedReveals(input))


    fun solution1(games: List<NumberedReveals>): Int {
        val limit = Limit(12, 13, 14)
        val sum = games
            .filter { numberedReveals ->
                numberedReveals.reveals.all { revealSet ->
                    revealSet.reveals.all { reveal ->
                        fitsIntoAll(
                            reveal,
                            limit
                        )
                    }
                }
            }
            .sumOf { numberedReveals -> numberedReveals.number }
        return sum
    }
    
}