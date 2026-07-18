package com.example.qingxue.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun BrandMark(modifier: Modifier = Modifier) {
    val red = Color(0xFFE0273A)
    val white = Color(0xFFFFF4F5)
    val black = Color(0xFF171719)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val unit = size.minDimension

        drawPath(
            Path().apply {
                moveTo(w * 0.50f, h * 0.11f)
                lineTo(w * 0.67f, h * 0.11f)
                lineTo(w * 0.36f, h * 0.91f)
                lineTo(w * 0.20f, h * 0.91f)
                close()
            },
            red
        )
        drawPath(
            Path().apply {
                moveTo(w * 0.13f, h * 0.11f)
                lineTo(w * 0.67f, h * 0.11f)
                lineTo(w * 0.61f, h * 0.27f)
                lineTo(w * 0.18f, h * 0.27f)
                close()
            },
            red
        )
        drawPath(
            Path().apply {
                moveTo(w * 0.21f, h * 0.40f)
                lineTo(w * 0.51f, h * 0.40f)
                lineTo(w * 0.45f, h * 0.56f)
                lineTo(w * 0.27f, h * 0.56f)
                close()
            },
            red
        )
        drawPath(
            Path().apply {
                moveTo(w * 0.70f, h * 0.11f)
                lineTo(w * 0.86f, h * 0.11f)
                lineTo(w * 0.57f, h * 0.76f)
                lineTo(w * 0.89f, h * 0.76f)
                lineTo(w * 0.83f, h * 0.91f)
                lineTo(w * 0.39f, h * 0.91f)
                close()
            },
            white
        )

        val center = Offset(w * 0.50f, h * 0.50f)
        drawCircle(black, radius = unit * 0.18f, center = center)
        drawCircle(white, radius = unit * 0.15f, center = center, style = Stroke(unit * 0.020f))
        drawCircle(white, radius = unit * 0.115f, center = center, style = Stroke(unit * 0.014f))

        val crosshairs = listOf(
            Offset(w * 0.50f, h * 0.31f) to Offset(w * 0.50f, h * 0.39f),
            Offset(w * 0.50f, h * 0.61f) to Offset(w * 0.50f, h * 0.69f),
            Offset(w * 0.31f, h * 0.50f) to Offset(w * 0.39f, h * 0.50f),
            Offset(w * 0.61f, h * 0.50f) to Offset(w * 0.69f, h * 0.50f)
        )
        crosshairs.forEach { (start, end) ->
            drawLine(black, start, end, strokeWidth = unit * 0.052f, cap = StrokeCap.Butt)
            drawLine(white, start, end, strokeWidth = unit * 0.026f, cap = StrokeCap.Butt)
        }

        drawPath(
            Path().apply {
                moveTo(w * 0.45f, h * 0.49f)
                lineTo(w * 0.55f, h * 0.49f)
                lineTo(w * 0.55f, h * 0.59f)
                lineTo(w * 0.45f, h * 0.59f)
                close()
            },
            white
        )
        drawPath(
            Path().apply {
                moveTo(w * 0.47f, h * 0.49f)
                lineTo(w * 0.47f, h * 0.46f)
                cubicTo(w * 0.47f, h * 0.41f, w * 0.53f, h * 0.41f, w * 0.53f, h * 0.46f)
                lineTo(w * 0.53f, h * 0.49f)
            },
            white,
            style = Stroke(width = unit * 0.022f, cap = StrokeCap.Round)
        )
        drawCircle(black, radius = unit * 0.012f, center = Offset(w * 0.50f, h * 0.53f))
        drawLine(
            black,
            Offset(w * 0.50f, h * 0.53f),
            Offset(w * 0.50f, h * 0.56f),
            strokeWidth = unit * 0.018f,
            cap = StrokeCap.Round
        )
    }
}
