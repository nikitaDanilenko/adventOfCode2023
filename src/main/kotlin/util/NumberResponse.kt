package util

import kotlinx.serialization.Serializable

@Serializable
data class NumberResponse(
    val solution1: BigIntegerWithJson,
    val solution2: BigIntegerWithJson
)