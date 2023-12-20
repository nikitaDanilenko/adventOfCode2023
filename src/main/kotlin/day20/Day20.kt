package day20

import java.math.BigInteger

object Day20 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val machine = Machine.parse(input)
        return solution1(machine) to solution2(machine)
    }

    private fun solution1(machine: Machine): BigInteger = BigInteger.ZERO
    private fun solution2(machine: Machine): BigInteger = BigInteger.ZERO

}