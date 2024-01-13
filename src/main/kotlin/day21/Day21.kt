package day21

import util.Direction
import util.Position
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

object Day21 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val elfMap = ElfMap.parse(input)
        return solution1(elfMap) to solution2(input)
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
                        !elfMap.rocks.contains(pos) && inBounds(elfMap.height, pos)
                    }.map { it.second }
                    .toSet()

            fun inBounds(size: Int, position: Position): Boolean =
                position.line >= 0 && position.line < size && position.column >= 0 && position.column < size

            fun loopedNeighbours(position: Position, elfMap: ElfMap): Set<Position> =
                Position.fourNeighbours(position)
                    .filter { (_, pos) ->
                        val corresponding =
                            pos.copy(line = pos.line.mod(elfMap.height), column = pos.column.mod(elfMap.height))
                        !elfMap.rocks.contains(corresponding)
                    }
                    .map { it.second }
                    .toSet()

        }
    }

    private fun breadthFirstSearch(
        elfMap: ElfMap,
        steps: Int,
        neighbourFunction: (Position, ElfMap) -> Set<Position>
    ): Set<Position> {

        tailrec fun iterate(currentPositions: Set<Position>, steps: Int): Set<Position> {
            return if (steps <= 0)
                currentPositions
            else {
                val neighbours = currentPositions.flatMap { neighbourFunction(it, elfMap) }.toSet()
                iterate(neighbours, steps - 1)
            }
        }

        return iterate(setOf(elfMap.startingPosition), steps)
    }

    private fun solution1(elfMap: ElfMap): BigInteger {
        val steps = 64
        val positions = breadthFirstSearch(elfMap, steps, ElfMap.Companion::accessibleNeighbours)
        return positions.size.toBigInteger()
    }

    private fun solution2(input: String): BigInteger {
        val elfMap = ElfMap.parse(input)

        val steps = 26501365

        val remainder = steps % elfMap.height

        val x0 = remainder
        val x1 = remainder + elfMap.height
        val x2 = remainder + 2 * elfMap.height

        println("x0: $x0")
        println("x1: $x1")
        println("x2: $x2")

        val y0 = breadthFirstSearch(elfMap, x0, ElfMap::loopedNeighbours).size.toBigDecimal()
        val y1 = breadthFirstSearch(elfMap, x1, ElfMap::loopedNeighbours).size.toBigDecimal()
        val y2 = breadthFirstSearch(elfMap, x2, ElfMap::loopedNeighbours).size.toBigDecimal()

        println("y0: $y0")
        println("y1: $y1")
        println("y2: $y2")

        // Manual interpolation with the assumption that x0 = 0, x1 = 1, and x2 = 2.
        // The assumption seems wrong with the above input, but works correctly, because the multiplicity of the height
        // corresponds to the aforementioned values.
        val a2 = (y2 - BigDecimal(2) * y1 + y0) / BigDecimal(2)
        val a1 = (-y2 + BigDecimal(4) * y1 - BigDecimal(3) * y0) / BigDecimal(2)
        val a0 = y0

        fun polynomial(x: BigDecimal): BigDecimal = a2 * x * x + a1 * x + a0

        val numberOfMapRepetitions = steps / elfMap.height

        return polynomial(numberOfMapRepetitions.toBigDecimal()).toBigInteger()
    }

}