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
    val charcoal = Color(0xFF211D20)
    val lockInRed = Color(0xFFB33E47)
    val deepRed = Color(0xFF762631)
    val face = Color(0xFFFFF1F2)
    val highlight = Color(0xFFF17A82)

    Canvas(modifier = modifier) {
        val unit = size.minDimension
        val flame = Path().apply {
            moveTo(size.width * 0.54f, size.height * 0.04f)
            cubicTo(
                size.width * 0.63f, size.height * 0.20f,
                size.width * 0.82f, size.height * 0.29f,
                size.width * 0.84f, size.height * 0.52f
            )
            cubicTo(
                size.width * 0.88f, size.height * 0.76f,
                size.width * 0.72f, size.height * 0.95f,
                size.width * 0.51f, size.height * 0.97f
            )
            cubicTo(
                size.width * 0.29f, size.height * 0.96f,
                size.width * 0.14f, size.height * 0.80f,
                size.width * 0.17f, size.height * 0.58f
            )
            cubicTo(
                size.width * 0.19f, size.height * 0.43f,
                size.width * 0.30f, size.height * 0.34f,
                size.width * 0.37f, size.height * 0.21f
            )
            cubicTo(
                size.width * 0.39f, size.height * 0.34f,
                size.width * 0.45f, size.height * 0.39f,
                size.width * 0.51f, size.height * 0.41f
            )
            cubicTo(
                size.width * 0.55f, size.height * 0.31f,
                size.width * 0.57f, size.height * 0.17f,
                size.width * 0.54f, size.height * 0.04f
            )
            close()
        }
        drawPath(flame, lockInRed)

        val innerFlame = Path().apply {
            moveTo(size.width * 0.37f, size.height * 0.28f)
            cubicTo(
                size.width * 0.36f, size.height * 0.43f,
                size.width * 0.29f, size.height * 0.49f,
                size.width * 0.29f, size.height * 0.65f
            )
            cubicTo(
                size.width * 0.29f, size.height * 0.80f,
                size.width * 0.39f, size.height * 0.89f,
                size.width * 0.51f, size.height * 0.91f
            )
            cubicTo(
                size.width * 0.43f, size.height * 0.80f,
                size.width * 0.42f, size.height * 0.68f,
                size.width * 0.48f, size.height * 0.57f
            )
            cubicTo(
                size.width * 0.40f, size.height * 0.50f,
                size.width * 0.36f, size.height * 0.41f,
                size.width * 0.37f, size.height * 0.28f
            )
            close()
        }
        drawPath(innerFlame, deepRed)

        drawOval(
            color = face,
            topLeft = Offset(size.width * 0.27f, size.height * 0.39f),
            size = Size(size.width * 0.50f, size.height * 0.39f)
        )
        drawLine(
            color = charcoal,
            start = Offset(size.width * 0.36f, size.height * 0.55f),
            end = Offset(size.width * 0.46f, size.height * 0.59f),
            strokeWidth = unit * 0.055f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = charcoal,
            start = Offset(size.width * 0.68f, size.height * 0.55f),
            end = Offset(size.width * 0.58f, size.height * 0.59f),
            strokeWidth = unit * 0.055f,
            cap = StrokeCap.Round
        )
        drawArc(
            color = charcoal,
            startAngle = 22f,
            sweepAngle = 136f,
            useCenter = false,
            topLeft = Offset(size.width * 0.42f, size.height * 0.63f),
            size = Size(size.width * 0.20f, size.height * 0.10f),
            style = Stroke(width = unit * 0.043f, cap = StrokeCap.Round)
        )
        drawCircle(
            color = highlight,
            radius = unit * 0.045f,
            center = Offset(size.width * 0.67f, size.height * 0.29f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.34f),
            radius = unit * 0.025f,
            center = Offset(size.width * 0.66f, size.height * 0.28f)
        )
    }
}
