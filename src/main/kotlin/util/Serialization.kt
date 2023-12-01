package util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigInteger


typealias BigIntegerWithJson = @Serializable(with = Serialization.BigIntegerSerializer::class) BigInteger

object Serialization {

    object BigIntegerSerializer : KSerializer<BigInteger> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.INT)

        // taken from https://stackoverflow.com/a/75257763/5296624
        override fun deserialize(decoder: Decoder): BigInteger =
            when (decoder) {
                is JsonDecoder -> decoder.decodeJsonElement().jsonPrimitive.content.toBigInteger()
                else -> decoder.decodeString().toBigInteger()
            }


        // taken from https://stackoverflow.com/a/75257763/5296624
        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: BigInteger) =
            when (encoder) {
                is JsonEncoder -> encoder.encodeJsonElement(JsonUnquotedLiteral(value.toString()))
                else -> encoder.encodeString(value.toString())
            }

    }

}
