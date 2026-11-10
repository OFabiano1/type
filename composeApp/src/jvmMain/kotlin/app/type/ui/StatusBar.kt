package app.type.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.editor.EditorState
import app.type.editor.Stats
import app.type.theme.LocalTypePalette
import app.type.theme.Motion
import app.type.theme.TypeFonts

private val ErrorColor = Color(0xFFD9786A)

@Composable
fun StatusBar(state: EditorState) {
    val palette = LocalTypePalette.current
    val text by state.text.collectAsState()
    val status by state.status.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(palette.background)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${Stats.countWords(text)} palavras",
                color = palette.textDim,
                fontSize = 12.sp,
                fontFamily = TypeFonts.JetBrainsMono,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "${Stats.countChars(text)} caracteres",
                color = palette.textDim,
                fontSize = 12.sp,
                fontFamily = TypeFonts.JetBrainsMono,
            )
        }
        val dot by animateColorAsState(
            when (status) {
                is EditorState.SaveStatus.Idle -> palette.textDim.copy(alpha = 0f)
                is EditorState.SaveStatus.Saving -> palette.textDim
                is EditorState.SaveStatus.Saved -> palette.accent
                is EditorState.SaveStatus.Error -> ErrorColor
            },
            Motion.spec(Motion.SAVE),
            label = "save-dot",
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Crossfade(statusLabel(status), animationSpec = Motion.spec(Motion.SAVE), label = "save-label") { label ->
                Text(
                    text = label,
                    color = palette.textDim,
                    fontSize = 12.sp,
                    fontFamily = TypeFonts.JetBrainsMono,
                )
            }
            Spacer(Modifier.width(8.dp))
            Spacer(Modifier.size(6.dp).background(dot, CircleShape))
        }
    }
}

private fun statusLabel(status: EditorState.SaveStatus): String = when (status) {
    is EditorState.SaveStatus.Idle -> ""
    is EditorState.SaveStatus.Saving -> "salvando…"
    is EditorState.SaveStatus.Saved -> EditorState.savedLabel(status.at)
    is EditorState.SaveStatus.Error -> "erro ao salvar"
}
