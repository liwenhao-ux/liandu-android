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
            moveTo(size.width * 0.51f, size.height * 0.13f)
            lineTo(size.width * 0.68f, size.height * 0.13f)
            lineTo(size.width * 0.27f, size.height * 0.90f)
            lineTo(size.width * 0.10f, size.height * 0.90f)
            close()
        }
        drawPath(fStem, lockInRed)

        val fTopArm = Path().apply {
            moveTo(size.width * 0.07f, size.height * 0.13f)
            lineTo(size.width * 0.68f, size.height * 0.13f)
            lineTo(size.width * 0.59f, size.height * 0.30f)
            lineTo(size.width * 0.02f, size.height * 0.30f)
            close()
        }
        drawPath(fTopArm, lockInRed)

        val fMiddleArm = Path().apply {
            moveTo(size.width * 0.13f, size.height * 0.43f)
            lineTo(size.width * 0.47f, size.height * 0.43f)
            lineTo(size.width * 0.38f, size.height * 0.60f)
            lineTo(size.width * 0.07f, size.height * 0.60f)
            close()
        }
        drawPath(fMiddleArm, lockInRed)

        val letterL = Path().apply {
            moveTo(size.width * 0.76f, size.height * 0.22f)
            lineTo(size.width * 0.93f, size.height * 0.22f)
            lineTo(size.width * 0.58f, size.height * 0.78f)
            lineTo(size.width * 0.99f, size.height * 0.78f)
            lineTo(size.width * 0.90f, size.height * 0.94f)
            lineTo(size.width * 0.33f, size.height * 0.94f)
            close()
        }
        drawPath(letterL, letterColor)
    }
}
