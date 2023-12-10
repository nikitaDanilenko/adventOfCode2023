package day10

import java.math.BigInteger

object Day10 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = Tile.parse(input)
        return solution1(parsed) to BigInteger.ZERO
    }

    private fun neighbours(
        position: Tile.Companion.Position,
        tile: Tile
    ): Set<Tile.Companion.Position> = when (tile) {
        Tile.VERTICAL -> setOf(
            Tile.Companion.Position(position.line - 1, position.column),
            Tile.Companion.Position(position.line + 1, position.column)
        )

        Tile.HORIZONTAL -> setOf(
            Tile.Companion.Position(position.line, position.column - 1),
            Tile.Companion.Position(position.line, position.column + 1)
        )

        Tile.NORTH_AND_EAST -> setOf(
            Tile.Companion.Position(position.line - 1, position.column),
            Tile.Companion.Position(position.line, position.column + 1)
        )

        Tile.NORTH_AND_WEST -> setOf(
            Tile.Companion.Position(position.line - 1, position.column),
            Tile.Companion.Position(position.line, position.column - 1)
        )

        Tile.SOUTH_AND_WEST -> setOf(
            Tile.Companion.Position(position.line + 1, position.column),
            Tile.Companion.Position(position.line, position.column - 1)
        )

        Tile.SOUTH_AND_EAST -> setOf(
            Tile.Companion.Position(position.line + 1, position.column),
            Tile.Companion.Position(position.line, position.column + 1)
        )

        Tile.EMPTY -> emptySet()
    }

    data class Iteration(
        val visited: Set<Tile.Companion.Position>,
        val current: Tile.Companion.Position
    )

    private fun step(
        tileMap: Map<Tile.Companion.Position, Tile>,
        iteration: Iteration
    ): Pair<Tile.Companion.Position, Tile>? {
        val unvisitedNeighbours = neighbours(iteration.current, tileMap[iteration.current]!!).minus(iteration.visited)
        return if (unvisitedNeighbours.isEmpty()) null
        else {
            val next = unvisitedNeighbours.first()
            next to tileMap[next]!!
        }
    }

    private fun iterateStep(
        start: Tile.Companion.Position,
        tileMap: Map<Tile.Companion.Position, Tile>
    ): List<Tile.Companion.Position> {

        tailrec fun recur(
            iteration: Iteration,
            path: List<Pair<Tile.Companion.Position, Tile>>
        ): List<Pair<Tile.Companion.Position, Tile>> {
            val next = step(tileMap, iteration)
            return if (next == null) path
            else recur(Iteration(iteration.visited + next.first, next.first), path + next)
        }

        return recur(Iteration(setOf(start), start), emptyList()).map { it.first }

    }

    private fun solution1(tileMap: Tile.Companion.TileMap): BigInteger {
        val steps = iterateStep(tileMap.startPosition, tileMap.tiles)
        return ((steps.size + 1) / 2).toBigInteger()
    }

}