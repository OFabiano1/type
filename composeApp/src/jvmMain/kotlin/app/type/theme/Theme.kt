package app.type.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.type.data.Theme

@Composable
fun TypeTheme(theme: Theme, content: @Composable () -> Unit) {
    val palette = if (theme == Theme.DARK) DarkPalette else LightPalette
    val scheme = if (theme == Theme.DARK) {
        darkColorScheme(
            background = palette.background,
            surface = palette.surface,
            onBackground = palette.text,
            onSurface = palette.text,
            primary = palette.text,
            onPrimary = palette.background,
        )
    } else {
        lightColorScheme(
            background = palette.background,
            surface = palette.surface,
            onBackground = palette.text,
            onSurface = palette.text,
            primary = palette.text,
            onPrimary = palette.background,
        )
    }
    CompositionLocalProvider(LocalTypePalette provides palette) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
