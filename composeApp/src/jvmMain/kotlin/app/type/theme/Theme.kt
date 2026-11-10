package app.type.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.type.data.Theme

@Composable
fun TypeTheme(theme: Theme, content: @Composable () -> Unit) {
    val dark = theme == Theme.DARK
    val p = (if (dark) DarkPalette else LightPalette).animated()
    val scheme = if (dark) {
        darkColorScheme(
            primary = p.accent, background = p.background, surface = p.surface,
            onBackground = p.text, onSurface = p.text, onPrimary = p.background,
        )
    } else {
        lightColorScheme(
            primary = p.accent, background = p.background, surface = p.surface,
            onBackground = p.text, onSurface = p.text, onPrimary = p.background,
        )
    }
    CompositionLocalProvider(
        LocalTypePalette provides p,
        LocalTextSelectionColors provides TextSelectionColors(p.accent, p.accent.copy(alpha = 0.3f)),
    ) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
