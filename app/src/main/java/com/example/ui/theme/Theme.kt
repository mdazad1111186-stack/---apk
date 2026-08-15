package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OrgGreenPrimaryDark,
    onPrimary = Color(0xFF003825),
    primaryContainer = OrgGreenContainerDark,
    onPrimaryContainer = OrgGreenPrimaryDark,
    secondary = Color(0xFF4DD0E1),
    onSecondary = Color(0xFF00363A),
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFF80DEEA),
    tertiary = Color(0xFFFFB74D),
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = OrgGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = OrgGreenContainer,
    onPrimaryContainer = OrgGreenOnContainer,
    secondary = OrgTealSecondary,
    onSecondary = Color.White,
    secondaryContainer = OrgTealContainer,
    onSecondaryContainer = OrgTealOnContainer,
    tertiary = OrgGoldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = OrgGoldContainer,
    onTertiaryContainer = OrgGoldOnContainer,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep custom organizational brand theme
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
