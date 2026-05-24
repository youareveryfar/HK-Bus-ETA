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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.loohp.hkbuseta.common.utils.ceilToInt


@Composable
fun VerticalGrid(
    columns: Int,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(8.dp),
    content: @Composable () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    val topPadding = paddingValues.calculateTopPadding()
    val bottomPadding = paddingValues.calculateBottomPadding()
    val startPadding = paddingValues.calculateStartPadding(layoutDirection)
    val endPadding = paddingValues.calculateEndPadding(layoutDirection)
    val verticalPadding = topPadding + bottomPadding
    val horizontalPadding = startPadding + endPadding
    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = { measurables, constraints ->
            val width = constraints.maxWidth
            val individualWidth = width / columns - horizontalPadding.roundToPx()
            val individualMaxHeight = measurables.maxOf { it.minIntrinsicHeight(individualWidth) }
            val individualConstraint = constraints.copy(
                minWidth = individualWidth,
                maxWidth = individualWidth,
                minHeight = individualMaxHeight,
                maxHeight = individualMaxHeight
            )
            val placeables = measurables.map { it.measure(individualConstraint) }
            val height = (individualMaxHeight + verticalPadding.roundToPx()) * (placeables.size / columns.toFloat()).ceilToInt()
            val itemsPerRow = width / individualWidth
            layout(width, height) {
                for ((index, placeable) in placeables.withIndex()) {
                    val x = ((individualWidth + horizontalPadding.roundToPx()) * (index % itemsPerRow)) + startPadding.roundToPx()
                    val y = (index / columns) * (individualMaxHeight + verticalPadding.roundToPx()) + topPadding.roundToPx()
                    placeable.placeRelative(x, y)
                }
            }
        }
    )
}