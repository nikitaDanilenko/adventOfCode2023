package day18

import util.Direction
import util.Position
import java.math.BigInteger

object Day18 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val instructions = parseInput(input)
        val instructionsHex = input.lines().mapNotNull(::parseHexString)
        return solution1(instructions) to solution1(instructionsHex)
    }

    private fun solution1(instructions: List<Instruction>): BigInteger {
        val boundary = followInstructions(instructions)
        val twiceArea = Position.twiceShoelaceArea(boundary.corners)
        val all = (twiceArea + boundary.length) / BigInteger.valueOf(2) + BigInteger.ONE
        return all
    }

    data class Instruction(
        val direction: Direction,
        val steps: Int,
    )

    data class Boundary(
        val corners: List<Position>,
        val length: BigInteger,
    )

    private fun followInstructions(instructions: List<Instruction>): Boundary {
        val startPosition = Position(0, 0)
        val corners =
            instructions.fold(startPosition to emptyList<Position>()) { (currentPosition, positions), instruction ->
                val nextPosition = Position.move(
                    position = currentPosition,
                    direction = instruction.direction,
                    steps = instruction.steps
                )
                nextPosition to (positions.plus(nextPosition))
            }
        val length = instructions.map { it.steps.toBigInteger() }.sumOf { it }

        return Boundary(corners.second, length)
    }

    private fun parseHexString(input: String): Instruction? {
        val relevant = input.dropWhile { it != '(' }.drop(2).dropLast(1)
        val number = relevant.take(5).toInt(16)
        val direction = parseNumericDirection(relevant.last())
        return direction?.let { Instruction(it, number) }
    }

    private fun parseNumericDirection(input: Char): Direction? =
        when (input) {
            '0' -> Direction.RIGHT
            '1' -> Direction.DOWN
            '2' -> Direction.LEFT
            '3' -> Direction.UP
            else -> null
        }

    private fun parseDirection(input: String): Direction? =
        when (input) {
            "U" -> Direction.UP
            "D" -> Direction.DOWN
            "L" -> Direction.LEFT
            "R" -> Direction.RIGHT
            else -> null
        }

    private fun parseInstruction(input: String): Instruction? {
        val parts = input.split(" ")
        val result = if (parts.size != 3)
            null
        else {
            val direction = parseDirection(parts[0])
            val steps = parts[1].toIntOrNull()
            direction?.let { d ->
                steps?.let { s ->
                    Instruction(
                        direction = d,
                        steps = s,
                    )
                }
            }
        }

        return result
    }

    private fun parseInput(input: String): List<Instruction> =
        input.lines().mapNotNull(::parseInstruction)

}