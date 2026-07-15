package com.example.qingxue.ui

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun BrandMark(modifier: Modifier = Modifier) {
    val ink = MaterialTheme.colorScheme.onSurface
    val body = Color(0xFFE0A09A)
    val leaf = Color(0xFF8FA38C)
    val face = Color(0xFFFFF8EE)

    Canvas(modifier = modifier) {
        val unit = size.minDimension
        val center = Offset(size.width * 0.5f, size.height * 0.57f)

        drawRoundRect(
            color = leaf,
            topLeft = Offset(size.width * 0.42f, size.height * 0.10f),
            size = Size(size.width * 0.16f, size.height * 0.18f),
            cornerRadius = CornerRadius(unit * 0.05f)
        )
        drawPath(
            path = Path().apply {
                moveTo(size.width * 0.50f, size.height * 0.23f)
                cubicTo(
                    size.width * 0.36f, size.height * 0.10f,
                    size.width * 0.25f, size.height * 0.18f,
                    size.width * 0.38f, size.height * 0.31f
                )
                close()
            },
            color = leaf
        )
        drawCircle(body, radius = unit * 0.38f, center = center)
        drawCircle(face, radius = unit * 0.285f, center = center)
        drawCircle(ink, radius = unit * 0.035f, center = Offset(size.width * 0.41f, size.height * 0.55f))
        drawCircle(ink, radius = unit * 0.035f, center = Offset(size.width * 0.59f, size.height * 0.55f))
        drawArc(
            color = ink,
            startAngle = 18f,
            sweepAngle = 144f,
            useCenter = false,
            topLeft = Offset(size.width * 0.38f, size.height * 0.56f),
            size = Size(size.width * 0.24f, size.height * 0.18f),
            style = Stroke(width = unit * 0.045f, cap = StrokeCap.Round)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.45f),
            radius = unit * 0.045f,
            center = Offset(size.width * 0.34f, size.height * 0.39f)
        )
    }
}