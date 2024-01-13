package day24

import java.math.BigDecimal
import java.math.BigInteger

object Day24 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val hails = input.lines().map(Hail.Companion::parse)
        return solution1(hails) to solution2(hails)
    }

    private fun solution1(hails: List<Hail>): BigInteger =
        checkIntersections(
            hails,
            BigDecimal.valueOf(200000000000000),
            BigDecimal.valueOf(400000000000000)
        ).toBigInteger()

    private fun solution2(hails: List<Hail>): BigInteger = BigInteger.ZERO

    data class Position3D(
        val x: BigInteger,
        val y: BigInteger,
        val z: BigInteger
    ) {
        companion object {
            fun parse(input: String): Position3D {
                val (x, y, z) = input.replace(" ", "").split(",")
                return Position3D(x.toBigInteger(), y.toBigInteger(), z.toBigInteger())
            }

        }
    }

    data class Hail(
        val startPosition: Position3D,
        val direction: Position3D
    ) {
        companion object {
            fun parse(input: String): Hail {
                val (position, direction) = input.replace(" ", "").split("@")

                return Hail(Position3D.parse(position), Position3D.parse(direction))
            }
        }
    }

    private fun scalarProduct(
        position1: Position3D,
        position2: Position3D
    ): BigInteger =
        position1.x * position2.x + position1.y * position2.y + position1.z * position2.z

    private fun euclideanNorm2(position: Position3D): BigInteger = scalarProduct(position, position)

    // Cauchy-Schwarz inequality; equality holds iff the vectors are linearly dependent
    private fun linearlyDependent(position1: Position3D, position2: Position3D): Boolean {
        val scalarProduct = scalarProduct(position1, position2)
        return scalarProduct * scalarProduct == euclideanNorm2(position1) * euclideanNorm2(position2)
    }

    sealed interface Solution {
        data class Single(val t1: BigDecimal, val t2: BigDecimal) : Solution
        data object No : Solution
        data object Infinite : Solution
    }

    //
    fun twoVariableSystem(
        a11: BigInteger,
        a12: BigInteger,
        a21: BigInteger,
        a22: BigInteger,
        b1: BigInteger,
        b2: BigInteger
    ): Solution {
        val det = a11 * a22 - a12 * a21
        if (det == BigInteger.ZERO) {
            val scalar = if (a21 != BigDecimal.ZERO) toBigDecimal(a11) / toBigDecimal(a21)
            else if (a22 != BigDecimal.ZERO) toBigDecimal(a12) / toBigDecimal(a22) else null
            return if (scalar != null) {
                if (toBigDecimal(b2) == scalar * toBigDecimal(b1)) {
                    // infinite solutions, we choose a particularly simple one
                    Solution.Infinite
                } else {
                    // no solutions
                    Solution.No
                }
            } else
                Solution.No
        } else {
            val detBD = toBigDecimal(det)
            val x = toBigDecimal(a22 * b1 - a12 * b2) / detBD
            val y = toBigDecimal(a11 * b2 - a21 * b1) / detBD
            return Solution.Single(x, y)
        }
    }

    private fun toBigDecimal(bigInteger: BigInteger): BigDecimal = bigInteger.toBigDecimal().setScale(10)
    private fun intersectionAt(
        hail1: Hail,
        hail2: Hail
    ): Pair<BigDecimal, BigDecimal>? {
        // The equation A*t = b can be derived by viewing the hail movement as h(t) = startPosition + t * direction
        // Note that the intersection can be reached at two distinct times.
        val a11 = hail1.direction.x
        val a12 = -hail2.direction.x
        val a21 = hail1.direction.y
        val a22 = -hail2.direction.y
        val b1 = (hail2.startPosition.x - hail1.startPosition.x)
        val b2 = (hail2.startPosition.y - hail1.startPosition.y)

        val intersection = twoVariableSystem(a11, a12, a21, a22, b1, b2)

        return when (intersection) {
            Solution.No -> null
            Solution.Infinite -> BigDecimal.ONE to BigDecimal.ONE
            is Solution.Single -> intersection.t1 to intersection.t2
        }
    }

    private fun checkIntersections(
        hails: List<Hail>,
        boundaryMin: BigDecimal,
        boundaryMax: BigDecimal
    ): Int =
        hails.mapIndexed { index, hail1 ->
            hails
                .drop(index + 1)
                .mapNotNull { hail2 ->
                    intersectionAt(hail1, hail2)
                }
                .filter { (t1, t2) ->
                    val intersectionAtX = hail1.startPosition.x.toBigDecimal() + hail1.direction.x.toBigDecimal() * t1
                    val intersectionAtY = hail1.startPosition.y.toBigDecimal() + hail1.direction.y.toBigDecimal() * t1
                    t1 >= BigDecimal.ZERO && t2 >= BigDecimal.ZERO && intersectionAtX >= boundaryMin && intersectionAtX <= boundaryMax && intersectionAtY >= boundaryMin && intersectionAtY <= boundaryMax
                }.size
        }.sum()

    private fun pointOfIntersection2(
        hail1: Hail,
        t1: BigDecimal,
    ): Pair<BigDecimal, BigDecimal> {
        val intersectionAtX = hail1.startPosition.x.toBigDecimal() + hail1.direction.x.toBigDecimal() * t1
        val intersectionAtY = hail1.startPosition.y.toBigDecimal() + hail1.direction.y.toBigDecimal() * t1
        return intersectionAtX to intersectionAtY
    }

    private fun areLinearlyIndependent(
        v1: Position3D,
        v2: Position3D,
        v3: Position3D
    ): Boolean {
        // 3 vectors are linearly independent iff the determinant of the matrix formed by the vectors is non-zero.
        // The determinant of a 3x3 matrix is computed by the Sarrus rule.
        val diagonal1 = v1.x * v2.y * v3.z
        val diagonal2 = v1.y * v2.z * v3.x
        val diagonal3 = v1.z * v2.x * v3.y
        val negativeDiagonal1 = v1.z * v2.y * v3.x
        val negativeDiagonal2 = v1.x * v2.z * v3.y
        val negativeDiagonal3 = v1.y * v2.x * v3.z
        val determinant = diagonal1 + diagonal2 + diagonal3 - negativeDiagonal1 - negativeDiagonal2 - negativeDiagonal3
        return determinant != BigInteger.ZERO
    }

    // Find the first three linearly independent direction vectors.
    private fun findLinearlyIndependent(hails: List<Hail>): Triple<Hail, Hail, Hail> {
        val hailSequence = hails.asSequence()
        return hailSequence
            .flatMapIndexed { index1, hail1 ->
                hailSequence
                    .drop(index1 + 1)
                    .flatMapIndexed { index2, hail2 ->
                        hailSequence
                            .drop(index2 + 1)
                            .mapNotNull { hail3 ->
                                if (areLinearlyIndependent(hail1.direction, hail2.direction, hail3.direction)) {
                                    Triple(hail1, hail2, hail3)
                                } else null
                            }
                    }
            }
            .first()
    }
    
}