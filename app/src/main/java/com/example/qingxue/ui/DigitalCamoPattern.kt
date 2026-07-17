package com.example.qingxue.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.ceil

@Composable
internal fun Modifier.digitalCamoPattern(intensity: Float = 1f): Modifier {
    val accent = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.background
    val strength = intensity.coerceIn(0f, 1.25f)

    return drawBehind {
        val columns = 24
        val cell = size.width / columns
        val rows = ceil(size.height / cell).toInt().coerceAtLeast(1)

        repeat(rows) { row ->
            repeat(columns) { column ->
                val x = column.toFloat() / columns
                val y = row.toFloat() / rows
                val inCamoZone =
                    (x > 0.56f && y < 0.48f) ||
                        (y > 0.66f && (x < 0.40f || x > 0.62f))
                if (!inCamoZone) return@repeat

                val hash = (column + 3) * 73 + (row + 5) * 41 + column * row * 17
                if (hash % 7 > 2) return@repeat

                val widthInCells = 1 + (hash / 7) % 3
                val heightInCells = if ((hash / 29) % 5 == 0) 2 else 1
                val isAccent = hash % 5 == 0
                drawRect(
                    color = (if (isAccent) accent else ink).copy(
                        alpha = (if (isAccent) 0.13f else 0.20f) * strength
                    ),
                    topLeft = Offset(column * cell, row * cell),
                    size = Size(
                        width = (widthInCells * cell).coerceAtMost(size.width - column * cell),
                        height = (heightInCells * cell).coerceAtMost(size.height - row * cell)
                    )
                )
            }
        }
    }
}
