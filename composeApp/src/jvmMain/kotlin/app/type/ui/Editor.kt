package app.type.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.data.FontChoice
import app.type.editor.EditorState
import app.type.editor.Lists
import app.type.theme.LocalTypePalette
import app.type.theme.Motion
import app.type.theme.TypeFonts

data class EditorSettings(
    val font: FontChoice,
    val fontSize: Int,
    val lineHeightPct: Int,
    val width: Int,
)

@Composable
fun Editor(state: EditorState, settings: EditorSettings) {
    val palette = LocalTypePalette.current
    val text by state.text.collectAsState()
    // Estado com cursor: o Enter de lista precisa saber onde o cursor está.
    var field by remember { mutableStateOf(TextFieldValue(text)) }
    LaunchedEffect(text) { if (field.text != text) field = TextFieldValue(text) } // nota trocada/renomeada por fora
    val noteId = state.note.collectAsState().value?.id
    val scroll = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val family = TypeFonts.of(settings.font)
    val size = settings.fontSize.sp
    val line = (settings.fontSize * settings.lineHeightPct / 100f).sp
    val style = remember(palette.accent, settings.fontSize, settings.lineHeightPct) {
        NoteStyle(titleSize = (settings.fontSize * 1.7f).sp, titleLine = (settings.fontSize * 1.7f * 1.3f).sp, accent = palette.accent)
    }

    LaunchedEffect(noteId) { focusRequester.requestFocus() }

    // Fade só ao trocar de nota — nunca enquanto digita.
    val fade = remember(noteId) { Animatable(0f) }
    LaunchedEffect(noteId) { fade.animateTo(1f, Motion.spec(Motion.NOTE)) }

    Column(
        modifier = Modifier.fillMaxSize().background(palette.editor).graphicsLayer { alpha = fade.value },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))
        Box(Modifier.widthIn(max = settings.width.dp).fillMaxWidth().padding(horizontal = 32.dp)) {
            BasicTextField(
                value = field,
                onValueChange = {
                    field = it
                    if (it.text != text) state.updateText(it.text)
                },
                visualTransformation = style,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { e ->
                        val enter = e.type == KeyEventType.KeyDown && e.key == Key.Enter && !e.isShiftPressed
                        val next = if (enter && field.selection.collapsed) Lists.onEnter(field.text, field.selection.start) else null
                        next?.let { (t, c) ->
                            field = TextFieldValue(t, TextRange(c))
                            state.updateText(t)
                        }
                        next != null
                    },
                textStyle = TextStyle(color = palette.text, fontSize = size, lineHeight = line, fontFamily = family),
                cursorBrush = SolidColor(palette.accent),
            )
            val hint by animateFloatAsState(if (text.isEmpty()) 1f else 0f, Motion.spec(Motion.NOTE), label = "placeholder")
            Text(
                text = "comece a escrever...",
                color = palette.textDim.copy(alpha = 0.5f * hint),
                fontSize = size,
                lineHeight = line,
                fontFamily = family,
            )
        }
    }
}
