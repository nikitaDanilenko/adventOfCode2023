package day02

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val solution1: Int,
    val solution2: Int
)