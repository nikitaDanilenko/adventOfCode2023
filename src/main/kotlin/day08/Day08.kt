package day08

import java.math.BigInteger

object Day08 {

    data class Position(
        val name: String
    )

    enum class Direction {
        L, R;
    }

    data class Node(
        val left: Position,
        val right: Position
    )

    fun parseNode(input: String): Pair<Position, Node> {
        val parts = input.filter { it.isLetter() || it == ' ' }.split(" ")
        val position = Position(parts[0])
        val left = Position(parts[2])
        val right = Position(parts[3])
        return position to Node(left, right)
    }

    data class Network(
        val nodes: Map<Position, Node>,
        val directions: List<Direction>
    )

    fun parseInput(input: String): Network {
        val lines = input.lines()
        val directions = lines[0].map { Direction.valueOf(it.toString()) }
        val nodes = lines.drop(2).associate(::parseNode)
        return Network(nodes, directions)
    }

    private fun step(
        nodes: Map<Position, Node>,
        position: Position,
        directions: List<Direction>
    ): Pair<Position, List<Direction>> {
        val node = nodes[position]!!
        val direction = directions.first()
        val nextPosition = when (direction) {
            Direction.L -> node.left
            Direction.R -> node.right
        }
        return nextPosition to directions.drop(1) + direction
    }


    private fun iterate(
        nodes: Map<Position, Node>,
        position: Position,
        finalPosition: Position,
        directions: List<Direction>
    ): Int {

        tailrec fun recur(position: Position, steps: Int, directions: List<Direction>): Int {
            return if (position == finalPosition) {
                steps
            } else {
                val (nextPosition, nextDirections) = step(nodes, position, directions)
                recur(nextPosition, steps + 1, nextDirections)
            }
        }

        return recur(position, 0, directions)
    }

    private fun solution1(
        network: Network
    ): Int =
        iterate(
            nodes = network.nodes,
            position = Position("AAA"),
            finalPosition = Position("ZZZ"),
            directions = network.directions
        )


    fun part1(input: String): BigInteger =
        solution1(parseInput(input)).toBigInteger()

    fun part2(input: String): BigInteger =
        BigInteger.ZERO

}