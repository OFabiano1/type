package app.type.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Dark = paleta do spec TYPE (fundo #111111, superfícies #191919, texto #E5E5E5).
// Light = mesmos neutros invertidos; interação derivada por alpha do texto.
@Immutable
data class TypePalette(
    val background: Color,
    val surface: Color,
    val text: Color,
    val textDim: Color,
    val hover: Color,
    val divider: Color,
)

val DarkPalette = TypePalette(
    background = Color(0xFF111111),
    surface = Color(0xFF191919),
    text = Color(0xFFE5E5E5),
    textDim = Color(0x99E5E5E5),
    hover = Color(0x14FFFFFF),
    divider = Color(0x22FFFFFF),
)

val LightPalette = TypePalette(
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFEFEFEF),
    text = Color(0xFF1A1A1A),
    textDim = Color(0x991A1A1A),
    hover = Color(0x14000000),
    divider = Color(0x22000000),
)

val LocalTypePalette = staticCompositionLocalOf { DarkPalette }
