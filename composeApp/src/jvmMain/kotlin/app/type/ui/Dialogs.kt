package app.type.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.data.Note
import app.type.theme.LocalTypePalette
import app.type.theme.TypeFonts

@Composable
private fun TypeDialog(
    title: String,
    confirm: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    val p = LocalTypePalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = p.surface,
        shape = RoundedCornerShape(10.dp),
        title = { Text(title, color = p.text, fontSize = 14.sp, fontFamily = TypeFonts.JetBrainsMono) },
        text = content,
        confirmButton = { SoftButton(confirm, onConfirm, color = p.accent) },
        dismissButton = { SoftButton("cancelar", onDismiss) },
    )
}

@Composable
fun ConfirmDeleteDialog(note: Note, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val p = LocalTypePalette.current
    TypeDialog("excluir \"${note.title}\"?", "excluir", onConfirm, onDismiss) {
        Text("a nota vai para a Lixeira.", color = p.textDim, fontSize = 12.sp, fontFamily = TypeFonts.JetBrainsMono)
    }
}

@Composable
fun RenameDialog(note: Note, onRename: (String) -> Unit, onDismiss: () -> Unit) {
    val p = LocalTypePalette.current
    var name by remember { mutableStateOf(TextFieldValue(note.title, TextRange(0, note.title.length))) }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    TypeDialog("renomear", "renomear", { onRename(name.text) }, onDismiss) {
        BasicTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            textStyle = TextStyle(color = p.text, fontSize = 13.sp, fontFamily = TypeFonts.JetBrainsMono),
            cursorBrush = SolidColor(p.accent),
            modifier = Modifier
                .focusRequester(focus)
                .onPreviewKeyEvent {
                    val enter = it.type == KeyEventType.KeyDown && it.key == Key.Enter
                    if (enter) onRename(name.text)
                    enter
                }
                .clip(RoundedCornerShape(6.dp))
                .background(p.editor)
                .padding(horizontal = 10.dp, vertical = 8.dp),
        )
    }
}
