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

import java.io.InputStream
import java.io.SequenceInputStream
import java.util.Collections
import java.util.Enumeration


fun <T> Collection<T>.asEnumeration(): Enumeration<T> {
    return Collections.enumeration(this)
}

fun <T: InputStream> Collection<T>.joinInputStreams(): InputStream {
    return SequenceInputStream(asEnumeration())
}

class MergedList<T>(
    val list1: List<T>,
    val list2: List<T>
): AbstractList<T>() {
    override val size: Int get() = list1.size + list2.size
    override fun get(index: Int): T {
        if (index < list1.size) {
            return list1[index];
        }
        return list2[index - list1.size]
    }
}

fun <T> List<T>.asMergedView(other: List<T>): List<T> = MergedList(this, other)

class MapEntry<K, V>(
    override val key: K,
    override val value : V
): Map.Entry<K, V>