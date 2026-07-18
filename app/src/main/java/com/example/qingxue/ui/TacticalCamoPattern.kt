package com.example.qingxue.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
internal fun Modifier.tacticalCamoPattern(intensity: Float = 1f): Modifier {
    val accent = MaterialTheme.colorScheme.primary
    val ink = Color(0xFF111113)
    val strength = intensity.coerceIn(0f, 1.25f)

    return drawBehind {
        drawPath(
            Path().apply {
                moveTo(size.width * 0.47f, 0f)
                cubicTo(
                    size.width * 0.61f, size.height * 0.03f,
                    size.width * 0.72f, size.height * 0.01f,
                    size.width * 0.83f, size.height * 0.07f
                )
                lineTo(size.width * 0.91f, size.height * 0.03f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.14f)
                cubicTo(
                    size.width * 0.88f, size.height * 0.10f,
                    size.width * 0.81f, size.height * 0.20f,
                    size.width * 0.69f, size.height * 0.15f
                )
                lineTo(size.width * 0.61f, size.height * 0.23f)
                cubicTo(
                    size.width * 0.57f, size.height * 0.18f,
                    size.width * 0.52f, size.height * 0.17f,
                    size.width * 0.47f, size.height * 0.18f
                )
                close()
            },
            ink.copy(alpha = 0.17f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.72f, size.height * 0.05f)
                lineTo(size.width, size.height * 0.12f)
                lineTo(size.width, size.height * 0.19f)
                cubicTo(
                    size.width * 0.88f, size.height * 0.15f,
                    size.width * 0.83f, size.height * 0.25f,
                    size.width * 0.69f, size.height * 0.19f
                )
                lineTo(size.width * 0.58f, size.height * 0.28f)
                lineTo(size.width * 0.54f, size.height * 0.24f)
                lineTo(size.width * 0.68f, size.height * 0.14f)
                close()
            },
            accent.copy(alpha = 0.12f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.64f, size.height * 0.31f)
                cubicTo(
                    size.width * 0.77f, size.height * 0.29f,
                    size.width * 0.86f, size.height * 0.24f,
                    size.width, size.height * 0.27f
                )
                lineTo(size.width, size.height * 0.38f)
                lineTo(size.width * 0.89f, size.height * 0.35f)
                lineTo(size.width * 0.80f, size.height * 0.42f)
                lineTo(size.width * 0.66f, size.height * 0.37f)
                lineTo(size.width * 0.56f, size.height * 0.44f)
                lineTo(size.width * 0.51f, size.height * 0.40f)
                close()
            },
            ink.copy(alpha = 0.15f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.78f, size.height * 0.43f)
                lineTo(size.width, size.height * 0.39f)
                lineTo(size.width, size.height * 0.47f)
                lineTo(size.width * 0.90f, size.height * 0.46f)
                lineTo(size.width * 0.83f, size.height * 0.51f)
                lineTo(size.width * 0.70f, size.height * 0.49f)
                close()
            },
            accent.copy(alpha = 0.10f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(0f, size.height * 0.69f)
                cubicTo(
                    size.width * 0.11f, size.height * 0.64f,
                    size.width * 0.20f, size.height * 0.73f,
                    size.width * 0.32f, size.height * 0.67f
                )
                lineTo(size.width * 0.43f, size.height * 0.74f)
                cubicTo(
                    size.width * 0.31f, size.height * 0.78f,
                    size.width * 0.22f, size.height * 0.74f,
                    size.width * 0.12f, size.height * 0.82f
                )
                lineTo(0f, size.height * 0.79f)
                close()
            },
            ink.copy(alpha = 0.18f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(0f, size.height * 0.80f)
                lineTo(size.width * 0.12f, size.height * 0.84f)
                lineTo(size.width * 0.22f, size.height * 0.78f)
                lineTo(size.width * 0.36f, size.height * 0.83f)
                lineTo(size.width * 0.29f, size.height * 0.89f)
                lineTo(size.width * 0.16f, size.height * 0.86f)
                lineTo(size.width * 0.07f, size.height * 0.93f)
                lineTo(0f, size.height * 0.90f)
                close()
            },
            accent.copy(alpha = 0.11f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.54f, size.height)
                lineTo(size.width * 0.63f, size.height * 0.91f)
                lineTo(size.width * 0.75f, size.height * 0.95f)
                lineTo(size.width * 0.84f, size.height * 0.85f)
                lineTo(size.width, size.height * 0.88f)
                lineTo(size.width, size.height)
                close()
            },
            ink.copy(alpha = 0.17f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.70f, size.height)
                lineTo(size.width * 0.79f, size.height * 0.91f)
                lineTo(size.width * 0.90f, size.height * 0.94f)
                lineTo(size.width, size.height * 0.89f)
                lineTo(size.width, size.height)
                close()
            },
            accent.copy(alpha = 0.11f * strength)
        )
    }
}
