package day01

import kotlinx.serialization.Serializable
import util.BigIntegerWithJson

@Serializable
data class Response1(
    val solution1: BigIntegerWithJson,
    val solution2: BigIntegerWithJson
)
