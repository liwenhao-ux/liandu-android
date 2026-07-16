package com.example.qingxue.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun BrandMark(modifier: Modifier = Modifier) {
    val charcoal = Color(0xFF242225)
    val tacticalRed = Color(0xFF9A5B5B)
    val warmOrange = Color(0xFFD58A5C)
    val face = Color(0xFFFFF0E7)

    Canvas(modifier = modifier) {
        val unit = size.minDimension
        val center = Offset(size.width * 0.5f, size.height * 0.53f)
        val shield = Path().apply {
            moveTo(size.width * 0.50f, size.height * 0.05f)
            lineTo(size.width * 0.88f, size.height * 0.23f)
            lineTo(size.width * 0.82f, size.height * 0.72f)
            quadraticTo(
                size.width * 0.70f,
                size.height * 0.91f,
                size.width * 0.50f,
                size.height * 0.98f
            )
            quadraticTo(
                size.width * 0.30f,
                size.height * 0.91f,
                size.width * 0.18f,
                size.height * 0.72f
            )
            lineTo(size.width * 0.12f, size.height * 0.23f)
            close()
        }
        drawPath(shield, tacticalRed)
        drawArc(
            color = warmOrange,
            startAngle = 204f,
            sweepAngle = 112f,
            useCenter = false,
            topLeft = Offset(size.width * 0.20f, size.height * 0.12f),
            size = Size(size.width * 0.60f, size.height * 0.62f),
            style = Stroke(width = unit * 0.075f, cap = StrokeCap.Round)
        )
        drawOval(
            color = face,
            topLeft = Offset(size.width * 0.25f, size.height * 0.28f),
            size = Size(size.width * 0.50f, size.height * 0.48f)
        )
        drawLine(
            color = charcoal,
            start = Offset(size.width * 0.34f, size.height * 0.48f),
            end = Offset(size.width * 0.45f, size.height * 0.52f),
            strokeWidth = unit * 0.06f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = charcoal,
            start = Offset(size.width * 0.66f, size.height * 0.48f),
            end = Offset(size.width * 0.55f, size.height * 0.52f),
            strokeWidth = unit * 0.06f,
            cap = StrokeCap.Round
        )
        drawArc(
            color = charcoal,
            startAngle = 24f,
            sweepAngle = 132f,
            useCenter = false,
            topLeft = Offset(size.width * 0.39f, size.height * 0.57f),
            size = Size(size.width * 0.22f, size.height * 0.12f),
            style = Stroke(width = unit * 0.048f, cap = StrokeCap.Round)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.32f),
            radius = unit * 0.045f,
            center = Offset(size.width * 0.29f, size.height * 0.28f)
        )
        drawCircle(
            color = tacticalRed,
            radius = unit * 0.055f,
            center = center.copy(y = size.height * 0.15f)
        )
    }
}
