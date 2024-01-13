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

    /* A naive solution would be to assume that the stone is at p with direction w,
       and then take three (suitable) hailstones and solve the system of equations
          p + t1 * 1 = v1 + t1 * d1
          p + t2 * 1 = v2 + t2 * d2
          p + t3 * 1 = v3 + t3 * d3
       However, there are multiple difficulties here:
       1. There are nine unknowns (p, w, and t, each with three components), giving quite the large system.
       2. We do not need w.
       3. We do not need t.

       Particularly the last two points indicate that only 3 equations should be enough,
       which provides a system that can be solved with closed formulas.

       The actual observation what is necessary is taken from this Reddit solution:
       https://www.reddit.com/r/adventofcode/comments/18pum3b/comment/kge0mw5/?utm_source=share&utm_medium=web2x&context=3
     */
    private fun solution2(hails: List<Hail>): BigInteger {
        val (hail1, hail2, hail3) = findLinearlyIndependent(hails)
        val row1 = Position3D.crossProduct(
            Position3D.minus(hail1.direction, hail2.direction),
            Position3D.minus(hail1.startPosition, hail2.startPosition)
        )
        val row2 = Position3D.crossProduct(
            Position3D.minus(hail1.direction, hail3.direction),
            Position3D.minus(hail1.startPosition, hail3.startPosition)
        )
        val row3 = Position3D.crossProduct(
            Position3D.minus(hail2.direction, hail3.direction),
            Position3D.minus(hail2.startPosition, hail3.startPosition)
        )
        val inverse = inverse3x3(row1, row2, row3).also { println("inverse: $it") }
        val vector = Position3D(
            Position3D.scalarProduct(
                Position3D.minus(hail1.direction, hail2.direction),
                Position3D.crossProduct(hail1.startPosition, hail2.startPosition)
            ),
            Position3D.scalarProduct(
                Position3D.minus(hail1.direction, hail3.direction),
                Position3D.crossProduct(hail1.startPosition, hail3.startPosition)
            ),
            Position3D.scalarProduct(
                Position3D.minus(hail2.direction, hail3.direction),
                Position3D.crossProduct(hail2.startPosition, hail3.startPosition)
            )
        )
        val determinant = toBigDecimal(determinant3x3(row1, row2, row3))
        val solution = matrixTimesVector(inverse, vector).also { println(it) }
        return (toBigDecimal(solution.x + solution.y + solution.z) / determinant).toBigInteger()
    }

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

            fun scalarProduct(
                position1: Position3D,
                position2: Position3D
            ): BigInteger =
                position1.x * position2.x + position1.y * position2.y + position1.z * position2.z

            fun crossProduct(
                position1: Position3D,
                position2: Position3D
            ): Position3D =
                Position3D(
                    position1.y * position2.z - position1.z * position2.y,
                    position1.z * position2.x - position1.x * position2.z,
                    position1.x * position2.y - position1.y * position2.x
                )

            fun minus(
                position1: Position3D,
                position2: Position3D
            ): Position3D =
                Position3D(
                    position1.x - position2.x,
                    position1.y - position2.y,
                    position1.z - position2.z
                )

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

    sealed interface Solution {
        data class Single(val t1: BigDecimal, val t2: BigDecimal) : Solution
        data object No : Solution
        data object Infinite : Solution
    }

    // Solution for A*x = b, where A is a 2x2 matrix.
    private fun twoVariableSystem(
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

    private fun determinant3x3(
        row1: Position3D,
        row2: Position3D,
        row3: Position3D
    ): BigInteger {
        // The determinant of a 3x3 matrix is computed by the Sarrus rule.
        val diagonal1 = row1.x * row2.y * row3.z
        val diagonal2 = row1.y * row2.z * row3.x
        val diagonal3 = row1.z * row2.x * row3.y
        val negativeDiagonal1 = row1.z * row2.y * row3.x
        val negativeDiagonal2 = row1.x * row2.z * row3.y
        val negativeDiagonal3 = row1.y * row2.x * row3.z
        return diagonal1 + diagonal2 + diagonal3 - negativeDiagonal1 - negativeDiagonal2 - negativeDiagonal3
    }

    private fun areLinearlyIndependent(
        v1: Position3D,
        v2: Position3D,
        v3: Position3D
    ): Boolean {
        // 3 vectors are linearly independent iff the determinant of the matrix formed by the vectors is non-zero.
        val determinant = determinant3x3(v1, v2, v3)
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

    // Inverse of a 3x3 matrix written row-wise. Formula from https://en.wikipedia.org/wiki/Invertible_matrix#Inversion_of_3_%C3%97_3_matrices
    // The matrix is not scaled by the inverse of the determinant.
    private fun inverse3x3(
        row1: Position3D,
        row2: Position3D,
        row3: Position3D
    ): Triple<Position3D, Position3D, Position3D> {
        val a = row2.y * row3.z - row2.z * row3.y
        val b = -row2.x * row3.z + row2.z * row3.x
        val c = row2.x * row3.y - row2.y * row3.x
        val d = -row1.y * row3.z + row1.z * row3.y
        val e = row1.x * row3.z - row1.z * row3.x
        val f = -row1.x * row3.y + row1.y * row3.x
        val g = row1.y * row2.z - row1.z * row2.y
        val h = -row1.x * row2.z + row1.z * row2.x
        val i = row1.x * row2.y - row1.y * row2.x

        return Triple(
            Position3D(a, d, g),
            Position3D(b, e, h),
            Position3D(c, f, i)
        )
    }

    private fun matrixTimesVector(
        matrix: Triple<Position3D, Position3D, Position3D>,
        vector: Position3D
    ): Position3D =
        Position3D(
            matrix.first.x * vector.x + matrix.first.y * vector.y + matrix.first.z * vector.z,
            matrix.second.x * vector.x + matrix.second.y * vector.y + matrix.second.z * vector.z,
            matrix.third.x * vector.x + matrix.third.y * vector.y + matrix.third.z * vector.z
        )


}