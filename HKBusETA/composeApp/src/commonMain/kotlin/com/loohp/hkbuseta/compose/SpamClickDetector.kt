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

package com.loohp.hkbuseta.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.loohp.hkbuseta.common.utils.Stable
import com.loohp.hkbuseta.common.utils.currentTimeMillis


@Stable
class SpamClickDetector(
    private val requiredClicks: Int,
    private val timeWindowMillis: Long
) {
    private var clickCount = 0
    private var firstClickTime = 0L

    fun onClick(clicksLeft: (Int) -> Unit): Boolean {
        val now = currentTimeMillis()

        if (now - firstClickTime > timeWindowMillis) {
            clickCount = 1
            firstClickTime = now
            return false
        }

        clickCount++
        clicksLeft.invoke(requiredClicks - clickCount)
        if (clickCount >= requiredClicks) {
            clickCount = 0
            firstClickTime = 0L
            return true
        }

        return false
    }
}

@Composable
fun rememberSpamClickDetector(
    requiredClicks: Int = 5,
    timeWindowMillis: Long = 2000
): SpamClickDetector {
    return remember {
        SpamClickDetector(
            requiredClicks = requiredClicks,
            timeWindowMillis = timeWindowMillis
        )
    }
}