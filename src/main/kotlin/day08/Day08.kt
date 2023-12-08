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

    private fun parseNode(input: String): Pair<Position, Node> {
        val parts = input
            .filter { it.isLetter() || it.isDigit() || it == ' ' }
            .split(" ")
            .filter { it.isNotBlank() }
        val position = Position(parts[0])
        val left = Position(parts[1])
        val right = Position(parts[2])
        return position to Node(left, right)
    }

    data class Network(
        val nodes: Map<Position, Node>,
        val directions: List<Direction>
    )

    private fun parseInput(input: String): Network {
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
        isFinal: (Position) -> Boolean,
        directions: List<Direction>
    ): Int {

        tailrec fun recur(position: Position, steps: Int, directions: List<Direction>): Int {
            return if (isFinal(position)) {
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
            isFinal = { it == Position("ZZZ") },
            directions = network.directions
        )


    fun part1(input: String): BigInteger =
        solution1(parseInput(input)).toBigInteger()


    private fun gcd(x: BigInteger, y: BigInteger): BigInteger =
        if (y == BigInteger.ZERO) x else gcd(y, x % y)

    private fun lcm(x: BigInteger, y: BigInteger): BigInteger =
        x * y / gcd(x, y)

    private fun lcmAll(xs: List<BigInteger>): BigInteger =
        xs.reduce { acc, x -> lcm(acc, x) }

    private fun solution2(
        network: Network
    ): BigInteger {
        val startingPositions = network.nodes.keys.filter { it.name.endsWith("A") }
        val perPosition = startingPositions.map { position ->
            iterate(
                nodes = network.nodes,
                position = position,
                isFinal = { it.name.endsWith("Z") },
                directions = network.directions
            ).toBigInteger()
        }

        val lcm = lcmAll(perPosition)
        return lcm
    }

    fun part2(input: String): BigInteger =
        solution2(parseInput(input))

}