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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.loohp.hkbuseta.common.utils.Immutable
import kotlin.math.max
import kotlin.math.min

@Immutable
data class TableColumn(
    val width: TableColumnWidth,
    val alignment: Alignment.Horizontal = Alignment.Start
)

@Immutable
sealed interface TableColumnWidth {
    data class Weight(val weight: Float): TableColumnWidth
    data class Fixed(val width: Dp): TableColumnWidth
    data object Wrap: TableColumnWidth
    data class Max(val a: TableColumnWidth, val b: TableColumnWidth): TableColumnWidth
    data class Min(val a: TableColumnWidth, val b: TableColumnWidth): TableColumnWidth
}

@Immutable
data class TableRow(
    val alignment: TableRowAlignment = TableRowAlignment.Vertical(Alignment.CenterVertically),
    val onClick: (() -> Unit)? = null,
    val background: @Composable () -> Unit = { /* do nothing */ },
    val remark: (@Composable () -> Unit)? = null,
    val horizontalExtension: Dp = 0.dp,
    val minHeight: Dp = 0.dp
)

@Immutable
sealed interface TableRowAlignment {
    data class Vertical(val alignment: Alignment.Vertical): TableRowAlignment
    data class Baseline(val alignment: AlignmentLine): TableRowAlignment
}

val DefaultTableRow: TableRow = TableRow()

