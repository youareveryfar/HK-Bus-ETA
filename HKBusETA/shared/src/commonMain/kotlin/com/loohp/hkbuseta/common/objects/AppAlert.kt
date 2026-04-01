/*
 * This file is part of HKBusETA.
 *
 * Copyright (C) 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2025. Contributors
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

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppAlert(
    val content: BilingualText? = null,
    @SerialName("url")
    private val urlZh: String? = null,
    private val urlEn: String? = null,
    private val validFrom: LocalDateTime? = null,
    private val validTo: LocalDateTime? = null,
    private val extras: List<AppAlert> = emptyList()
) {
    val isNotBlank: Boolean get() = content?.isNotBlank() == true

    fun url(language: String): String? {
        return if (language == "en") (urlEn?: urlZh) else urlZh
    }

    fun isAlertValidAt(time: LocalDateTime): Boolean {
        return !(validFrom != null && validFrom > time) && !(validTo != null && validTo < time)
    }

    fun collectValidAlertsAt(time: LocalDateTime): List<AppAlert> {
        return buildList {
            if (isNotBlank && isAlertValidAt(time)) {
                add(copy(extras = emptyList()))
            }
            for (extra in extras) {
                addAll(extra.collectValidAlertsAt(time))
            }
        }
    }
}

fun AppAlert?.takeFirstValidAtOrNull(time: LocalDateTime): AppAlert? {
    return this?.takeIf { it.collectValidAlertsAt(time).firstOrNull()?.isNotBlank == true }
}

fun AppAlert?.collectValidAlertsAt(time: LocalDateTime): List<AppAlert> {
    return this?.collectValidAlertsAt(time)?: emptyList()
}