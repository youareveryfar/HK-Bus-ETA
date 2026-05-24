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

import kotlin.math.max

data class Version(val version: String): CharSequence by version, Comparable<Version> {

    init {
        require(version.matches("[0-9]+(\\.[0-9]+)*".toRegex())) { "Invalid version format" }
    }

    override fun compareTo(other: Version): Int {
        val thisParts = version.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val thatParts = other.version.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val length = max(thisParts.size.toDouble(), thatParts.size.toDouble()).toInt()
        for (i in 0..<length) {
            val thisPart = if (i < thisParts.size) thisParts[i].toInt() else 0
            val thatPart = if (i < thatParts.size) thatParts[i].toInt() else 0
            if (thisPart < thatPart) return -1
            if (thisPart > thatPart) return 1
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this.javaClass != other.javaClass) return false
        return this.compareTo(other as Version) == 0
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}