package com.example.qingxue.ui

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
internal fun BrandMark(modifier: Modifier = Modifier) {
    val neutral = MaterialTheme.colorScheme.onSurface
    val accent = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        fun risingBar(left: Float, top: Float, right: Float, color: Color) {
            drawPath(
                path = Path().apply {
                    moveTo(left, size.height * 0.78f)
                    lineTo(left, top + size.height * 0.08f)
                    lineTo(right, top)
                    lineTo(right, size.height * 0.78f)
                    close()
                },
                color = color
            )
        }

        risingBar(size.width * 0.12f, size.height * 0.50f, size.width * 0.34f, neutral.copy(alpha = 0.78f))
        risingBar(size.width * 0.40f, size.height * 0.34f, size.width * 0.62f, accent)
        risingBar(size.width * 0.68f, size.height * 0.18f, size.width * 0.90f, neutral)
    }
}