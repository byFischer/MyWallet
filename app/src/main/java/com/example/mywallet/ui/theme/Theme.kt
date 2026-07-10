package com.example.mywallet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColors = lightColorScheme(
    primary = ClaudeOrange,
    onPrimary = Color.White,
    primaryContainer = ClaudeOrangeLight,
    onPrimaryContainer = ClaudeOrangeDark,

    secondary = GoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GoldAccentLight,
    onSecondaryContainer = TextDark,

    tertiary = ClaudeOrangeDark,
    onTertiary = Color.White,

    background = CreamBackground,
    onBackground = TextDark,

    surface = CreamSurface,
    onSurface = TextDark,

    surfaceVariant = CreamCard,
    onSurfaceVariant = TextMuted,

    error = Color(0xFFB3261E),
    onError = Color.White
)

@Composable
fun MyWalletTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}