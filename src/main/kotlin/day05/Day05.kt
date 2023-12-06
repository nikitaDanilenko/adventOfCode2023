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

    fun part2(input: String): BigInteger =
        solution2(Instructions.parse(input).getOrThrow())

    data class Interval(
        val start: BigInteger,
        val end: BigInteger
    ) {
        override fun toString(): String =
            "[$start, $end]"


        companion object {

            private fun fromBounds(start: BigInteger, end: BigInteger): Interval? =
                if (start <= end) Interval(start, end) else null

            fun fromRangeMap(rangeMap: RangeMap): Interval =
                Interval(rangeMap.sourceStart, rangeMap.sourceStart + rangeMap.length - BigInteger.ONE)

            fun translate(interval: Interval, offset: BigInteger): Interval =
                Interval(interval.start + offset, interval.end + offset)

            data class SplitResult(
                val before: Interval?,
                val intersection: Interval,
                val after: Interval?
            )

            fun splitBy(interval: Interval, splitter: Interval): SplitResult? {
                val startIntersection = interval.start.max(splitter.start)
                val endIntersection = interval.end.min(splitter.end)

                val intersection = fromBounds(startIntersection, endIntersection)
                return intersection?.let {
                    SplitResult(
                        fromBounds(interval.start, startIntersection - BigInteger.ONE),
                        it,
                        fromBounds(endIntersection + BigInteger.ONE, interval.end)
                    )
                }
            }

        }
    }


    private fun solution2(instructions: Instructions): BigInteger {
        val startingIntervals = instructions
            .seeds
            .chunked(2)
            .map { Interval(it.first(), it.first() + it.last() - BigInteger.valueOf(1L)) }


        // In each step we have a list of given intervals that are mapped somewhere.
        // These intervals are split into smaller intervals with respect to the range maps of the current layer.
        // After the split, for each interval we have an optional sub-interval that is mapped via the range map.
        // Unmapped intervals (either part(s) of the interval or the whole interval) are kept for later range maps.
        // The assumption is that mapped areas never overlap.
        val targetIntervals = instructions.rangeMaps.fold(startingIntervals) { intervals, rangeMaps ->
            val (mappedDirectly, unmapped) = rangeMaps.fold(emptyList<Interval>() to intervals) { (mapped, unmapped), rangeMap ->
                val rangeMapInterval = Interval.fromRangeMap(rangeMap)
                unmapped.fold(mapped to emptyList()) { (mappedAcc, unmappedAcc), interval ->
                    // Does the interval intersect with the range map?
                    val splitResult = Interval.splitBy(interval, rangeMapInterval)
                    // If it does, the sub-intervals of the split are unmapped, and the intersection can be mapped.
                    // Otherwise, the interval is unmapped by the current range map.
                    val (mappable, addUnmapped) = splitResult?.let {
                        setOf(it.intersection) to listOfNotNull(
                            it.before,
                            it.after
                        )
                    } ?: (emptySet<Interval>() to listOf(interval))

                    // Add the newly mapped part, update the unmapped intervals.
                    // There is a possible optimization here, because the mapped intervals may form a continuous block.
                    // In that case, one case fuse multiple intervals into one.
                    // With a smaller number of intervals, the minimum is computable faster.
                    // However, fusing intervals is not trivial, and the number of intervals is rather small (110 for the final step).
                    (mappedAcc + mappable.map {
                        Interval.translate(
                            it,
                            rangeMap.targetStart - rangeMap.sourceStart
                        )
                    }) to (unmappedAcc + addUnmapped - mappable)
                }
            }

            // An interval is either mapped directly via one of the range maps,
            // or it is not matched by any range map. In that case, it is mapped to itself.
            // Since all range maps have been applied above, the unmapped intervals are mapped to themselves.
            mappedDirectly + unmapped
        }

        return targetIntervals.minOf { it.start }
    }

}