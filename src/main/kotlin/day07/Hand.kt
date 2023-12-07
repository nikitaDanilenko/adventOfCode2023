package day07

import java.math.BigInteger

data class Hand(
    val cards: List<Card>,
    val bid: BigInteger
) {

    companion object {

        fun parse(input: String): Result<Hand> = runCatching {
            val parts = input.split(" ")
            val cards = parts[0].map { c -> Card.parse(c.toString()).getOrThrow() }
            val bid = parts[1].toBigInteger()
            return Result.success(Hand(cards, bid))
        }
    }
}

