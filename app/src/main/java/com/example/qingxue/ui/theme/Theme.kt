package com.example.qingxue.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class AppAccent(
    val storageKey: String,
    val label: String,
    val previewColor: Color,
    internal val lightPrimary: Color,
    internal val lightContainer: Color,
    internal val lightOnContainer: Color,
    internal val darkPrimary: Color,
    internal val darkContainer: Color,
    internal val darkOnContainer: Color
) {
    GrayPurple(
        storageKey = "gray_purple",
        label = "雾紫",
        previewColor = Color(0xFFA89FB5),
        lightPrimary = Color(0xFF71697E),
        lightContainer = Color(0xFFEAE5EF),
        lightOnContainer = Color(0xFF302B37),
        darkPrimary = Color(0xFFC4B9D0),
        darkContainer = Color(0xFF494252),
        darkOnContainer = Color(0xFFF3EDF7)
    ),
    MistGreen(
        storageKey = "mist_green",
        label = "浅苔",
        previewColor = Color(0xFF9EADA4),
        lightPrimary = Color(0xFF5F746A),
        lightContainer = Color(0xFFE2EBE6),
        lightOnContainer = Color(0xFF29332E),
        darkPrimary = Color(0xFFB8C9BF),
        darkContainer = Color(0xFF3F4D46),
        darkOnContainer = Color(0xFFEDF4F0)
    ),
    SlateBlue(
        storageKey = "slate_blue",
        label = "雾蓝",
        previewColor = Color(0xFF9CAEBB),
        lightPrimary = Color(0xFF607789),
        lightContainer = Color(0xFFE2EBF1),
        lightOnContainer = Color(0xFF28343D),
        darkPrimary = Color(0xFFB8CAD7),
        darkContainer = Color(0xFF3E4D58),
        darkOnContainer = Color(0xFFEDF3F7)
    ),
    DustRose(
        storageKey = "dust_rose",
        label = "月灰",
        previewColor = Color(0xFFA9AAA5),
        lightPrimary = Color(0xFF70716E),
        lightContainer = Color(0xFFE9E9E5),
        lightOnContainer = Color(0xFF30312F),
        darkPrimary = Color(0xFFC5C6C0),
        darkContainer = Color(0xFF484946),
        darkOnContainer = Color(0xFFF2F2EE)
    );

    companion object {
        fun fromStorage(value: String?): AppAccent =
            entries.firstOrNull { it.storageKey == value } ?: MistGreen
    }
}
private fun lightColors(accent: AppAccent): ColorScheme = lightColorScheme(
    primary = accent.lightPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = accent.lightContainer,
    onPrimaryContainer = accent.lightOnContainer,
    secondary = accent.lightPrimary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFECECF0),
    onSecondaryContainer = Color(0xFF242527),
    tertiary = Color(0xFF5F6265),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE4E4E8),
    onTertiaryContainer = Color(0xFF202224),
    background = Color(0xFFF7F7F9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFECECF0),
    onBackground = Color(0xFF1B1D1F),
    onSurface = Color(0xFF1B1D1F),
    outline = Color(0xFF747679),
    outlineVariant = Color(0xFFCECED4)
)

private fun darkColors(accent: AppAccent): ColorScheme = darkColorScheme(
    primary = accent.darkPrimary,
    onPrimary = Color(0xFF252229),
    primaryContainer = accent.darkContainer,
    onPrimaryContainer = accent.darkOnContainer,
    secondary = accent.darkPrimary,
    onSecondary = Color(0xFF252229),
    secondaryContainer = Color(0xFF303235),
    onSecondaryContainer = Color(0xFFF1F0E9),
    tertiary = Color(0xFFB7B8B5),
    onTertiary = Color(0xFF252729),
    tertiaryContainer = Color(0xFF3A3C3F),
    onTertiaryContainer = Color(0xFFF1F0E9),
    background = Color(0xFF111113),
    surface = Color(0xFF1B1A1C),
    surfaceVariant = Color(0xFF302D30),
    onBackground = Color(0xFFF1F0E9),
    onSurface = Color(0xFFF1F0E9),
    outline = Color(0xFF929491),
    outlineVariant = Color(0xFF454044)
)

private val AppTypography = Typography(
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )
)

@Composable
fun QingXueTheme(
    accent: AppAccent = AppAccent.MistGreen,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColors(accent) else lightColors(accent),
        typography = AppTypography,
        content = content
    )
}
