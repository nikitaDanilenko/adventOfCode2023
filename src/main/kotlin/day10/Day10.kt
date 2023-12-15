package day10

import util.Position
import java.math.BigInteger
import kotlin.math.absoluteValue

object Day10 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = Tile.parse(input)
        return solution1(parsed) to solution2(parsed)
    }

    private fun neighbours(
        position: Position,
        tile: Tile
    ): Set<Position> = when (tile) {
        Tile.VERTICAL -> setOf(
            Position(position.line - 1, position.column),
            Position(position.line + 1, position.column)
        )

        Tile.HORIZONTAL -> setOf(
            Position(position.line, position.column - 1),
            Position(position.line, position.column + 1)
        )

        Tile.NORTH_AND_EAST -> setOf(
            Position(position.line - 1, position.column),
            Position(position.line, position.column + 1)
        )

        Tile.NORTH_AND_WEST -> setOf(
            Position(position.line - 1, position.column),
            Position(position.line, position.column - 1)
        )

        Tile.SOUTH_AND_WEST -> setOf(
            Position(position.line + 1, position.column),
            Position(position.line, position.column - 1)
        )

        Tile.SOUTH_AND_EAST -> setOf(
            Position(position.line + 1, position.column),
            Position(position.line, position.column + 1)
        )

        Tile.EMPTY -> emptySet()
    }

    data class Iteration(
        val visited: Set<Position>,
        val current: Pair<Position, Tile>
    )

    private fun step(
        tileMap: Map<Position, Tile>,
        iteration: Iteration
    ): Pair<Position, Tile>? {
        val unvisitedNeighbours =
            neighbours(iteration.current.first, tileMap[iteration.current.first]!!).minus(iteration.visited)
        return if (unvisitedNeighbours.isEmpty()) null
        else {
            val next = unvisitedNeighbours.first()
            next to tileMap[next]!!
        }
    }

    private fun iterateStep(
        start: Pair<Position, Tile>,
        tileMap: Map<Position, Tile>
    ): List<Position> {

        tailrec fun recur(
            iteration: Iteration,
            path: List<Pair<Position, Tile>>
        ): List<Pair<Position, Tile>> {
            val next = step(tileMap, iteration)
            return if (next == null) path
            else recur(Iteration(iteration.visited + iteration.current.first, next), path + iteration.current)
        }

        return recur(Iteration(emptySet(), start), emptyList()).map { it.first }

    }

    private fun solution1(tileMap: Tile.Companion.TileMap): BigInteger {
        val steps = iterateStep(tileMap.startPosition to tileMap.startTile, tileMap.tiles)
        return ((steps.size + 1) / 2).toBigInteger()
    }

    private fun solution2(tileMap: Tile.Companion.TileMap): BigInteger {
        val steps = iterateStep(tileMap.startPosition to tileMap.startTile, tileMap.tiles).also { println(it) }
        val shifted = steps.drop(1) + steps.first()
        // The area of a simple polygon via the shoelace formula (https://en.wikipedia.org/wiki/Shoelace_formula)
        val twiceShoelaceArea = steps.zip(shifted)
            .sumOf { (pi, pi1) -> (pi.line + pi1.line) * (pi.column - pi1.column) }.absoluteValue
        // The number of inner points is computed via Pick's theorem (https://en.wikipedia.org/wiki/Pick%27s_theorem)
        // TODO: There is an off-by-one error here, because depending on the size, one may get one element too many.
        val inner = (twiceShoelaceArea - steps.size) / 2 + 1
        return inner.toBigInteger()
    }

}