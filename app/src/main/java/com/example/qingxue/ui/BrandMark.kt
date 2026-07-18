package com.example.qingxue.ui

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
internal fun BrandMark(modifier: Modifier = Modifier) {
    val lockInRed = Color(0xFFE0273A)
    val deepRed = Color(0xFF8E1726)
    val letterColor = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier) {
        val mirroredFShadow = Path().apply {
            moveTo(size.width * 0.11f, size.height * 0.22f)
            lineTo(size.width * 0.52f, size.height * 0.22f)
            lineTo(size.width * 0.52f, size.height * 0.90f)
            lineTo(size.width * 0.32f, size.height * 0.90f)
            lineTo(size.width * 0.32f, size.height * 0.65f)
            lineTo(size.width * 0.11f, size.height * 0.65f)
            lineTo(size.width * 0.23f, size.height * 0.50f)
            lineTo(size.width * 0.32f, size.height * 0.50f)
            lineTo(size.width * 0.32f, size.height * 0.38f)
            lineTo(size.width * 0.11f, size.height * 0.38f)
            lineTo(size.width * 0.23f, size.height * 0.22f)
            close()
        }
        drawPath(mirroredFShadow, deepRed)

        val mirroredF = Path().apply {
            moveTo(size.width * 0.07f, size.height * 0.18f)
            lineTo(size.width * 0.48f, size.height * 0.18f)
            lineTo(size.width * 0.48f, size.height * 0.86f)
            lineTo(size.width * 0.28f, size.height * 0.86f)
            lineTo(size.width * 0.28f, size.height * 0.61f)
            lineTo(size.width * 0.07f, size.height * 0.61f)
            lineTo(size.width * 0.19f, size.height * 0.46f)
            lineTo(size.width * 0.28f, size.height * 0.46f)
            lineTo(size.width * 0.28f, size.height * 0.34f)
            lineTo(size.width * 0.07f, size.height * 0.34f)
            lineTo(size.width * 0.19f, size.height * 0.18f)
            close()
        }
        drawPath(mirroredF, lockInRed)

        val letterL = Path().apply {
            moveTo(size.width * 0.51f, size.height * 0.18f)
            lineTo(size.width * 0.72f, size.height * 0.18f)
            lineTo(size.width * 0.72f, size.height * 0.68f)
            lineTo(size.width * 0.97f, size.height * 0.68f)
            lineTo(size.width * 0.83f, size.height * 0.86f)
            lineTo(size.width * 0.51f, size.height * 0.86f)
            close()
        }
        drawPath(letterL, letterColor)
    }
}
