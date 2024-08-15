package com.mj.core.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val scheme = lightColorScheme(
    primary = green_500,
    secondary = green_A400,
    tertiary = green_A700,
)

@Composable
fun BargainPriceTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = scheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = white.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}