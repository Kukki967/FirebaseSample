package com.kukki.firebasesample.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = BlueBayoux,
    secondary = Norway,
    background = Color.White,
    onPrimary = BlueBayoux,
    onSecondary = Norway,
    onBackground = Color.Black,
    onSurface = Firefly
)

@Composable
fun FirebaseSampleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        content = content
    )
}