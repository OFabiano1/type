package app.type.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.editor.EditorState
import app.type.editor.Stats
import app.type.theme.LocalTypePalette
import app.type.theme.TypeFonts

@Composable
fun StatusBar(state: EditorState) {
    val palette = LocalTypePalette.current
    val text by state.text.collectAsState()
    val status by state.status.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(palette.surface)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val words = Stats.countWords(text)
        val chars = Stats.countChars(text)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$words palavras",
                color = palette.textDim,
                fontSize = 12.sp,
                fontFamily = TypeFonts.JetBrainsMono,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "$chars caracteres",
                color = palette.textDim,
                fontSize = 12.sp,
                fontFamily = TypeFonts.JetBrainsMono,
            )
        }
        Text(
            text = statusLabel(status),
            color = palette.textDim,
            fontSize = 12.sp,
            fontFamily = TypeFonts.JetBrainsMono,
        )
    }
}

private fun statusLabel(status: EditorState.SaveStatus): String = when (status) {
    is EditorState.SaveStatus.Idle -> ""
    is EditorState.SaveStatus.Saving -> "salvando…"
    is EditorState.SaveStatus.Saved -> EditorState.savedLabel(status.at)
    is EditorState.SaveStatus.Error -> "erro ao salvar"
}
