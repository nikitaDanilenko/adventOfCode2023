package day22

import java.math.BigInteger

object Day22 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = input.lines().map(Brick::parse)
        return solution1(parsed) to solution2(parsed)
    }

    private fun solution1(bricks: List<Brick>): BigInteger {
        return BigInteger.ZERO
    }

    private fun solution2(bricks: List<Brick>): BigInteger {
        return BigInteger.ZERO
    }

    data class Position(
        val x: Int,
        val y: Int,
        val z: Int
    ) {
        companion object {
            fun parse(input: String): Position {
                val (x, y, z) = input.split(",").map(String::toInt)
                return Position(x, y, z)
            }
        }
    }

    data class Brick(
        val first: Position,
        val last: Position
    ) {
        companion object {
            fun parse(input: String): Brick {
                val (first, last) = input.split("~").map(Position::parse)
                return Brick(first, last)
            }
        }
    }
}