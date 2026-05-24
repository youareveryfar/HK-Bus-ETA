/*
 * This file is part of HKBusETA.
 *
 * Copyright (C) 2026. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2026. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.hkbuseta.common.objects

import com.loohp.hkbuseta.common.utils.Immutable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Serializable(with = FareSerializer::class)
@Immutable
class Fare private constructor(val tenth: Int): Number(), Comparable<Fare> {

    companion object {
        val ZERO = of(0F)
        val TWO = of(2F)

        fun of(value: Float): Fare {
            return Fare((value * 10).roundToInt())
        }

        fun of(value: String): Fare {
            return of(value.toFloat())
        }

        fun of(value: Int): Fare {
            return Fare(value * 10)
        }

    }

    override fun toDouble(): Double {
        return tenth / 10.0
    }

    override fun toFloat(): Float {
        return tenth / 10F
    }

    override fun toLong(): Long {
        return toDouble().roundToLong()
    }

    override fun toInt(): Int {
        return toFloat().roundToInt()
    }

    override fun toShort(): Short {
        return toInt().toShort()
    }

    override fun toByte(): Byte {
        return toInt().toByte()
    }

    override fun compareTo(other: Fare): Int {
        return tenth.compareTo(other.tenth)
    }

    override fun toString(): String {
        return "${tenth / 10}.${(tenth % 10).absoluteValue}"
    }

    operator fun plus(other: Fare): Fare {
        return Fare(tenth + other.tenth)
    }

    operator fun minus(other: Fare): Fare {
        return Fare(tenth - other.tenth)
    }

    operator fun plus(other: Number): Fare {
        return Fare((tenth + other.toFloat() * 10F).roundToInt())
    }

    operator fun minus(other: Number): Fare {
        return Fare((tenth - other.toFloat() * 10F).roundToInt())
    }

    operator fun times(other: Number): Fare {
        return Fare((tenth * other.toFloat()).roundToInt())
    }

    operator fun div(other: Number): Fare {
        return Fare((tenth / other.toFloat()).roundToInt())
    }

}

object FareSerializer : KSerializer<Fare> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Fare", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Fare {
        return Fare.of(decoder.decodeSerializableValue(JsonElement.serializer()).jsonPrimitive.float)
    }

    override fun serialize(encoder: Encoder, value: Fare) {
        encoder.encodeString(value.toString())
    }
}
