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

package com.loohp.hkbuseta.notificationserver.utils

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readByte
import io.ktor.utils.io.readByteArray
import io.ktor.utils.io.writeByte
import io.ktor.utils.io.writeByteArray
import java.io.IOException
import java.nio.charset.Charset


suspend fun ByteReadChannel.readVarInt(): Int {
    var i = 0
    var j = 0
    var b: Byte
    do {
        b = readByte()
        i = i or ((b.toInt() and 127) shl j++ * 7)
        if (j > 5) {
            throw RuntimeException("VarInt too big")
        }
    } while ((b.toInt() and 128) == 128)
    return i
}

suspend fun ByteWriteChannel.writeVarInt(value: Int) {
    var i = value
    while ((i and -128) != 0) {
        writeByte((i and 127 or 128).toByte())
        i = i ushr 7
    }
    writeByte(i.toByte())
}

suspend fun ByteReadChannel.readString(charset: Charset): String {
    val length = readVarInt()
    if (length == -1) {
        throw IOException("Premature end of stream.")
    }
    val b = readByteArray(length)
    return String(b, charset)
}

suspend fun ByteWriteChannel.writeString(value: String, charset: Charset) {
    val bytes = value.toByteArray(charset)
    writeVarInt(bytes.size)
    writeByteArray(bytes)
}

suspend fun ByteReadChannel.readBoolean(): Boolean {
    return readByte() > 0
}

suspend fun ByteWriteChannel.writeBoolean(value: Boolean) {
    writeByte(if (value) 1 else 0)
}