package day15

import java.math.BigInteger

object Day15 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = parseInput(input)
        val parsedInstructions = parseInstructions(input)
        return solution1(parsed) to solution2(parsedInstructions)
    }

    private fun parseInput(input: String): List<String> =
        input.split(",")

    private fun solution1(input: List<String>): BigInteger =
        input.sumOf { hash(it).toBigInteger() }

    private fun solution2(instructions: List<Instruction>): BigInteger =
        followInstructions(instructions)

    private fun hash(input: String): Int =
        input.fold(0) { acc, c -> (acc + c.code) * 17 % 256 }

    sealed interface Instruction

    data class Remove(val label: String) : Instruction
    data class Assign(val label: String, val value: Int) : Instruction

    private fun parseInstruction(input: String): Instruction =
        if (input.endsWith("-")) {
            Remove(input.dropLast(1))
        } else {
            val (label, value) = input.split("=")
            Assign(label, value.toInt())
        }

    private fun parseInstructions(input: String): List<Instruction> =
        input.split(",").map(::parseInstruction)

    private fun followInstructions(
        instructions: List<Instruction>
    ): BigInteger {
        val boxes = mutableMapOf<Int, List<Pair<String, Int>>>()
        instructions.forEach { instruction ->
            when (instruction) {
                is Remove -> {
                    val hash = hash(instruction.label)
                    boxes[hash] = boxes[hash]?.filter { it.first != instruction.label } ?: emptyList()
                }

                is Assign -> {
                    val hash = hash(instruction.label)
                    boxes[hash] = boxes[hash]?.let { lenses ->
                        //TODO: Not very efficient.
                        if (lenses.any { it.first == instruction.label })
                            lenses.map { if (it.first == instruction.label) it.copy(second = instruction.value) else it }
                        else lenses + (instruction.label to instruction.value)
                    } ?: listOf(instruction.label to instruction.value)
                }
            }
        }
        val result = boxes.toList().flatMap { (box, lenses) ->
            lenses.mapIndexed { index, (_, value) ->
                (value * (index + 1) * (box + 1)).toBigInteger()
            }
        }.sumOf { it }

        return result
    }

}