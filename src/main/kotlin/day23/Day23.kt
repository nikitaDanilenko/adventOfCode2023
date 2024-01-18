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

    // This is not a proper solution, but requires guesswork, and manual inspection.
    // The idea is that the maximum will be found much earlier than all paths can be inspected.
    // The main issue is that the generated rose tree is not dynamic, meaning that there should be edges
    // between children of the same parent.
    // I am very annoyed by this non-solution.
    private fun solution2(graph: Graph): BigInteger {
        val nonSloped = graph.copy(
            tiles = graph.tiles.mapValues { (_, tile) ->
                when (tile) {
                    is Tile.Slope -> Tile.Free
                    else -> tile
                }
            }
        )
        val edgeGraph = Graph.flattenToJunctions(nonSloped)

        val generated = Backtracking.generate(
            initial = WeightedPosition(Graph.source, 0) to emptySet<Position>(),
            extend = { (position, visited) ->
                val nextPositions =
                    edgeGraph.adjacency[position.position]?.filter { wp -> !visited.contains(wp.position) }?.map {
                        WeightedPosition(
                            it.position,
                            position.weight + it.weight
                        )
                    }
                        ?: emptyList()
                val nextVisited = visited + position.position
                nextPositions.map { it to nextVisited }
            }
        )

        var currentMax = 0
        val pruned = Backtracking
            .prune(
                generated,
                canBeDismissed = { false },
                isValid = {
                    val valid = it.first.position == Graph.target(graph)
                    if (valid) {
                        val newMax = maxOf(currentMax, it.first.weight - 1)
                        if (newMax != currentMax)
                            println(newMax)
                        currentMax = newMax
                    }
                    valid
                }
            )
            .maxOf { it.first.weight - 1 }

        return pruned.toBigInteger()
    }

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

            fun nextJunctionOrEnd(graph: Graph, position: Position, direction: Direction): CorridorEnd? {
                val target = target(graph)

                fun follow(
                    positionInner: Position,
                    seenSlope: Boolean,
                    direction: Direction,
                    distance: Int
                ): CorridorEnd? {
                    if (isJunction(graph, positionInner) || positionInner == target) {
                        return CorridorEnd(positionInner, distance, seenSlope)
                    } else {
                        val nextInDirection = neighbours(
                            graph,
                            positionInner,
                            deadEndSlopesAllowed = false
                        )
                            .firstOrNull { it.first != Direction.opposite(direction) }

                        val isSlope = tileOrWall(graph, position) is Tile.Slope
                        return nextInDirection?.let { follow(it.second, seenSlope || isSlope, it.first, distance + 1) }
                    }
                }

                return follow(position, false, direction, 0)
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
                                neighbours(
                                    graph,
                                    junction,
                                    deadEndSlopesAllowed = false
                                ).flatMap { (direction, position) ->
                                    val next = nextJunctionOrEnd(graph, position, direction)
                                    next?.let {
                                        if (it.oneWay) {
                                            setOf(WeightedArc(junction, it.position, it.length))
                                        } else {
                                            setOf(
                                                WeightedArc(junction, it.position, it.length),
                                                WeightedArc(it.position, junction, it.length)
                                            )
                                        }
                                    } ?: emptySet()
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
                val firstJunction = nextJunctionOrEnd(graph, source, Direction.DOWN)!!
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