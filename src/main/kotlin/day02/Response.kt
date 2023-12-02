package day02

import kotlinx.serialization.Serializable
import util.BigIntegerWithJson

@Serializable
data class Response(
    val solution1: Int,
    val solution2: BigIntegerWithJson
)