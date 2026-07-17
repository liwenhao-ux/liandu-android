package com.example.qingxue.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
internal fun BrandMark(modifier: Modifier = Modifier) {
    val lockInRed = Color(0xFFD62939)
    val deepRed = Color(0xFF8E1726)
    val iceWhite = Color(0xFFFFF4F5)

    Canvas(modifier = modifier) {
        val shadow = Path().apply {
            moveTo(size.width * 0.13f, size.height * 0.21f)
            lineTo(size.width * 0.53f, size.height * 0.21f)
            lineTo(size.width * 0.65f, size.height * 0.07f)
            lineTo(size.width * 0.95f, size.height * 0.07f)
            lineTo(size.width * 0.79f, size.height * 0.28f)
            lineTo(size.width * 0.62f, size.height * 0.28f)
            lineTo(size.width * 0.52f, size.height * 0.40f)
            lineTo(size.width * 0.82f, size.height * 0.40f)
            lineTo(size.width * 0.67f, size.height * 0.58f)
            lineTo(size.width * 0.53f, size.height * 0.58f)
            lineTo(size.width * 0.53f, size.height * 0.94f)
            lineTo(size.width * 0.31f, size.height * 0.94f)
            lineTo(size.width * 0.31f, size.height * 0.69f)
            lineTo(size.width * 0.07f, size.height * 0.69f)
            lineTo(size.width * 0.21f, size.height * 0.51f)
            lineTo(size.width * 0.38f, size.height * 0.51f)
            lineTo(size.width * 0.48f, size.height * 0.38f)
            lineTo(size.width * 0.13f, size.height * 0.38f)
            close()
        }
        drawPath(shadow, deepRed)

        val mainBlade = Path().apply {
            moveTo(size.width * 0.09f, size.height * 0.16f)
            lineTo(size.width * 0.51f, size.height * 0.16f)
            lineTo(size.width * 0.63f, size.height * 0.03f)
            lineTo(size.width * 0.93f, size.height * 0.03f)
            lineTo(size.width * 0.77f, size.height * 0.24f)
            lineTo(size.width * 0.59f, size.height * 0.24f)
            lineTo(size.width * 0.49f, size.height * 0.36f)
            lineTo(size.width * 0.79f, size.height * 0.36f)
            lineTo(size.width * 0.64f, size.height * 0.54f)
            lineTo(size.width * 0.49f, size.height * 0.54f)
            lineTo(size.width * 0.49f, size.height * 0.90f)
            lineTo(size.width * 0.27f, size.height * 0.90f)
            lineTo(size.width * 0.27f, size.height * 0.64f)
            lineTo(size.width * 0.03f, size.height * 0.64f)
            lineTo(size.width * 0.17f, size.height * 0.46f)
            lineTo(size.width * 0.35f, size.height * 0.46f)
            lineTo(size.width * 0.45f, size.height * 0.34f)
            lineTo(size.width * 0.09f, size.height * 0.34f)
            close()
        }
        drawPath(mainBlade, lockInRed)

        val lockSlash = Path().apply {
            moveTo(size.width * 0.62f, size.height * 0.58f)
            lineTo(size.width * 0.96f, size.height * 0.58f)
            lineTo(size.width * 0.82f, size.height * 0.75f)
            lineTo(size.width * 0.70f, size.height * 0.75f)
            lineTo(size.width * 0.58f, size.height * 0.89f)
            lineTo(size.width * 0.40f, size.height * 0.89f)
            close()
        }
        drawPath(lockSlash, iceWhite)
    }
}
