package day23

import util.Backtracking
import util.Direction
import util.Position
import java.math.BigInteger

object Day23 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = Graph.parse(input)
        return solution1(parsed) to solution2(parsed)
    }

    private fun solution1(graph: Graph): BigInteger {
        val edgeGraph = Graph.flattenToJunctions(graph)

        val generated = Backtracking.generate(
            initial = WeightedPosition(Graph.source, 0),
            extend = { position ->
                edgeGraph.adjacency[position.position]?.map {
                    WeightedPosition(
                        it.position,
                        position.weight + it.weight
                    )
                }
                    ?: emptyList()
            }
        )

        val pruned = Backtracking
            .prune(
                generated,
                canBeDismissed = { false },
                isValid = { it.position == Graph.target(graph) }
            )
            .map { it.weight - 1 }
            .maxOf { it }

        return pruned.toBigInteger()
    }

    private fun solution2(graph: Graph): BigInteger = BigInteger.ZERO

    data class CorridorEnd(
        val position: Position,
        val length: Int,
        val oneWay: Boolean
    )

    data class WeightedPosition(
        val position: Position,
        val weight: Int
    )

    data class WeightedArc(
        val from: Position,
        val to: Position,
        val weight: Int
    )

    data class EdgeGraph(
        val adjacency: Map<Position, Set<WeightedPosition>>
    )

    data class Graph(
        val tiles: Map<Position, Tile>,
        val height: Int,
        val width: Int
    ) {
        companion object {
            fun parse(input: String): Graph {
                val lines = input.lines()
                val tiles = lines.flatMapIndexed { lineIndex, line ->
                    line.mapIndexed { columnIndex, c ->
                        Position(line = lineIndex, column = columnIndex) to Tile.parse(c)
                    }
                }.toMap()
                return Graph(
                    tiles,
                    height = lines.size,
                    width = lines.first().length
                )
            }

            val source: Position = Position(line = 0, column = 1)
            fun target(graph: Graph): Position = Position(graph.height - 1, graph.width - 2)

            fun tileOrWall(graph: Graph, position: Position): Tile = graph.tiles[position] ?: Tile.Wall

            fun neighbours(
                graph: Graph,
                position: Position,
                deadEndSlopesAllowed: Boolean
            ): Set<Pair<Direction, Position>> {
                val currentTile = tileOrWall(graph, position)
                return when (currentTile) {
                    is Tile.Slope -> setOf(currentTile.direction to Position.move(position, currentTile.direction))
                    is Tile.Free -> Position.fourNeighbours(position)
                        .filter {
                            val tile = tileOrWall(graph, it.second)
                            tile != Tile.Wall && (deadEndSlopesAllowed || tile != Tile.Slope(Direction.opposite(it.first)))
                        }
                        .toSet()

                    is Tile.Wall -> emptySet()
                }
            }

            fun isJunction(graph: Graph, position: Position): Boolean {
                val atPosition = tileOrWall(graph, position)
                // Junctions can have dead ends in any direction.
                val neighbours = neighbours(graph, position, deadEndSlopesAllowed = true)
                return atPosition is Tile.Free && neighbours.size > 2
            }

            fun nextJunctionOrEnd(graph: Graph, position: Position): CorridorEnd {
                val target = target(graph)

                fun follow(positionInner: Position, visited: Set<Position>, distance: Int): CorridorEnd {
                    if (isJunction(graph, positionInner) || positionInner == target) {
                        return CorridorEnd(positionInner, distance, visited.any { graph.tiles[it] is Tile.Slope })
                    } else {
                        val next = neighbours(graph, positionInner, deadEndSlopesAllowed = false)
                            .first {
                                tileOrWall(
                                    graph,
                                    it.second
                                ) != Tile.Slope(Direction.opposite(it.first)) && !visited.contains(it.second)
                            }
                            .second
                        return follow(next, visited + positionInner, distance + 1)
                    }
                }

                return follow(position, emptySet(), 0)
            }

            fun flattenToJunctions(graph: Graph): EdgeGraph {

                val target = target(graph)

                fun flatten(
                    expanded: Set<Position>,
                    junctions: Set<Position>,
                    edges: Set<WeightedArc>
                ): Set<WeightedArc> {
                    if (junctions.isEmpty())
                        return edges
                    else {
                        val nextArcs = junctions
                            .flatMap { junction ->
                                neighbours(graph, junction, deadEndSlopesAllowed = false).flatMap { (_, position) ->
                                    val next = nextJunctionOrEnd(graph, position)
                                    if (next.oneWay) {
                                        setOf(WeightedArc(junction, next.position, next.length))
                                    } else {
                                        setOf(
                                            WeightedArc(junction, next.position, next.length),
                                            WeightedArc(next.position, junction, next.length)
                                        )
                                    }
                                }
                            }
                            // The filter should not be necessary, but there is an error somewhere, so it is.
                            .filter { it.from != it.to }
                        val nextExpanded = expanded + junctions
                        // If the target has been reached once, it is not a junction, and going back from there
                        // causes strange behaviour. Luckily, going back also does not make sense.
                        val nextJunctions = nextArcs.map { it.to }.toSet() - nextExpanded - target
                        return flatten(
                            nextExpanded,
                            nextJunctions,
                            edges + nextArcs
                        )
                    }
                }

                // The first junction is found manually, because 'flatten' only makes sense for junctions,
                // but the first step is not a junction.
                // N.B.: The solution seems extremely convoluted.
                val firstJunction = nextJunctionOrEnd(graph, source)
                val junctionEdges = flatten(emptySet(), setOf(firstJunction.position), emptySet()) + WeightedArc(
                    source,
                    firstJunction.position,
                    firstJunction.length
                )
                val adjacency = junctionEdges
                    .groupBy { it.from }
                    .mapValues { (_, arcs) ->
                        // One more, because the steps are always counted from the first neighbour, so the first step is missing.
                        arcs.map { WeightedPosition(it.to, 1 + it.weight) }.toSet()
                    }
                return EdgeGraph(adjacency)
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