@Composable
fun Table(
    columnsCount: Int,
    columns: (Int) -> TableColumn = { TableColumn(width = TableColumnWidth.Weight(1F / columnsCount)) },
    modifier: Modifier = Modifier,
    rowSpacing: Dp = 0.dp,
    columnSpacing: Dp = 0.dp,
    rowDivider: @Composable () -> Unit = { /* do nothing */ },
    rows: (Int) -> TableRow = { DefaultTableRow },
    content: @Composable () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    SubcomposeLayout(modifier = modifier) { constraints ->

        val policies = List(columnsCount) { columns(it).width }
        val hAligns = List(columnsCount) { columns(it).alignment }

        val rowSpacingPx = rowSpacing.roundToPx()
        val colSpacingPx = columnSpacing.roundToPx()

        val cellMeasurables = subcompose("cells", content)
        val rowCount = (cellMeasurables.size + columnsCount - 1) / columnsCount

        fun intrinsicWidthFor(policy: TableColumnWidth, colIndex: Int): Int = when (policy) {
            is TableColumnWidth.Fixed -> policy.width.roundToPx()
            is TableColumnWidth.Wrap -> cellMeasurables
                .filterIndexed { idx, _ -> idx % columnsCount == colIndex }
                .maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) } ?: 0
            is TableColumnWidth.Max -> max(
                intrinsicWidthFor(policy.a, colIndex),
                intrinsicWidthFor(policy.b, colIndex)
            )
            is TableColumnWidth.Min -> min(
                intrinsicWidthFor(policy.a, colIndex),
                intrinsicWidthFor(policy.b, colIndex)
            )
            is TableColumnWidth.Weight -> 0
        }

        val baseWidths = policies.mapIndexed { i, p -> intrinsicWidthFor(p, i) }
        val totalFixed = baseWidths.sum() + colSpacingPx * (columnsCount - 1)
        val totalWeight = policies.mapNotNull { (it as? TableColumnWidth.Weight)?.weight }.sum()
        val remaining = (constraints.maxWidth - totalFixed).coerceAtLeast(0)

        val colWidths = baseWidths.mapIndexed { i, w ->
            (policies[i] as? TableColumnWidth.Weight)?.let { p -> ((p.weight / totalWeight) * remaining).toInt() }?: w
        }

        val tableWidthPx = colWidths.sum() + colSpacingPx * (columnsCount - 1)

        val cellPlaceables = cellMeasurables.mapIndexed { i, m ->
            val col = i % columnsCount
            m.measure(
                Constraints(
                    minWidth = 0,
                    maxWidth = colWidths[col],
                    minHeight = 0,
                    maxHeight = constraints.maxHeight
                )
            )
        }

        val dividerCount = max(0, rowCount - 1)
        val dividerPlaceables = List(dividerCount) { r ->
            subcompose("divider_$r", rowDivider)
                .firstOrNull()
                ?.measure(Constraints.fixedWidth(tableWidthPx))
        }
        val dividerHeight = dividerPlaceables.firstOrNull()?.height ?: 0

        val rowHeights = MutableList(rowCount) { 0 }
        val baselineAbove = MutableList(rowCount) { 0 }

        for (r in 0 until rowCount) {
            val start = r * columnsCount
            val end = minOf(start + columnsCount, cellPlaceables.size)
            val rowPlaceables = cellPlaceables.subList(start, end)

            when (val ra = rows(r).alignment) {
                is TableRowAlignment.Baseline -> {
                    var above = 0
                    var below = 0
                    for (p in rowPlaceables) {
                        val b = p[ra.alignment]
                        above = maxOf(above, b)
                        below = maxOf(below, p.height - b)
                    }
                    baselineAbove[r] = above
                    rowHeights[r] = above + below
                }
                is TableRowAlignment.Vertical -> {
                    rowHeights[r] = rowPlaceables.maxOfOrNull { it.height } ?: 0
                }
            }

            val diff = rows(r).minHeight.roundToPx() - rowHeights[r]
            if (diff > 0) {
                rowHeights[r] += diff
                baselineAbove[r] += diff / 2
            }
        }

        val remarkPlaceables = List(rowCount) { r ->
            rows(r).remark?.let { remark ->
                subcompose("remark_$r", remark)
                    .firstOrNull()
                    ?.measure(Constraints.fixedWidth(tableWidthPx).copy(minHeight = 0, maxHeight = constraints.maxHeight))
            }
        }
        val remarkHeights = remarkPlaceables.map { it?.height ?: 0 }

        val layoutHeight = (rowHeights.sum() + remarkHeights.sum() + (dividerHeight + rowSpacingPx) * dividerCount)
            .coerceIn(constraints.minHeight, constraints.maxHeight)

        val layoutWidth = tableWidthPx.coerceIn(constraints.minWidth, constraints.maxWidth)

        layout(layoutWidth, layoutHeight) {
            val colX = List(columnsCount) { i ->
                (0 until i).sumOf { j -> colWidths[j] + colSpacingPx }
            }

            var y = 0

            for (r in 0 until rowCount) {
                val rowInfo = rows(r)
                val horizontalExtension = rowInfo.horizontalExtension.roundToPx()
                val verticalPadding = (dividerHeight + rowSpacingPx) / 2

                val fullRowHeight = rowHeights[r] + remarkHeights[r]

                // Background
                subcompose("background_$r", rowInfo.background)
                    .firstOrNull()
                    ?.measure(Constraints.fixed(layoutWidth + horizontalExtension * 2, fullRowHeight + verticalPadding))
                    ?.place(-horizontalExtension, y - verticalPadding)

                // Click
                rowInfo.onClick?.let { onClick ->
                    subcompose("onClick_$r") {
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(onClick = onClick)
                        )
                    }
                        .firstOrNull()
                        ?.measure(Constraints.fixed(layoutWidth + horizontalExtension * 2, fullRowHeight + verticalPadding))
                        ?.place(-horizontalExtension, y - verticalPadding)
                }

                when (val alignment = rowInfo.alignment) {
                    is TableRowAlignment.Baseline -> {
                        val above = baselineAbove[r]
                        for (c in 0 until columnsCount) {
                            val idx = r * columnsCount + c
                            cellPlaceables.getOrNull(idx)?.place(
                                x = colX[c] + hAligns[c].align(cellPlaceables[idx].width, colWidths[c], layoutDirection),
                                y = y + (above - cellPlaceables[idx][alignment.alignment])
                            )
                        }
                    }
                    is TableRowAlignment.Vertical -> {
                        val h = rowHeights[r]
                        for (c in 0 until columnsCount) {
                            val idx = r * columnsCount + c
                            cellPlaceables.getOrNull(idx)?.place(
                                x = colX[c] + hAligns[c].align(cellPlaceables[idx].width, colWidths[c], layoutDirection),
                                y = y + alignment.alignment.align(cellPlaceables[idx].height, h)
                            )
                        }
                    }
                }

                y += rowHeights[r]

                remarkPlaceables[r]?.place(0, y)
                y += remarkHeights[r]

                if (r < rowCount - 1) {
                    dividerPlaceables[r]?.place(0, y)
                    y += dividerHeight + rowSpacingPx
                }
            }
        }
    }
}