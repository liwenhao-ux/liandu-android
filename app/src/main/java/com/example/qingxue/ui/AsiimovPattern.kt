package com.example.qingxue.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path

@Composable
internal fun Modifier.asiimovPattern(): Modifier {
    val accent = MaterialTheme.colorScheme.primary
    val neutral = MaterialTheme.colorScheme.onSurface

    return drawBehind {
        drawPath(
            path = Path().apply {
                moveTo(size.width * 0.52f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.30f)
                lineTo(size.width * 0.72f, size.height * 0.52f)
                close()
            },
            color = neutral.copy(alpha = 0.045f)
        )
        drawPath(
            path = Path().apply {
                moveTo(size.width * 0.82f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.60f)
                lineTo(size.width * 0.92f, size.height * 0.68f)
                lineTo(size.width * 0.92f, size.height * 0.22f)
                close()
            },
            color = accent.copy(alpha = 0.13f)
        )
        drawPath(
            path = Path().apply {
                moveTo(0f, size.height * 0.88f)
                lineTo(size.width * 0.28f, size.height * 0.88f)
                lineTo(size.width * 0.20f, size.height)
                lineTo(0f, size.height)
                close()
            },
            color = accent.copy(alpha = 0.07f)
        )
    }
}