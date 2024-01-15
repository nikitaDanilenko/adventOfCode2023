package day23

import util.Direction
import util.Position
import java.math.BigInteger

object Day23 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = Graph.parse(input)
        return solution1(parsed) to solution2(parsed)
    }

    private fun solution1(graph: Graph): BigInteger = BigInteger.ZERO

    private fun solution2(graph: Graph): BigInteger = BigInteger.ZERO

    data class Graph(val tiles: Map<Position, Tile>) {
        companion object {
            fun parse(input: String): Graph {
                val tiles = input.lines().flatMapIndexed { lineIndex, line ->
                    line.mapIndexed { columnIndex, c ->
                        Position(line = lineIndex, column = columnIndex) to Tile.parse(c)
                    }
                }.toMap()
                return Graph(tiles)
            }
        }
    }

    sealed interface Tile {
        data object Free : Tile
        data object Wall : Tile
        data class Slope(val direction: Direction) : Tile

        companion object {
            fun parse(input: Char): Tile = when (input) {
                '.' -> Free
                '^' -> Slope(Direction.UP)
                'v' -> Slope(Direction.DOWN)
                '>' -> Slope(Direction.RIGHT)
                '<' -> Slope(Direction.LEFT)
                else -> Wall
            }
        }
    }


}