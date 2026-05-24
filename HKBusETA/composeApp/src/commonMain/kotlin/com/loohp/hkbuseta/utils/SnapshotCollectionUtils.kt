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

package com.loohp.hkbuseta.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.loohp.hkbuseta.common.utils.AutoSortedList


inline fun <E: Comparable<E>, T: SnapshotStateList<E>> T.asAutoSortedList(comparator: Comparator<E> = naturalOrder()): AutoSortedList<E, T> {
    return AutoSortedList(this, comparator, true)
}