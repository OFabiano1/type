package app.type.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.editor.EditorState
import app.type.theme.LocalTypePalette
import app.type.theme.TypeFonts

@Composable
fun Editor(state: EditorState) {
    val palette = LocalTypePalette.current
    val text by state.text.collectAsState()
    val scroll = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier.fillMaxSize().imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))
        Box(Modifier.widthIn(max = 720.dp).fillMaxWidth().padding(horizontal = 32.dp)) {
            BasicTextField(
                value = text,
                onValueChange = state::updateText,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color = palette.text,
                    fontSize = 17.sp,
                    lineHeight = 28.sp,
                    fontFamily = TypeFonts.JetBrainsMono,
                ),
                cursorBrush = SolidColor(palette.text),
            )
            if (text.isEmpty()) {
                Text(
                    text = "Enter your text...",
                    color = palette.textDim.copy(alpha = 0.4f),
                    fontSize = 17.sp,
                    fontFamily = TypeFonts.JetBrainsMono,
                )
            }
        }
    }
}
