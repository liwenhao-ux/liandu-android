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
    val letterColor = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier) {
        val fStem = Path().apply {
            moveTo(size.width * 0.53f, size.height * 0.11f)
            lineTo(size.width * 0.63f, size.height * 0.11f)
            lineTo(size.width * 0.35f, size.height * 0.91f)
            lineTo(size.width * 0.25f, size.height * 0.91f)
            close()
        }
        drawPath(fStem, lockInRed)

        val fTopArm = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.11f)
            lineTo(size.width * 0.63f, size.height * 0.11f)
            lineTo(size.width * 0.59f, size.height * 0.23f)
            lineTo(size.width * 0.14f, size.height * 0.23f)
            close()
        }
        drawPath(fTopArm, lockInRed)

        val fMiddleArm = Path().apply {
            moveTo(size.width * 0.24f, size.height * 0.43f)
            lineTo(size.width * 0.50f, size.height * 0.43f)
            lineTo(size.width * 0.46f, size.height * 0.55f)
            lineTo(size.width * 0.20f, size.height * 0.55f)
            close()
        }
        drawPath(fMiddleArm, lockInRed)

        val letterL = Path().apply {
            moveTo(size.width * 0.67f, size.height * 0.17f)
            lineTo(size.width * 0.77f, size.height * 0.17f)
            lineTo(size.width * 0.55f, size.height * 0.79f)
            lineTo(size.width * 0.87f, size.height * 0.79f)
            lineTo(size.width * 0.83f, size.height * 0.92f)
            lineTo(size.width * 0.41f, size.height * 0.92f)
            close()
        }
        drawPath(letterL, letterColor)
    }
}
