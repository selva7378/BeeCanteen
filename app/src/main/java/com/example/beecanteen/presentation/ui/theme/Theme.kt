package com.example.beecanteen.presentation.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = CanteenOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBC8),
    onPrimaryContainer = CanteenBrown,
    secondary = CanteenAmber,
    background = CanteenCream,
    surface = CanteenSurface,
    onSurface = CanteenOnSurface,
    outline = CanteenOutline,
    surfaceVariant = Color(0xFFF3EBE5)
)

private val DarkColorScheme = darkColorScheme(
    primary = CanteenAmber,
    onPrimary = Color(0xFF3E1F00),
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface
)

@Composable
fun BeeCanteenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CanteenTypography,
        shapes = CanteenShapes,
        content = content
    )
}