package app.type.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import app.type.editor.Lists

// Estilo só visual (o texto salvo não muda): 1ª linha vira título grande, marcador de lista ganha o acento.
class NoteStyle(
    private val titleSize: TextUnit,
    private val titleLine: TextUnit,
    private val accent: Color,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val t = text.text
        val b = AnnotatedString.Builder(t)
        var pos = 0
        var titleDone = false
        for (line in t.split('\n')) {
            if (!titleDone && line.isNotBlank()) {
                titleDone = true
                b.addStyle(SpanStyle(fontSize = titleSize), pos, pos + line.length)
                b.addStyle(ParagraphStyle(lineHeight = titleLine), pos, minOf(pos + line.length + 1, t.length))
            } else {
                Lists.MARKER.find(line)?.let { m ->
                    val end = pos + m.value.length - 1 // sem o espaço final
                    b.addStyle(SpanStyle(color = accent), pos + m.groupValues[1].length, end)
                }
            }
            pos += line.length + 1
        }
        return TransformedText(b.toAnnotatedString(), OffsetMapping.Identity)
    }
}
