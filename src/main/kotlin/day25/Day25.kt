package day25

import java.math.BigInteger

object Day25 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val graph = Graph.parse(input)
        return solution1(graph) to solution2(graph)
    }

    private fun solution1(graph: Graph): BigInteger {
        return BigInteger.ZERO
    }

    private fun solution2(graph: Graph): BigInteger {
        return BigInteger.ZERO
    }

    data class Graph(
        val adjacency: Map<String, List<String>>
    ) {
        companion object {
            fun parse(input: String): Graph {
                val adjacency = input.lines().flatMap { line ->
                    val (from, tosString) = line.split(": ")
                    tosString.split(" ").flatMap { listOf(it to from, from to it) }
                }
                    .groupBy { it.first }
                    .mapValues {
                        it
                            .value
                            .map { pair -> pair.second }

                    }
                return Graph(adjacency)
            }
        }
    }