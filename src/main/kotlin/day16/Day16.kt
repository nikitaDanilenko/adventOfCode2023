package day16

import util.Direction
import util.Position
import java.math.BigInteger

object Day16 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val contraption = parseInput(input)
        return solution1(contraption) to BigInteger.ZERO
    }

    private fun solution1(contraption: Contraption): BigInteger {
        val beam = Beam(Direction.RIGHT, Position(0, 0))
        val visited = iterateStep(beam, contraption)
        prettyPrintPositions(visited.map { it.position }.toSet(), contraption.height, contraption.width)
        return visited.map { it.position }.toSet().size.toBigInteger()
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
                val nextBeams = beams.flatMap { moveBeam(it, contraption) }
                val nextVisited = visited.plus(beams)
                if (nextVisited == visited)
                    nextVisited
                else
                    iterate(nextBeams.toSet(), nextVisited)
            }

        return iterate(setOf(beam), emptySet())
    }

    fun prettyPrint(
        beams: Set<Beam>,
        contraption: Contraption
    ): Unit {
        val lines = (0..<contraption.height).map { lineIndex ->
            (0..<contraption.width).joinToString("") { columnIndex ->
                val position = Position(line = lineIndex, column = columnIndex)

                val beamsAtPosition = beams.filter { it.position == position }
                if (beamsAtPosition.size > 1)
                    "${beamsAtPosition.size}"
                else if (beamsAtPosition.size == 1) {
                    val first = beamsAtPosition.first()
                    when (first.direction) {
                        Direction.UP -> "^"
                        Direction.DOWN -> "v"
                        Direction.LEFT -> "<"
                        Direction.RIGHT -> ">"
                    }
                } else "."
            }
        }
        println(lines.joinToString("\n"))
    }

    // TODO: Remove after debugging?
    fun prettyPrintPositions(
        set: Set<Position>,
        height: Int,
        width: Int
    ): Unit {
        val lines = (0..<height).map { lineIndex ->
            (0..<width).joinToString("") { columnIndex ->
                val position = Position(line = lineIndex, column = columnIndex)

                if (set.contains(position))
                    "#"
                else "."
            }
        }
        println(lines.joinToString("\n"))
    }

}