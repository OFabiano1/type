package app.type.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Soft minimalism 0.2: tons quentes fora do preto absoluto; o roxo é só detalhe de identidade.
@Immutable
data class TypePalette(
    val background: Color,
    val sidebar: Color,
    val editor: Color,
    val surface: Color,
    val hover: Color,
    val selection: Color,
    val text: Color,
    val textDim: Color,
    val accent: Color,
    val divider: Color,
)

val DarkPalette = TypePalette(
    background = Color(0xFF111110),
    sidebar = Color(0xFF181817),
    editor = Color(0xFF141413),
    surface = Color(0xFF20201E),
    hover = Color(0xFF252523),
    selection = Color(0xFF292927),
    text = Color(0xFFE7E5E1),
    textDim = Color(0xFF85837E),
    accent = Color(0xFFB5A6D9),
    divider = Color(0x14FFFFFF),
)

// textDim mais escuro que o #85837E do briefing: nele o texto pequeno ficava abaixo de 4.5:1 sobre o claro.
val LightPalette = TypePalette(
    background = Color(0xFFF6F5F2),
    sidebar = Color(0xFFEFEEE9),
    editor = Color(0xFFFAF9F6),
    surface = Color(0xFFEAE9E3),
    hover = Color(0xFFE4E3DC),
    selection = Color(0xFFDDDBD3),
    text = Color(0xFF292824),
    textDim = Color(0xFF6F6D68),
    accent = Color(0xFF8272A8),
    divider = Color(0x14000000),
)

val LocalTypePalette = staticCompositionLocalOf { DarkPalette }

// Troca de tema sem flash: cada cor desliza até o alvo.
@Composable
fun TypePalette.animated(): TypePalette {
    val spec = Motion.spec<Color>(Motion.PANEL)
    @Composable fun a(c: Color): Color = animateColorAsState(c, spec, label = "palette").value
    return TypePalette(
        a(background), a(sidebar), a(editor), a(surface), a(hover),
        a(selection), a(text), a(textDim), a(accent), a(divider),
    )
}
