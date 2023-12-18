package day17

import util.Direction
import util.Position
import java.math.BigInteger

object Day17 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = parseInput(input)
        return solution1(parsed) to solution2(parsed)
    }

    private fun solution1(weightedGraph: WeightedGraph): BigInteger =
        solutionWith(weightedGraph, ::neighboursOf, 1)

    private fun solution2(weightedGraph: WeightedGraph): BigInteger =
        solutionWith(weightedGraph, ::neighboursOf2, 4)

    private fun solutionWith(
        weightedGraph: WeightedGraph,
        neighboursOf: (WeightedGraph, ReachedPosition) -> List<ReachedPosition>,
        minNumberOfStepsBeforeEnd: Int
    ): BigInteger {
        val sourcePosition = Position(0, 0)
        val targetPosition = Position(weightedGraph.height - 1, weightedGraph.width - 1)
        val distance =
            dijkstraMutable(weightedGraph, sourcePosition, targetPosition, neighboursOf, minNumberOfStepsBeforeEnd)
        return (distance as Tropical.Value).weight.also { println(it) }
    }

    private fun parseInput(input: String): WeightedGraph {
        val lines = input.lines()
        val map = lines
            .mapIndexed { lineIndex, line ->
                line.mapIndexed { columnIndex, char ->
                    Position(
                        line = lineIndex,
                        column = columnIndex
                    ) to char.toString().toBigInteger()
                }
            }
            .flatten()
            .toMap()

        return WeightedGraph(
            nodes = map,
            height = lines.size,
            width = lines.first().length
        )
    }

    data class WeightedGraph(
        val nodes: Map<Position, BigInteger>,
        val height: Int,
        val width: Int
    )

    private fun leftOf(direction: Direction): Direction =
        when (direction) {
            Direction.UP -> Direction.LEFT
            Direction.DOWN -> Direction.RIGHT
            Direction.LEFT -> Direction.DOWN
            Direction.RIGHT -> Direction.UP
        }

    private fun rightOf(direction: Direction): Direction =
        when (direction) {
            Direction.UP -> Direction.RIGHT
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
            Direction.RIGHT -> Direction.DOWN
        }

    private fun insideBoundary(height: Int, width: Int, position: Position): Boolean =
        position.line in 0..<height && position.column in 0..<width

    private fun neighboursOf(
        weightedGraph: WeightedGraph,
        reachedPosition: ReachedPosition,
        maxNumberOfSameDirection: Int = 3
    ): List<ReachedPosition> =
        listOfNotNull(
            reachedPosition.direction,
            leftOf(reachedPosition.direction),
            rightOf(reachedPosition.direction),
            if (reachedPosition.stepsInDirection == 0) leftOf(leftOf(reachedPosition.direction)) else null,
        ).map { direction ->
            ReachedPosition(
                position = Position.move(reachedPosition.position, direction),
                direction = direction,
                stepsInDirection = 1 + if (direction == reachedPosition.direction) reachedPosition.stepsInDirection else 0
            )
        }.filter {
            it.stepsInDirection <= maxNumberOfSameDirection &&
                    insideBoundary(weightedGraph.height, weightedGraph.width, it.position)
        }

    private fun neighboursOf2(
        weightedGraph: WeightedGraph,
        reachedPosition: ReachedPosition
    ): List<ReachedPosition> {
        val directions = if (reachedPosition.stepsInDirection == 0) {
            // In the beginning, all directions are possible (boundary check comes later).
            Direction.entries
        } else if (reachedPosition.stepsInDirection < 4) {
            // We need to move at least four steps in the same direction to be able to turn around.
            listOf(reachedPosition.direction)
        } else {
            listOfNotNull(
                if (reachedPosition.stepsInDirection < 10) reachedPosition.direction else null,
                leftOf(reachedPosition.direction),
                rightOf(reachedPosition.direction)
            )
        }

        val neighbours = directions.map { direction ->
            ReachedPosition(
                position = Position.move(reachedPosition.position, direction),
                direction = direction,
                stepsInDirection = 1 + if (direction == reachedPosition.direction) reachedPosition.stepsInDirection else 0
            )
        }.filter {
            insideBoundary(weightedGraph.height, weightedGraph.width, it.position)
        }

        return neighbours
    }

    sealed interface Tropical {
        data class Value(val weight: BigInteger) : Tropical
        data object Infinity : Tropical

        companion object {

            fun min(tropical1: Tropical, tropical2: Tropical): Tropical =
                when {
                    tropical1 is Value && tropical2 is Value -> if (tropical1.weight < tropical2.weight) tropical1 else tropical2
                    tropical1 is Value -> tropical1
                    else -> tropical2
                }

            fun minList(tropicals: List<Tropical>): Tropical =
                tropicals.fold(Infinity as Tropical) { acc, tropical ->
                    min(acc, tropical)
                }

            fun less(tropical1: Tropical, tropical2: Tropical): Boolean =
                when {
                    tropical1 is Value && tropical2 is Value -> tropical1.weight < tropical2.weight
                    tropical1 is Value && tropical2 is Infinity -> true
                    else -> false
                }

            fun lessOrEqual(tropical1: Tropical, tropical2: Tropical): Boolean =
                when {
                    tropical1 is Value && tropical2 is Value -> tropical1.weight <= tropical2.weight
                    tropical2 is Infinity -> true
                    else -> false
                }

            fun plus(tropical1: Tropical, tropical2: Tropical): Tropical =
                when {
                    tropical1 is Value && tropical2 is Value -> Value(tropical1.weight + tropical2.weight)
                    else -> Infinity
                }
        }
    }

    data class ReachedPosition(
        val position: Position,
        val direction: Direction,
        val stepsInDirection: Int
    )

    // This is the functional variant, but it is unreasonably slow for the actual input.
    // However, it is likely that it could be redesigned somewhat.
    private fun dijkstra(weightedGraph: WeightedGraph, source: Position): Map<ReachedPosition, Tropical> {
        // The direction is irrelevant, because the number of steps is zero.
        val start = ReachedPosition(source, Direction.UP, 0)
        val distances = mapOf(start to Tropical.Value(BigInteger.ZERO))

        val previousDirections = mutableMapOf<Position, List<Direction>>()
        // TODO: Better with a priority queue than can have tropical priorities.
        val searchQueue = mapOf(start to Tropical.Value(BigInteger.ZERO))

        tailrec fun recur(
            searchQueue: Map<ReachedPosition, Tropical>,
            distances: Map<ReachedPosition, Tropical>
        ): Map<ReachedPosition, Tropical> {
            if (searchQueue.isEmpty())
                return distances
            else {
                val (u, distanceToU) = searchQueue.map { it }
                    .reduce { acc, pair ->
                        if (Tropical.lessOrEqual(pair.value, acc.value))
                            pair
                        else
                            acc
                    }.let { it.key to it.value }
                val remainingQueue = searchQueue - u
                val pathToNext = previousDirections[u.position] ?: emptyList()
                val neighbours = neighboursOf(weightedGraph, u)

                val (newDistances, newSearchQueue) = neighbours.fold(distances to remainingQueue) { (distancesAcc, remainingQueueAcc), arc ->
                    val distanceUV = Tropical.Value(weightedGraph.nodes[arc.position]!!)
                    val distanceThroughUToArcTarget = Tropical.plus(distanceToU, distanceUV)

                    val currentDistanceToArcTarget = distancesAcc.getOrDefault(arc, Tropical.Infinity)

                    val (newDistances, newSearchQueue) = if (Tropical.less(
                            distanceThroughUToArcTarget,
                            currentDistanceToArcTarget
                        )
                    ) {
                        previousDirections[arc.position] = pathToNext.plusElement(arc.direction)
                        // 'plus' works as an upsert for maps.
                        distancesAcc.plus(arc to distanceThroughUToArcTarget) to remainingQueueAcc.plus(arc to distanceThroughUToArcTarget)
                    } else distancesAcc to remainingQueueAcc

                    newDistances to newSearchQueue
                }

                return recur(newSearchQueue, newDistances)
            }
        }

        val result = recur(searchQueue, distances)

        val targetPosition = Position(line = weightedGraph.height - 1, column = weightedGraph.width - 1)
        val targetPath = previousDirections[targetPosition]
        println(targetPath)

        return result

    }


    // Essentially, the Dijkstra pseudocode from Wikipedia.
    private fun dijkstraMutable(
        weightedGraph: WeightedGraph,
        source: Position,
        target: Position,
        neighboursOf: (WeightedGraph, ReachedPosition) -> List<ReachedPosition>,
        minNumberOfStepsBeforeEnd: Int
    ): Tropical {
        // The direction is irrelevant, because the number of steps is zero.
        val start = ReachedPosition(source, Direction.RIGHT, 0)
        val distances = mutableMapOf(start to Tropical.Value(BigInteger.ZERO) as Tropical)

        val previousDirections = mutableMapOf<ReachedPosition, List<Direction>>()
        // TODO: Better with a priority queue than can have tropical priorities.
        val searchQueue = mutableMapOf(start to Tropical.Value(BigInteger.ZERO) as Tropical)

        while (searchQueue.isNotEmpty()) {
            val (u, distanceToU) = searchQueue.map { it }
                .reduce { acc, pair ->
                    if (Tropical.lessOrEqual(pair.value, acc.value))
                        pair
                    else
                        acc
                }.let { it.key to it.value }
            searchQueue.remove(u)
            val neighbours = neighboursOf(weightedGraph, u)

            neighbours.forEach { arc ->
                val distanceUV = Tropical.Value(weightedGraph.nodes[arc.position]!!)
                val distanceThroughUToArcTarget = Tropical.plus(distanceToU, distanceUV)
                val currentDistanceToArcTarget = distances.getOrDefault(arc, Tropical.Infinity)

                if (Tropical.less(
                        distanceThroughUToArcTarget,
                        currentDistanceToArcTarget
                    )
                ) {
                    previousDirections[arc] = (previousDirections[u] ?: emptyList()).plusElement(arc.direction)
                    distances[arc] = distanceThroughUToArcTarget
                    searchQueue[arc] = distanceThroughUToArcTarget
                }
            }
        }

        val targetPaths = previousDirections.filter { it.key.position == target }

        // Horrible hack (necessary for part 2):
        // Before the crucible stops, it needs to have made at least the correct minimum number of steps in the current direction.
        // This condition cannot be accounted for by the neighbours alone.
        // All computed paths are used to "verify" the minimum value by following the paths, and checking whether their directions match.
        val min = targetPaths.values
            .filter { allEqual(it.takeLast(minNumberOfStepsBeforeEnd)) }
            .minOfOrNull { followDirections(start.position, it, weightedGraph) }
            ?.let { Tropical.Value(it) }
            ?: Tropical.Infinity

        return min
    }

    private fun <A> allEqual(list: List<A>): Boolean =
        list.isEmpty() || list.all { it == list.first() }

    private fun followDirections(
        start: Position,
        directions: List<Direction>,
        weightedGraph: WeightedGraph
    ): BigInteger {
        val end = directions.fold(start to BigInteger.ZERO) { (position, weight), direction ->
            val newPosition = Position.move(position, direction)
            val newWeight = weight + weightedGraph.nodes[newPosition]!!
            newPosition to newWeight
        }
        return end.second
    }

}
