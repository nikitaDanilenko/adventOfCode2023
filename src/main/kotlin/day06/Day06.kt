package day06

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

object Day06 {

    private fun parseInput(input: String): List<Race> {
        val numberLines = input.lines()
            .map {
                it
                    .split(" ")
                    .drop(1)
                    .filter(String::isNotEmpty)
                    .map { n ->
                        n.toBigInteger()
                    }
            }

        return if (numberLines.size < 2) return emptyList()
        else {
            val times = numberLines[0]
            val distances = numberLines[1]
            times.zip(distances).map { Race(it.first, it.second) }
        }
    }

    private fun ceilBigDecimal(bigDecimal: BigDecimal): BigInteger =
        bigDecimal.setScale(0, RoundingMode.CEILING).toBigInteger()

    private fun floorBigDecimal(bigDecimal: BigDecimal): BigInteger =
        bigDecimal.setScale(0, RoundingMode.FLOOR).toBigInteger()

    private const val SCALE: Int = 10

    /* The task in question requires solving a quadratic equation, but not for precise roots,
       but rather for being above or below a certain threshold:
       Give f(x) = x * (time - x)
       find the lowest x1 and highest x2 between which f(x) > distance holds.

       Note that f(x) = -x^2 + time * x, and we can solve f(x) = distance
       in a closed form using the quadratic formula.

       The two solutions need to be rounded up from below, and rounded down from above.
       Caveat: If the root term is an integer, and the half-term is an integer as well,
       the lower and upper bounds are the precise solutions, while the task requires to find
       solutions that are strictly larger than the distance.

       To accommodate the conditions:
         * round in the correct direction
         * modify by one in the case of a precise value
       we modify by one *before* rounding, and then round in the seemingly wrong direction.

     */
    private fun choices(race: Race): BigInteger {
        val time = race.time.toBigDecimal().setScale(SCALE)
        val distance = race.distance.toBigDecimal().setScale(SCALE)

        // The quadratic formula is: x1,2 = (time / 2) +- sqrt((time / 2)^2 - distance) = halfTerm +- rootTerm

        val halfTerm = time / 2.toBigDecimal().setScale(SCALE)

        val rootTerm = (halfTerm * halfTerm - distance).sqrt(MathContext(SCALE))

        val lower = floorBigDecimal(halfTerm - rootTerm + BigDecimal.ONE)
        val upper = ceilBigDecimal(halfTerm + rootTerm - BigDecimal.ONE)

        return upper - lower + BigInteger.ONE
    }


    private fun solution1(races: List<Race>): BigInteger =
        races.map { choices(it) }
            .fold(BigInteger.ONE) { acc, i -> acc * i }

    fun part1(input: String): BigInteger =
        solution1(parseInput(input))

    private fun parseInput2(input: String): Race {
        val numberLines = input.lines()
            .map {
                it
                    .dropWhile { c -> c != ' ' }
                    .filter(Char::isDigit)
                    .toBigInteger()

            }

        return Race(numberLines[0], numberLines[1])
    }

    fun part2(input: String): BigInteger {
        return solution1(listOf(parseInput2(input)))
    }


}