package day06

import java.math.BigInteger

object Day06 {

    private fun parseInput(input: String): List<Race> {
        val numberLines = input.lines()
            .map {
                it
                    .split(" ")
                    .drop(1)
                    .filter(String::isNotEmpty)
                    .map { n ->
                        n.toInt()
                    }
            }

        return if (numberLines.size < 2) return emptyList()
        else {
            val times = numberLines[0]
            val distances = numberLines[1]
            times.zip(distances).map { Race(it.first, it.second) }
        }
    }

    private fun choices(race: Race): Int =
        1.rangeUntil(race.time)
            .count {
                val complement = race.time - it
                complement * it > race.distance
            }


    private fun solution1(races: List<Race>): BigInteger =
        races.map { choices(it).toBigInteger() }
            .fold(BigInteger.ONE) { acc, i -> acc * i }

    fun part1(input: String): BigInteger =
        solution1(parseInput(input))

    fun part2(input: String): BigInteger = BigInteger.ZERO

}