package com.example.qingxue.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal enum class CompanionMood {
    Cheer,
    Focus,
    Celebrate
}

@Composable
internal fun StudyCompanion(
    modifier: Modifier = Modifier,
    mood: CompanionMood = CompanionMood.Cheer
) {
    val primary = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onSurface
    val skin = Color(0xFFF1C7B1)
    val paper = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier) {
        val unit = size.minDimension / 100f
        val centerX = size.width * 0.52f

        // Manga motion lines keep the illustration lively without adding another card.
        repeat(4) { index ->
            val y = (18 + index * 16) * unit
            drawLine(
                color = primary.copy(alpha = 0.16f - index * 0.02f),
                start = Offset(4 * unit, y),
                end = Offset((22 - index * 2) * unit, y - 5 * unit),
                strokeWidth = 1.5f * unit,
                cap = StrokeCap.Round
            )
        }

        val hoodie = Path().apply {
            moveTo(centerX - 31 * unit, 92 * unit)
            quadraticTo(centerX - 29 * unit, 61 * unit, centerX - 19 * unit, 56 * unit)
            quadraticTo(centerX, 48 * unit, centerX + 20 * unit, 57 * unit)
            quadraticTo(centerX + 32 * unit, 67 * unit, centerX + 34 * unit, 96 * unit)
            close()
        }
        drawPath(hoodie, ink.copy(alpha = 0.88f))
        drawLine(
            primary,
            Offset(centerX - 17 * unit, 62 * unit),
            Offset(centerX - 22 * unit, 91 * unit),
            3.5f * unit,
            StrokeCap.Round
        )

        drawOval(
            color = skin,
            topLeft = Offset(centerX - 21 * unit, 20 * unit),
            size = Size(42 * unit, 43 * unit)
        )

        val hair = Path().apply {
            moveTo(centerX - 24 * unit, 35 * unit)
            quadraticTo(centerX - 23 * unit, 12 * unit, centerX + 2 * unit, 10 * unit)
            quadraticTo(centerX + 25 * unit, 12 * unit, centerX + 24 * unit, 39 * unit)
            lineTo(centerX + 16 * unit, 31 * unit)
            lineTo(centerX + 11 * unit, 41 * unit)
            lineTo(centerX + 4 * unit, 29 * unit)
            lineTo(centerX - 3 * unit, 40 * unit)
            lineTo(centerX - 10 * unit, 28 * unit)
            lineTo(centerX - 17 * unit, 39 * unit)
            close()
        }
        drawPath(hair, Color(0xFF3A2528))

        val eyeY = 43 * unit
        if (mood == CompanionMood.Celebrate) {
            drawArc(
                ink,
                200f,
                140f,
                false,
                Offset(centerX - 13 * unit, eyeY - 2 * unit),
                Size(8 * unit, 6 * unit),
                style = Stroke(1.8f * unit, cap = StrokeCap.Round)
            )
            drawArc(
                ink,
                200f,
                140f,
                false,
                Offset(centerX + 5 * unit, eyeY - 2 * unit),
                Size(8 * unit, 6 * unit),
                style = Stroke(1.8f * unit, cap = StrokeCap.Round)
            )
        } else {
            drawOval(ink, Offset(centerX - 11 * unit, eyeY), Size(3.2f * unit, 5 * unit))
            drawOval(ink, Offset(centerX + 8 * unit, eyeY), Size(3.2f * unit, 5 * unit))
        }
        val blush = Color(0xFFC86A72)
        drawCircle(blush.copy(alpha = 0.30f), 2.2f * unit, Offset(centerX - 14 * unit, 51 * unit))
        drawCircle(blush.copy(alpha = 0.30f), 2.2f * unit, Offset(centerX + 14 * unit, 51 * unit))
        drawArc(
            color = ink.copy(alpha = 0.78f),
            startAngle = if (mood == CompanionMood.Focus) 25f else 8f,
            sweepAngle = if (mood == CompanionMood.Focus) 130f else 165f,
            useCenter = false,
            topLeft = Offset(centerX - 5 * unit, 51 * unit),
            size = Size(10 * unit, 6 * unit),
            style = Stroke(1.5f * unit, cap = StrokeCap.Round)
        )

        // Notebook and pen make the character read as a study companion at small sizes.
        drawRoundRect(
            color = paper,
            topLeft = Offset(centerX - 24 * unit, 73 * unit),
            size = Size(48 * unit, 25 * unit),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3 * unit)
        )
        drawLine(
            ink.copy(alpha = 0.35f),
            Offset(centerX, 75 * unit),
            Offset(centerX, 96 * unit),
            unit
        )
        drawLine(
            primary,
            Offset(centerX + 25 * unit, 68 * unit),
            Offset(centerX + 14 * unit, 88 * unit),
            2.2f * unit,
            StrokeCap.Round
        )

        drawCircle(blush, 2.8f * unit, Offset(centerX + 24 * unit, 19 * unit))
        drawLine(
            blush,
            Offset(centerX + 20 * unit, 19 * unit),
            Offset(centerX + 28 * unit, 19 * unit),
            1.2f * unit
        )
    }
}

@Composable
internal fun CompanionGreeting(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth().height(126.dp)) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart).padding(end = 116.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Column {
                Text(
                    text = title,
                    fontSize = 21.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    modifier = Modifier.padding(top = 6.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
        StudyCompanion(
            mood = CompanionMood.Cheer,
            modifier = Modifier.align(Alignment.BottomEnd).size(122.dp)
        )
    }
}

@Composable
internal fun CompanionEmptyState(
    text: String,
    modifier: Modifier = Modifier,
    mood: CompanionMood = CompanionMood.Focus
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StudyCompanion(modifier = Modifier.size(64.dp), mood = mood)
        Text(
            text = text,
            modifier = Modifier.padding(start = 10.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}
