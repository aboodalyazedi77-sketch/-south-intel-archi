package com.southintel.archive.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val NavyDark = Color(0xFF0B1E3F)
private val NavyDeep = Color(0xFF06122A)
private val Steel = Color(0xFF5A6B85)
private val Cloud = Color(0xFFE6EEF8)
private val Accent = Color(0xFF3F7CAC)
private val Danger = Color(0xFFB23A3A)

private val Dark = darkColorScheme(
    primary = Accent, onPrimary = Color.White,
    secondary = Steel, onSecondary = Color.White,
    background = NavyDeep, onBackground = Cloud,
    surface = NavyDark, onSurface = Cloud,
    surfaceVariant = Color(0xFF132A52), onSurfaceVariant = Color(0xFFB9C7DC),
    error = Danger
)

private val Light = lightColorScheme(
    primary = NavyDark, onPrimary = Color.White,
    secondary = Steel, onSecondary = Color.White,
    background = Color(0xFFF6F8FB), onBackground = NavyDeep,
    surface = Color.White, onSurface = NavyDeep,
    surfaceVariant = Color(0xFFE6EEF8), onSurfaceVariant = Color(0xFF324A6B),
    error = Danger
)

private val ArabicTypography = Typography(
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
)

@Composable
fun ArchiveTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) Dark else Light,
        typography = ArabicTypography,
        content = content
    )
}
