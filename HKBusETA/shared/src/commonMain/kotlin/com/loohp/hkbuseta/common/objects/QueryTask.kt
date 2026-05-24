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

import com.loohp.hkbuseta.common.utils.IO
import com.loohp.hkbuseta.common.utils.Immutable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.DateTimeUnit

@Immutable
open class QueryTask<T>(
    private val errorResult: T,
    private val task: suspend () -> T
) {

    suspend fun query(): T {
        return try {
            task.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
            errorResult
        }
    }

    suspend fun query(timeout: Int, unit: DateTimeUnit.TimeBased): T {
        return try {
            withTimeout(unit.duration.times(timeout).inWholeMilliseconds) { task.invoke() }
        } catch (e: Exception) {
            e.printStackTrace()
            errorResult
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun query(callback: (T) -> Unit) {
        val deferred = CoroutineScope(Dispatchers.IO).async { task.invoke() }
        deferred.invokeOnCompletion { it?.let {
            it.printStackTrace()
            callback.invoke(errorResult)
        }?: callback.invoke(deferred.getCompleted()) }
    }

    fun query(timeout: Int, unit: DateTimeUnit.TimeBased, callback: (T) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val deferred = async { task.invoke() }
            try {
                callback.invoke(withTimeout(unit.duration.times(timeout).inWholeMilliseconds) { deferred.await() })
            } catch (e: Exception) {
                e.printStackTrace()
                try { deferred.cancel() } catch (_: Throwable) { }
                callback.invoke(errorResult)
            }
        }
    }
}