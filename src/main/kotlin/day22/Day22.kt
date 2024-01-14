package day22

import java.math.BigInteger

object Day22 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = input.lines().map(Brick::parse)
        return solution1(parsed) to solution2(parsed)
    }

    private fun solution1(bricks: List<Brick>): BigInteger {
        val dropped = dropAll(bricks).sortedBy { it.first.z }
        val hasSupport =
            dropped.associateWith { brick -> hasSupportOf(brick, dropped).filter { other -> other != brick } }
        val supported =
            dropped.associateWith { brick -> supportedBy(brick, dropped).filter { other -> other != brick } }
        val canBeDisintegrated = dropped.filter { brick ->
            val supportedByBrick = supported[brick]!!
            val condition = supportedByBrick.all {
                val supportingBricks = hasSupport[it]!!
                supportingBricks.size > 1
            }
            if (condition) println(brick)
            condition
        }
        return canBeDisintegrated.size.toBigInteger()
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

            fun positions(brick: Brick): Set<Position> {
                return if (brick.first.x != brick.last.x) {
                    val min = minOf(brick.first.x, brick.last.x)
                    val max = maxOf(brick.first.x, brick.last.x)
                    (min..max).map { Position(it, brick.first.y, brick.first.z) }.toSet()
                } else if (brick.first.y != brick.last.y) {
                    val min = minOf(brick.first.y, brick.last.y)
                    val max = maxOf(brick.first.y, brick.last.y)
                    (min..max).map { Position(brick.first.x, it, brick.first.z) }.toSet()
                } else {
                    val min = minOf(brick.first.z, brick.last.z)
                    val max = maxOf(brick.first.z, brick.last.z)
                    (min..max).map { Position(brick.first.x, brick.first.y, it) }.toSet()
                }
            }
        }
    }

    data class HeightMap(
        val xyHeights: Map<Pair<Int, Int>, Int>,
    )

    private fun drop(brick: Brick, heightMap: HeightMap): Pair<Brick, HeightMap> {
        val positions = Brick.positions(brick)
        val maxHeight = 1 + (positions.maxOf { heightMap.xyHeights[it.x to it.y] ?: 0 })
        val newBrick = if (brick.first.z == brick.last.z)
            brick.copy(
                first = brick.first.copy(z = maxHeight),
                last = brick.last.copy(z = maxHeight)
            )
        else
            brick.copy(
                first = brick.first.copy(z = maxHeight),
                last = brick.last.copy(z = maxHeight + (brick.last.z - brick.first.z))
            )
        val newHeightMap = Brick.positions(newBrick).fold(heightMap) { acc, position ->
            acc.copy(
                xyHeights = acc.xyHeights + (position.x to position.y to position.z)
            )
        }
        return newBrick to newHeightMap
    }

    private fun dropAll(bricks: List<Brick>): List<Brick> =
        bricks
            .sortedBy { it.first.z }
            .fold(HeightMap(emptyMap()) to emptyList<Brick>()) { acc, brick ->
                val (dropped, map) = drop(brick, acc.first)
                map to (acc.second + dropped)
            }.second

    private fun supportedBy(brick: Brick, bricks: List<Brick>): List<Brick> {
        return bricks.filter { other ->
            Brick.positions(brick).any { position ->
                Brick.positions(other).any { otherPosition ->
                    position.x == otherPosition.x && position.y == otherPosition.y && position.z == otherPosition.z - 1
                }
            }
        }
    }

    private fun hasSupportOf(brick: Brick, bricks: List<Brick>): List<Brick> {
        return bricks.filter { other ->
            Brick.positions(brick).any { position ->
                Brick.positions(other).any { otherPosition ->
                    position.x == otherPosition.x && position.y == otherPosition.y && position.z == otherPosition.z + 1
                }
            }
        }
    }
    
}