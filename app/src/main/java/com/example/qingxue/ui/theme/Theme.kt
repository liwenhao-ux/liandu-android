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
        label = "低饱和红",
        previewColor = Color(0xFFB33E47),
        lightPrimary = Color(0xFFA2353F),
        lightContainer = Color(0xFFF2D8DA),
        lightOnContainer = Color(0xFF35191C),
        darkPrimary = Color(0xFFDF626A),
        darkContainer = Color(0xFF52282D),
        darkOnContainer = Color(0xFFFFECEE)
    ),
    MistGreen(
        storageKey = "mist_green",
        label = "雾绿",
        previewColor = Color(0xFF82948A),
        lightPrimary = Color(0xFF66766E),
        lightContainer = Color(0xFFDCE6E0),
        lightOnContainer = Color(0xFF25312B),
        darkPrimary = Color(0xFFA5B5AC),
        darkContainer = Color(0xFF414F48),
        darkOnContainer = Color(0xFFEAF2EE)
    ),
    SlateBlue(
        storageKey = "slate_blue",
        label = "灰蓝",
        previewColor = Color(0xFF81909A),
        lightPrimary = Color(0xFF65737D),
        lightContainer = Color(0xFFDDE4E8),
        lightOnContainer = Color(0xFF263039),
        darkPrimary = Color(0xFFA5B2BA),
        darkContainer = Color(0xFF424E56),
        darkOnContainer = Color(0xFFECF1F4)
    ),
    DustRose(
        storageKey = "dust_rose",
        label = "暮粉",
        previewColor = Color(0xFF9A8288),
        lightPrimary = Color(0xFF806B70),
        lightContainer = Color(0xFFEADDE0),
        lightOnContainer = Color(0xFF35262A),
        darkPrimary = Color(0xFFB8A2A7),
        darkContainer = Color(0xFF554247),
        darkOnContainer = Color(0xFFF4E9EB)
    );

    companion object {
        fun fromStorage(value: String?): AppAccent =
            entries.firstOrNull { it.storageKey == value } ?: MistGreen
    }
}

enum class AppVisualStyle(val storageKey: String) {
    Standard("standard"),
    Tactical("tactical");

    companion object {
        fun fromStorage(value: String?): AppVisualStyle =
            entries.firstOrNull { it.storageKey == value } ?: Standard
    }
}

val LocalAppVisualStyle = staticCompositionLocalOf { AppVisualStyle.Standard }

private fun lightColors(accent: AppAccent): ColorScheme = lightColorScheme(
    primary = accent.lightPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = accent.lightContainer,
    onPrimaryContainer = accent.lightOnContainer,
    secondary = accent.lightPrimary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE7E7E2),
    onSecondaryContainer = Color(0xFF242527),
    tertiary = Color(0xFF5F6265),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD9DAD6),
    onTertiaryContainer = Color(0xFF202224),
    background = Color(0xFFF3F1EE),
    surface = Color(0xFFFBF9F6),
    surfaceVariant = Color(0xFFE5E5DF),
    onBackground = Color(0xFF1B1D1F),
    onSurface = Color(0xFF1B1D1F),
    outline = Color(0xFF747679),
    outlineVariant = Color(0xFFC8C8C2)
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
    visualStyle: AppVisualStyle = AppVisualStyle.Standard,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAppVisualStyle provides visualStyle) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColors(accent) else lightColors(accent),
            typography = AppTypography,
            content = content
        )
    }
}