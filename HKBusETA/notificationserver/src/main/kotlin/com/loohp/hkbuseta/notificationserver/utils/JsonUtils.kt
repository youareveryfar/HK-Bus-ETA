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

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


val EMPTY_JSON_OBJECT = JsonObject(emptyMap())
val EMPTY_JSON_ARRAY = JsonArray(emptyList())

fun emptyJsonObject(): JsonObject = EMPTY_JSON_OBJECT
fun emptyJsonArray(): JsonArray = EMPTY_JSON_ARRAY

inline fun <V> Map<*, V>.toJsonObject(valueSerializer: (V) -> Any? = { it }): JsonObject {
    return buildJsonObject {
        this@toJsonObject.forEach { (rawKey, rawValue) ->
            val key = rawKey.toString()
            when (val value = valueSerializer.invoke(rawValue)) {
                is JsonElement -> put(key, value)
                is Number -> put(key, value)
                is String -> put(key, value)
                is Boolean -> put(key, value)
                else -> throw IllegalArgumentException()
            }
        }
    }
}