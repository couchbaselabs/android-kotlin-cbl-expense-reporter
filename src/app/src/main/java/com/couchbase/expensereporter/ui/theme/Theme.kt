package com.couchbase.expensereporter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Red600,
    primaryVariant = Red900,
    secondary = Teal200,
    onPrimary = BlackTheme,
    background = BackgroundDark,
    secondaryVariant = Teal200,
)

private val LightColorPalette = lightColors(
    primary = Red600,
    primaryVariant = Red900,
    secondary = Teal200,
    onPrimary = WhiteTheme,
    background = BackgroundLight,
    secondaryVariant = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ExpenseReporterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}