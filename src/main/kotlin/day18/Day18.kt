package day18

import util.Direction
import util.Position
import java.math.BigInteger

object Day18 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val instructions = parseInput(input)
        return solution1(instructions) to solution2(instructions)
    }

    private fun solution1(instructions: List<Instruction>): BigInteger {
        val visited = followInstructions(instructions)
        val twiceArea = Position.twiceShoelaceArea(visited)
        val all = (twiceArea + ((visited.size).toBigInteger())) / BigInteger.valueOf(2) + BigInteger.ONE
        return all
    }


    private fun solution2(instructions: List<Instruction>): BigInteger =
        BigInteger.ZERO

    data class Instruction(
        val direction: Direction,
        val steps: Int,
        val color: String
    )

    private fun followInstructions(instructions: List<Instruction>): List<Position> {
        val startPosition = Position(0, 0)
        val visited =
            instructions.fold(startPosition to emptyList<Position>()) { (currentPosition, positions), instruction ->
                val (nextPosition, visited) =
                    (0..<instruction.steps).fold(currentPosition to emptyList<Position>()) { (stepPosition, stepped), _ ->
                        val nextPosition = Position.move(
                            position = stepPosition,
                            direction = instruction.direction
                        )
                        nextPosition to (stepped.plusElement(nextPosition))
                    }
                nextPosition to (positions.plus(visited))
            }

        return listOf(startPosition) + visited.second.distinct()
    }

    // TODO: Remove?
    private fun followInstructions2(instructions: List<Instruction>): List<Position> {
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
        return corners.second
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
                        color = parts[2].drop(1).dropLast(1)
                    )
                }
            }
        }

        return result
    }

    private fun parseInput(input: String): List<Instruction> =
        input.lines().mapNotNull(::parseInstruction)
    
}