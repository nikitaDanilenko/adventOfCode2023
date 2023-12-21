package day20

import java.math.BigInteger

object Day20 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val machine = Machine.parse(input)
        return solution1(machine) to solution2(machine)
    }

    fun <A> iterate(start: A, steps: Int, function: (A) -> A): A {
        fun recur(steps: Int, a: A): A = if (steps <= 0) a else recur(steps - 1, function(a))

        return recur(steps, start)
    }

    private fun solution1(machine: Machine): BigInteger {
        val steps = 1000
        val pair = iterate(machine to Module.Companion.Count(BigInteger.ZERO, BigInteger.ZERO), steps) { (m, c) ->
            Module.iterateProcess(m, c)
        }
        val result = pair.second.copy(low = pair.second.low)
        return result.high * result.low
    }

    private fun solution2(machine: Machine): BigInteger = BigInteger.ZERO

}