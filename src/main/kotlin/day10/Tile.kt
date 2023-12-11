package day10

enum class Tile {
    VERTICAL, HORIZONTAL, NORTH_AND_EAST, NORTH_AND_WEST, SOUTH_AND_WEST, SOUTH_AND_EAST, EMPTY;

    companion object {
        fun parseTile(c: Char): Tile? = when (c) {
            '|' -> VERTICAL
            '-' -> HORIZONTAL
            'L' -> NORTH_AND_EAST
            'J' -> NORTH_AND_WEST
            '7' -> SOUTH_AND_WEST
            'F' -> SOUTH_AND_EAST
            '.' -> EMPTY
            else -> null
        }

        fun hasNorthExit(tile: Tile): Boolean = tile == VERTICAL || tile == NORTH_AND_EAST || tile == NORTH_AND_WEST
        fun hasSouthExit(tile: Tile): Boolean = tile == VERTICAL || tile == SOUTH_AND_EAST || tile == SOUTH_AND_WEST
        fun hasEastExit(tile: Tile): Boolean = tile == HORIZONTAL || tile == NORTH_AND_EAST || tile == SOUTH_AND_EAST
        fun hasWestExit(tile: Tile): Boolean = tile == HORIZONTAL || tile == NORTH_AND_WEST || tile == SOUTH_AND_WEST

        fun replaceStart(top: Tile, left: Tile, right: Tile, bottom: Tile): Tile =
            if (hasSouthExit(top) && hasEastExit(left)) NORTH_AND_WEST
            else if (hasSouthExit(top) && hasNorthExit(bottom)) VERTICAL
            else if (hasSouthExit(top) && hasWestExit(right)) NORTH_AND_EAST
            else if (hasEastExit(left) && hasNorthExit(bottom)) SOUTH_AND_WEST
            else if (hasEastExit(left) && hasWestExit(right)) HORIZONTAL
            else SOUTH_AND_EAST

        data class Position(val line: Int, val column: Int)
        data class TileMap(val tiles: Map<Position, Tile>, val startPosition: Position, val startTile: Tile)

        fun parse(input: String): TileMap {
            val withStart = input.lines()
                .mapIndexed { lineIndex, line ->
                    line.mapIndexed { columnIndex, c ->
                        Position(lineIndex, columnIndex) to parseTile(c)
                    }
                }
                .flatten()
                .toMap()
            val startPosition = withStart.filterValues { it == null }.keys.first()
            fun getWithDefault(position: Position): Tile = withStart.getOrDefault(position, EMPTY)!!

            val replacement = replaceStart(
                top = getWithDefault(Position(startPosition.line - 1, startPosition.column)),
                left = getWithDefault(Position(startPosition.line, startPosition.column - 1)),
                right = getWithDefault(Position(startPosition.line, startPosition.column + 1)),
                bottom = getWithDefault(Position(startPosition.line + 1, startPosition.column))
            )
            return TileMap(
                (withStart + (startPosition to replacement)).mapValues { it.value!! },
                startPosition,
                replacement
            )
        }

    }


}