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

import kotlinx.coroutines.delay


suspend inline fun awaitUntil(
    pollInterval: Long = 50,
    onPollFailed: () -> Unit = { /* do nothing */ },
    predicate: () -> Boolean
) {
    val delay = pollInterval.coerceAtLeast(1)
    while (!predicate.invoke()) {
        onPollFailed.invoke()
        delay(delay)
    }
}

suspend inline fun <T> retryUntil(
    retry: Long = 10000,
    maxTries: Int = 50,
    block: () -> T,
    predicate: (T) -> Boolean,
    fallbackValue: T
): T {
    for (i in 0 until maxTries) {
        val value = try {
            block.invoke()
        } catch (_: Throwable) {
            null
        }
        if (value != null && predicate.invoke(value)) {
            return value
        }
        delay(retry)
    }
    return fallbackValue
}