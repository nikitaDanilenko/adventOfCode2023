package day16

import util.Direction
import util.Position
import java.math.BigInteger

object Day16 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val contraption = parseInput(input)
        return solution1(contraption) to solution2(contraption)
    }

    private fun solution1(contraption: Contraption): BigInteger =
        countVisited(contraption, Beam(Direction.RIGHT, Position(0, 0)))

    private fun countVisited(contraption: Contraption, startBeam: Beam): BigInteger {
        val visited = iterateStep(startBeam, contraption)
        return visited.map { it.position }.toSet().size.toBigInteger()
    }

    //TODO: Very slow, although only non-empty lines and columns are considered.
    //      It may be worth looking into it.
    private fun solution2(contraption: Contraption): BigInteger {
        val allStartBeams =
            ((0..<contraption.width).map { Beam(Direction.DOWN, Position(0, it)) } +
                    (0..<contraption.width).map { Beam(Direction.UP, Position(contraption.height - 1, it)) } +
                    (0..<contraption.height).map { Beam(Direction.RIGHT, Position(it, 0)) } +
                    (0..<contraption.height).map { Beam(Direction.LEFT, Position(it, contraption.width - 1)) }).toSet()
        val max = allStartBeams.maxOf { beam ->
            val isZero = when (beam.direction) {
                Direction.UP, Direction.DOWN -> contraption.elements.keys.none { it.column == beam.position.column }
                Direction.LEFT, Direction.RIGHT -> contraption.elements.keys.none { it.line == beam.position.line }
            }
            if (isZero) BigInteger.ZERO
            else
                countVisited(contraption, beam)
        }
        return max
    }

    enum class MirrorOrientation {
        BOTTOM_LEFT_TOP_RIGHT, TOP_LEFT_BOTTOM_RIGHT
    }

    enum class SplitterOrientation {
        HORIZONTAL, VERTICAL
    }

    sealed interface Element {
        data class Mirror(
            val orientation: MirrorOrientation
        ) : Element

        data class Splitter(
            val orientation: SplitterOrientation
        ) : Element

    }

    data class Contraption(
        val elements: Map<Position, Element>,
        val height: Int,
        val width: Int
    )

    private fun parseInput(input: String): Contraption {
        val lines = input.lines()

        val elements = lines.flatMapIndexed { lineIndex, line ->
            line.mapIndexedNotNull { columnIndex, char ->
                val position = Position(line = lineIndex, column = columnIndex)
                val element = when (char) {
                    '\\' -> Element.Mirror(MirrorOrientation.TOP_LEFT_BOTTOM_RIGHT)
                    '/' -> Element.Mirror(MirrorOrientation.BOTTOM_LEFT_TOP_RIGHT)
                    '|' -> Element.Splitter(SplitterOrientation.VERTICAL)
                    '-' -> Element.Splitter(SplitterOrientation.HORIZONTAL)
                    else -> null
                }
                element?.let { position to it }
            }
        }.toMap()

        return Contraption(elements, lines.size, lines.first().length)
    }


    data class Beam(
        val direction: Direction,
        val position: Position
    )

    private fun moveBeam(
        beam: Beam,
        contraption: Contraption
    ): Set<Beam> {
        fun insideBoundary(beam: Beam): Boolean =
            beam.position.line >= 0 && beam.position.line < contraption.height &&
                    beam.position.column >= 0 && beam.position.column < contraption.width

        val elementAt = contraption.elements[beam.position]
        val nextBeams =
            if (elementAt == null) {
                listOf(beam.copy(position = Position.move(beam.position, beam.direction)))
            } else {
                when (elementAt) {
                    is Element.Mirror -> {
                        val (nextDirection, nextPosition) = when (elementAt.orientation) {
                            MirrorOrientation.BOTTOM_LEFT_TOP_RIGHT -> when (beam.direction) {
                                Direction.UP -> Direction.RIGHT to Position.move(beam.position, Direction.RIGHT)
                                Direction.DOWN -> Direction.LEFT to Position.move(beam.position, Direction.LEFT)
                                Direction.RIGHT -> Direction.UP to Position.move(beam.position, Direction.UP)
                                Direction.LEFT -> Direction.DOWN to Position.move(beam.position, Direction.DOWN)
                            }

                            MirrorOrientation.TOP_LEFT_BOTTOM_RIGHT -> when (beam.direction) {
                                Direction.UP -> Direction.LEFT to Position.move(beam.position, Direction.LEFT)
                                Direction.DOWN -> Direction.RIGHT to Position.move(beam.position, Direction.RIGHT)
                                Direction.LEFT -> Direction.UP to Position.move(beam.position, Direction.UP)
                                Direction.RIGHT -> Direction.DOWN to Position.move(beam.position, Direction.DOWN)
                            }
                        }
                        listOf(beam.copy(direction = nextDirection, position = nextPosition))
                    }

                    is Element.Splitter -> {
                        when (elementAt.orientation) {
                            SplitterOrientation.VERTICAL -> {
                                when (beam.direction) {
                                    Direction.UP ->
                                        listOf(beam.copy(position = Position.move(beam.position, Direction.UP)))

                                    Direction.DOWN ->
                                        listOf(beam.copy(position = Position.move(beam.position, Direction.DOWN)))

                                    else ->
                                        listOf(
                                            beam.copy(
                                                direction = Direction.UP,
                                                position = Position.move(beam.position, Direction.UP)
                                            ),
                                            beam.copy(
                                                direction = Direction.DOWN,
                                                position = Position.move(beam.position, Direction.DOWN)
                                            )
                                        )
                                }
                            }

                            SplitterOrientation.HORIZONTAL -> {
                                when (beam.direction) {
                                    Direction.LEFT ->
                                        listOf(beam.copy(position = Position.move(beam.position, Direction.LEFT)))

                                    Direction.RIGHT ->
                                        listOf(beam.copy(position = Position.move(beam.position, Direction.RIGHT)))

                                    else ->
                                        listOf(
                                            beam.copy(
                                                direction = Direction.LEFT,
                                                position = Position.move(beam.position, Direction.LEFT)
                                            ),
                                            beam.copy(
                                                direction = Direction.RIGHT,
                                                position = Position.move(beam.position, Direction.RIGHT)
                                            )
                                        )
                                }
                            }
                        }
                    }
                }
            }
        val next = nextBeams.filter(::insideBoundary).toSet()

        return next
    }

    private fun iterateStep(
        beam: Beam,
        contraption: Contraption
    ): Set<Beam> {

        fun iterate(
            beams: Set<Beam>,
            visited: Set<Beam>
        ): Set<Beam> =
            if (beams.isEmpty())
                visited
            else {
                if (visited.containsAll(beams))
                    visited
                else {
                    val nextVisited = visited.plus(beams)
                    val nextBeams = beams.minus(visited).flatMap { moveBeam(it, contraption) }
                    iterate(nextBeams.toSet(), nextVisited)
                }
            }

        return iterate(setOf(beam), emptySet())
    }

}