package day05

import java.math.BigInteger

object Day05 {

    data class RangeMap(
        val targetStart: BigInteger,
        val sourceStart: BigInteger,
        val length: BigInteger
    ) {
        companion object {
            fun parse(input: String): Result<RangeMap> = runCatching {
                val parts = input.split(" ").map { it.toBigInteger() }
                RangeMap(parts.first(), parts[1], parts[2])

            }

            fun inRange(rangeMap: RangeMap, value: BigInteger): Boolean =
                value < rangeMap.sourceStart + rangeMap.length && value >= rangeMap.sourceStart
        }
    }

    private fun mapByRangeMap(
        rangeMap: RangeMap,
        value: BigInteger
    ): BigInteger {
        val offset = if (RangeMap.inRange(rangeMap, value)) {
            rangeMap.targetStart - rangeMap.sourceStart
        } else BigInteger.ZERO
        return value + offset
    }


    private fun mapToFirstMatching(
        rangeMaps: List<RangeMap>,
        value: BigInteger
    ): BigInteger = if (rangeMaps.isEmpty()) value else {
        val head = rangeMaps.first()
        if (RangeMap.inRange(head, value)) {
            mapByRangeMap(head, value)
        } else {
            mapToFirstMatching(rangeMaps.drop(1), value)
        }
    }

    private fun mapByRangeMapBlocks(
        rangeMapBlocks: List<List<RangeMap>>,
        value: BigInteger
    ): BigInteger = rangeMapBlocks.fold(value) { acc, rangeMaps ->
        mapToFirstMatching(rangeMaps, acc)
    }


    data class Instructions(
        val seeds: List<BigInteger>,
        val rangeMaps: List<List<RangeMap>>
    ) {
        companion object {
            fun parse(input: String): Result<Instructions> = runCatching {
                val parts = input.split("\n\n")
                val seeds = parts.first().dropWhile { !it.isDigit() }
                    .split(" ")
                    .map(String::toBigInteger)

                val rangeMaps = parts
                    .subList(1, parts.size)
                    .map {
                        it
                            .split("\n")
                            .drop(1)
                            .map { line ->
                                RangeMap.parse(line).getOrThrow()
                            }
                    }

                return Result.success(
                    Instructions(
                        seeds,
                        rangeMaps
                    )
                )
            }
        }
    }

    private fun solution1(instructions: Instructions): BigInteger =
        instructions.seeds.minOf { seed ->
            mapByRangeMapBlocks(instructions.rangeMaps, seed)
        }

    fun part1(input: String): BigInteger =
        solution1(Instructions.parse(input).getOrThrow())

    fun part2(input: String): BigInteger = BigInteger.ZERO
    

}