package util

import arrow.core.NonEmptyList
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.parser.Parser

// TODO: Check uses
object ParserUtil {

    fun <A> oneOf(parsers: NonEmptyList<Parser<A>>): Parser<A> =
        parsers.tail.fold(parsers.head) { acc, parser -> acc or parser }

}