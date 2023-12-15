package day14

import day03.Day03
import day12.Day12
import java.math.BigInteger

object Day14 {

    fun solutions(input: String): Pair<BigInteger, BigInteger> {
        val parsed = parseInput(input)
        return solution1(parsed) to solution2(parsed)
    }

    private fun solution1(dish: Dish): BigInteger {
        val dropped = dropNorth(dish)
        val result = dropped.columns.map(::dropColumn)

        return result.sumOf { column ->
            column.mapIndexed { index, tile ->
                if (tile == Tile.Filled(Rock.ROUND)) {
                    (dish.size - index).toBigInteger()
                } else {
                    BigInteger.ZERO
                }
            }.sumOf { it }.also { println(it) }
        }
    }

    private fun solution2(dish: Dish): BigInteger {
        return BigInteger.ZERO
    }


    enum class Rock {
        CUBE, ROUND;
    }

    private fun parseRock(char: Char): Rock? =
        when (char) {
            '#' -> Rock.CUBE
            'O' -> Rock.ROUND
            else -> null
        }

    sealed interface Tile {
        data object Empty : Tile
        data class Filled(val rock: Rock) : Tile
    }


    data class Dish(
        val columns: List<List<Tile>>,
        val size: Int
    )

    private fun parseInput(input: String): Dish {
        val lines = input.lines()
        val tiles = lines
            .map { line ->
                line.mapIndexed { columnIndex, c ->
                    val tile = parseRock(c)?.let { rock -> Tile.Filled(rock) } ?: Tile.Empty
                    columnIndex to tile
                }
            }
            .flatten()
            .groupBy { it.first }
            .mapValues { it.value.sortedBy { p -> p.first }.map { p -> p.second } }
            .toList()
            .sortedBy { it.first }
            .map { it.second }
        return Dish(tiles, lines.size)
    }

    private fun dropNorth(dish: Dish): Dish {
        val modifiedDish = dish
            .columns
            .map {
                dropColumn(it)
            }

        return dish.copy(columns = modifiedDish)
    }

    fun <A> collectAlternatingGroups(
        xs: List<A>,
        predicate: (A) -> Boolean
    ): List<List<A>> {

        tailrec fun recur(
            rest: List<A>,
            groups: List<List<A>>,
            predicate: (A) -> Boolean
        ): List<List<A>> =
            if (rest.isEmpty())
                groups
            else {
                val (match, remainder) = Day03.span(rest, predicate)
                recur(remainder, groups.plusElement(match)) { !predicate(it) }
            }

        return recur(xs, emptyList(), predicate)
    }

    private fun dropColumn(
        list: List<Tile>
    ): List<Tile> =
        collectAlternatingGroups(list) { tile ->
            tile == Tile.Filled(Rock.CUBE)
        }.flatMap { rockGroup ->
            if (rockGroup.contains(Tile.Filled(Rock.CUBE))) {
                rockGroup
            } else {
                val size = rockGroup.size
                val dropped = rockGroup.filter { tile -> tile == Tile.Filled(Rock.ROUND) }
                val droppedSize = dropped.size
                val remainder = List(size - droppedSize) { Tile.Empty }
                dropped + remainder
            }
        }

}