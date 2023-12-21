package day21

import util.Position
import java.math.BigInteger

object Day21 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val elfMap = ElfMap.parse(input)
        return solution1(elfMap) to solution2(elfMap)
    }

    data class ElfMap(
        val startingPosition: Position,
        val rocks: Set<Position>,
        val height: Int,
        val width: Int
    ) {

        companion object {
            fun parse(input: String): ElfMap {
                val lines = input.lines()
                val height = lines.size
                val width = lines.first().length
                val all = lines.flatMapIndexed { lineIndex, line ->
                    line.mapIndexed { columnIndex, c ->
                        Position(lineIndex, columnIndex) to c
                    }
                }.toSet()
                val rocks = all.filter { it.second == '#' }.map { it.first }.toSet()
                val startingPosition = all.filter { it.second == 'S' }.map { it.first }.first()

                return ElfMap(startingPosition, rocks, height, width)
            }

            fun accessibleNeighbours(position: Position, elfMap: ElfMap): Set<Position> =
                Position.fourNeighbours(position)
                    .filter { (_, pos) ->
                        !elfMap.rocks.contains(pos) && pos.line >= 0 && pos.line < elfMap.height && pos.column >= 0 && pos.column < elfMap.width
                    }.map { it.second }
                    .toSet()

        }
    }

    private fun breadthFirstSearch(elfMap: ElfMap, steps: Int): Set<Position> {

        tailrec fun iterate(currentPositions: Set<Position>, steps: Int): Set<Position> {
            return if (steps <= 0)
                currentPositions
            else {
                val neighbours = currentPositions.flatMap { ElfMap.accessibleNeighbours(it, elfMap) }.toSet()
                iterate(neighbours, steps - 1)
            }
        }

        return iterate(setOf(elfMap.startingPosition), steps)
    }

    private fun solution1(elfMap: ElfMap): BigInteger {
        val steps = 64
        val positions = breadthFirstSearch(elfMap, steps)
        return positions.size.toBigInteger()
    }

    private fun solution2(elfMap: ElfMap): BigInteger {
        return BigInteger.ZERO
    }

}