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

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromStream


val HttpClient = HttpClient(Java)

suspend inline fun fetchResourceAsBytes(url: String): ByteArray? {
    return try {
        HttpClient.get(url) {
            headers {
                append(HttpHeaders.UserAgent, "Mozilla/5.0")
                append(HttpHeaders.CacheControl, "no-cache, no-store, must-revalidate")
                append(HttpHeaders.Pragma, "no-cache")
            }
            timeout {
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 10000
            }
        }.bodyAsBytes()
    } catch (_: Exception) {
        null
    }
}

suspend inline fun fetchResourceAsText(url: String): String? {
    return try {
        HttpClient.get(url) {
            headers {
                append(HttpHeaders.UserAgent, "Mozilla/5.0")
                append(HttpHeaders.CacheControl, "no-cache, no-store, must-revalidate")
                append(HttpHeaders.Pragma, "no-cache")
            }
            timeout {
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 10000
            }
        }.bodyAsText()
    } catch (_: Exception) {
        null
    }
}

suspend inline fun fetchResourceAsChannel(url: String): ByteReadChannel? {
    return try {
        HttpClient.get(url) {
            headers {
                append(HttpHeaders.UserAgent, "Mozilla/5.0")
                append(HttpHeaders.CacheControl, "no-cache, no-store, must-revalidate")
                append(HttpHeaders.Pragma, "no-cache")
            }
            timeout {
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 10000
            }
        }.bodyAsChannel()
    } catch (_: Exception) {
        null
    }
}

@OptIn(ExperimentalSerializationApi::class)
suspend inline fun <reified T: JsonElement> fetchResourceAsJson(url: String): T? {
    return try {
        Json.decodeFromStream<T>(fetchResourceAsChannel(url)!!.toInputStream())
    } catch (_: Exception) {
        null
    }
}