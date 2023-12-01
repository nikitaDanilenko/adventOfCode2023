package day01

import kotlinx.serialization.Serializable
import util.BigIntegerWithJson

@Serializable
data class Response1(
    val result: BigIntegerWithJson
)
