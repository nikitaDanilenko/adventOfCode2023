package util

import java.math.BigInteger
import kotlin.math.absoluteValue

data class Position(
    val line: Int, val column: Int
) {

    companion object {
        fun move(position: Position, direction: Direction, steps: Int = 1): Position = when (direction) {
            Direction.UP -> position.copy(line = position.line - steps)
            Direction.DOWN -> position.copy(line = position.line + steps)
            Direction.LEFT -> position.copy(column = position.column - steps)
            Direction.RIGHT -> position.copy(column = position.column + steps)
        }

        fun fourNeighbours(position: Position): List<Pair<Direction, Position>> = listOf(
            Direction.UP to move(position, Direction.UP),
            Direction.DOWN to move(position, Direction.DOWN),
            Direction.LEFT to move(position, Direction.LEFT),
            Direction.RIGHT to move(position, Direction.RIGHT)
        )

        fun prettyBlock(
            positions: Collection<Position>,
            height: Int,
            width: Int
        ): String = (0.rangeTo(height)).joinToString("\n") { line ->
            (0.rangeTo(width)).joinToString("") { column ->
                val position = Position(line, column)
                if (positions.contains(position)) "#"
                else "."
            }
        }

        // The area of a simple polygon via the shoelace formula (https://en.wikipedia.org/wiki/Shoelace_formula)
        // The factor 2 guarantees that the result is an integer
        fun twiceShoelaceArea(positions: List<Position>): BigInteger {
            val shifted = positions.drop(1) + positions.first()
            val twiceShoelaceArea = positions.zip(shifted)
                .sumOf { (pi, pi1) ->
                    (pi.line.toBigInteger() + pi1.line.toBigInteger()) * (pi.column.toBigInteger() - pi1.column.toBigInteger())
                }
                .abs()

            return twiceShoelaceArea
        }
    }
}
