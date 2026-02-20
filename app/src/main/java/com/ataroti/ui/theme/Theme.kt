package com.ataroti.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AtarotiColors = darkColorScheme(
    background = Color(0xFF141414),
    surface = Color(0xFF1D1D1D),
    primary = Color(0xFFE8E0CF),
    onBackground = Color(0xFFE8E0CF),
    onSurface = Color(0xFFE8E0CF)
)

@Composable
fun AtarotiTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AtarotiColors, content = content)
}
