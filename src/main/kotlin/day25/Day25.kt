package day25

import java.math.BigInteger

object Day25 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val graph = Graph.parse(input)
        return solution1(graph) to solution2(graph)
    }

    private fun solution1(graph: Graph): BigInteger {
        return heuristicMinCut(graph).toBigInteger()
    }

    // There is no part 2.
    private fun solution2(graph: Graph): BigInteger {
        return BigInteger.ZERO
    }

    data class Graph(
        val adjacency: Map<String, Set<String>>
    ) {
        companion object {
            fun parse(input: String): Graph {
                val adjacency = input
                    .lines()
                    .flatMap { line ->
                        val (from, tosString) = line.split(": ")
                        tosString
                            .split(" ")
                            .flatMap { listOf(it to from, from to it) }
                    }
                    .groupBy { it.first }
                    .mapValues {
                        it
                            .value
                            .map { pair -> pair.second }
                            .toSet()
                    }
                return Graph(adjacency)
            }
        }
    }

    // This is a heuristic presented on Reddit.
    // The assumption is that the graph is sufficiently well-formed.
    private fun heuristicMinCut(graph: Graph): Int {
        val nodes = graph.adjacency.keys

        fun outgoingEdges(node: String, partition: Set<String>): Int =
            graph
                .adjacency[node]!!
                .minus(partition)
                .size

        tailrec fun iterate(partition: Set<String>): Int {
            val allOutgoing = partition.sumOf { outgoingEdges(it, partition) }
            if (allOutgoing == 3)
                return partition.size * nodes.minus(partition).size
            else {
                val maxOutgoing = partition.maxBy { outgoingEdges(it, partition) }
                return iterate(partition - maxOutgoing)
            }
        }

        return iterate(nodes)
    }
}