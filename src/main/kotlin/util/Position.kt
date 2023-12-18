package util

data class Position(
    val line: Int,
    val column: Int
) {

    companion object {
        fun move(position: Position, direction: Direction): Position =
            when (direction) {
                Direction.UP -> position.copy(line = position.line - 1)
                Direction.DOWN -> position.copy(line = position.line + 1)
                Direction.LEFT -> position.copy(column = position.column - 1)
                Direction.RIGHT -> position.copy(column = position.column + 1)
            }

        fun fourNeighbours(position: Position): List<Pair<Direction, Position>> =
            listOf(
                Direction.UP to move(position, Direction.UP),
                Direction.DOWN to move(position, Direction.DOWN),
                Direction.LEFT to move(position, Direction.LEFT),
                Direction.RIGHT to move(position, Direction.RIGHT)
            )
    }
}
