package com.example.qingxue.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
internal fun Modifier.classicCamoPattern(intensity: Float = 1f): Modifier {
    val red = MaterialTheme.colorScheme.primary
    val darkRed = Color(0xFF681D27)
    val black = Color(0xFF0D0D0F)
    val strength = intensity.coerceIn(0f, 1.25f)

    return drawBehind {
        drawPath(
            Path().apply {
                moveTo(size.width * 0.54f, 0f)
                cubicTo(
                    size.width * 0.62f, size.height * 0.06f,
                    size.width * 0.72f, size.height * 0.01f,
                    size.width * 0.78f, size.height * 0.07f
                )
                cubicTo(
                    size.width * 0.86f, size.height * 0.14f,
                    size.width * 0.94f, size.height * 0.05f,
                    size.width, size.height * 0.10f
                )
                lineTo(size.width, size.height * 0.31f)
                cubicTo(
                    size.width * 0.91f, size.height * 0.35f,
                    size.width * 0.84f, size.height * 0.27f,
                    size.width * 0.76f, size.height * 0.32f
                )
                cubicTo(
                    size.width * 0.66f, size.height * 0.38f,
                    size.width * 0.59f, size.height * 0.29f,
                    size.width * 0.51f, size.height * 0.33f
                )
                cubicTo(
                    size.width * 0.45f, size.height * 0.26f,
                    size.width * 0.52f, size.height * 0.16f,
                    size.width * 0.54f, 0f
                )
                close()
            },
            black.copy(alpha = 0.17f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.69f, size.height * 0.08f)
                cubicTo(
                    size.width * 0.77f, size.height * 0.05f,
                    size.width * 0.82f, size.height * 0.13f,
                    size.width * 0.88f, size.height * 0.11f
                )
                cubicTo(
                    size.width * 0.95f, size.height * 0.08f,
                    size.width * 0.99f, size.height * 0.16f,
                    size.width, size.height * 0.21f
                )
                cubicTo(
                    size.width * 0.91f, size.height * 0.18f,
                    size.width * 0.86f, size.height * 0.25f,
                    size.width * 0.78f, size.height * 0.21f
                )
                cubicTo(
                    size.width * 0.73f, size.height * 0.19f,
                    size.width * 0.66f, size.height * 0.16f,
                    size.width * 0.69f, size.height * 0.08f
                )
                close()
            },
            red.copy(alpha = 0.10f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.68f, size.height * 0.39f)
                cubicTo(
                    size.width * 0.77f, size.height * 0.34f,
                    size.width * 0.84f, size.height * 0.43f,
                    size.width * 0.92f, size.height * 0.39f
                )
                cubicTo(
                    size.width * 0.97f, size.height * 0.37f,
                    size.width, size.height * 0.44f,
                    size.width, size.height * 0.52f
                )
                cubicTo(
                    size.width * 0.91f, size.height * 0.55f,
                    size.width * 0.84f, size.height * 0.49f,
                    size.width * 0.76f, size.height * 0.55f
                )
                cubicTo(
                    size.width * 0.69f, size.height * 0.59f,
                    size.width * 0.61f, size.height * 0.51f,
                    size.width * 0.63f, size.height * 0.45f
                )
                cubicTo(
                    size.width * 0.64f, size.height * 0.42f,
                    size.width * 0.66f, size.height * 0.40f,
                    size.width * 0.68f, size.height * 0.39f
                )
                close()
            },
            darkRed.copy(alpha = 0.15f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(0f, size.height * 0.61f)
                cubicTo(
                    size.width * 0.08f, size.height * 0.56f,
                    size.width * 0.14f, size.height * 0.65f,
                    size.width * 0.21f, size.height * 0.61f
                )
                cubicTo(
                    size.width * 0.29f, size.height * 0.56f,
                    size.width * 0.37f, size.height * 0.65f,
                    size.width * 0.40f, size.height * 0.73f
                )
                cubicTo(
                    size.width * 0.33f, size.height * 0.79f,
                    size.width * 0.26f, size.height * 0.74f,
                    size.width * 0.19f, size.height * 0.80f
                )
                cubicTo(
                    size.width * 0.12f, size.height * 0.86f,
                    size.width * 0.06f, size.height * 0.79f,
                    0f, size.height * 0.83f
                )
                close()
            },
            black.copy(alpha = 0.18f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.12f, size.height * 0.73f)
                cubicTo(
                    size.width * 0.18f, size.height * 0.69f,
                    size.width * 0.23f, size.height * 0.77f,
                    size.width * 0.29f, size.height * 0.74f
                )
                cubicTo(
                    size.width * 0.36f, size.height * 0.72f,
                    size.width * 0.42f, size.height * 0.80f,
                    size.width * 0.38f, size.height * 0.87f
                )
                cubicTo(
                    size.width * 0.30f, size.height * 0.89f,
                    size.width * 0.25f, size.height * 0.84f,
                    size.width * 0.18f, size.height * 0.89f
                )
                cubicTo(
                    size.width * 0.12f, size.height * 0.91f,
                    size.width * 0.08f, size.height * 0.80f,
                    size.width * 0.12f, size.height * 0.73f
                )
                close()
            },
            red.copy(alpha = 0.10f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.51f, size.height * 0.70f)
                cubicTo(
                    size.width * 0.58f, size.height * 0.64f,
                    size.width * 0.67f, size.height * 0.72f,
                    size.width * 0.72f, size.height * 0.68f
                )
                cubicTo(
                    size.width * 0.80f, size.height * 0.62f,
                    size.width * 0.88f, size.height * 0.73f,
                    size.width * 0.94f, size.height * 0.70f
                )
                cubicTo(
                    size.width, size.height * 0.67f,
                    size.width, size.height * 0.78f,
                    size.width, size.height * 0.84f
                )
                cubicTo(
                    size.width * 0.90f, size.height * 0.87f,
                    size.width * 0.84f, size.height * 0.81f,
                    size.width * 0.76f, size.height * 0.88f
                )
                cubicTo(
                    size.width * 0.68f, size.height * 0.95f,
                    size.width * 0.60f, size.height * 0.85f,
                    size.width * 0.53f, size.height * 0.89f
                )
                cubicTo(
                    size.width * 0.48f, size.height * 0.84f,
                    size.width * 0.46f, size.height * 0.76f,
                    size.width * 0.51f, size.height * 0.70f
                )
                close()
            },
            black.copy(alpha = 0.16f * strength)
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.67f, size.height * 0.79f)
                cubicTo(
                    size.width * 0.73f, size.height * 0.75f,
                    size.width * 0.79f, size.height * 0.83f,
                    size.width * 0.85f, size.height * 0.79f
                )
                cubicTo(
                    size.width * 0.91f, size.height * 0.76f,
                    size.width * 0.97f, size.height * 0.84f,
                    size.width, size.height * 0.89f
                )
                lineTo(size.width, size.height)
                lineTo(size.width * 0.63f, size.height)
                cubicTo(
                    size.width * 0.59f, size.height * 0.91f,
                    size.width * 0.61f, size.height * 0.84f,
                    size.width * 0.67f, size.height * 0.79f
                )
                close()
            },
            darkRed.copy(alpha = 0.15f * strength)
        )
    }
}
