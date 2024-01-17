package util

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    companion object {
        fun opposite(direction: Direction): Direction = when (direction) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}