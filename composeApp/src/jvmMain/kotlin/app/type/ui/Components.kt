package app.type.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.theme.Dimens
import app.type.theme.LocalTypePalette
import app.type.theme.Motion
import app.type.theme.TypeFonts

// Botão de texto: hover e pressionado animados, sem mexer no layout.
@Composable
fun SoftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = LocalTypePalette.current.textDim,
) {
    val palette = LocalTypePalette.current
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val bg by animateColorAsState(
        when {
            pressed -> palette.selection
            hovered -> palette.hover
            else -> palette.hover.copy(alpha = 0f)
        },
        Motion.spec(Motion.HOVER),
        label = "btn-bg",
    )
    val fg by animateColorAsState(if (hovered || pressed) palette.text else color, Motion.spec(Motion.HOVER), label = "btn-fg")
    Text(
        text = text,
        color = fg,
        fontSize = 12.sp,
        fontFamily = TypeFonts.JetBrainsMono,
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radius))
            .background(bg)
            .hoverable(source)
            .clickable(interactionSource = source, indication = null, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp),
    )
}
