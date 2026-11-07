package app.type.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.data.Note
import app.type.theme.LocalTypePalette
import app.type.theme.TypeFonts

@Immutable
data class NoteState(val note: Note, val active: Boolean)

@Composable
fun Sidebar(
    notes: List<Note>,
    activeId: String?,
    query: String,
    onQueryChange: (String) -> Unit,
    onNoteClick: (Note) -> Unit,
    onCreate: () -> Unit,
    onDelete: (Note) -> Unit,
    searchFocus: FocusRequester,
) {
    val palette = LocalTypePalette.current
    Column(
        modifier = Modifier
            .width(230.dp)
            .fillMaxHeight()
            .background(palette.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCreate() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "nova nota",
                color = palette.textDim,
                fontSize = 13.sp,
                fontFamily = TypeFonts.JetBrainsMono,
            )
        }
        Box(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().focusRequester(searchFocus),
                textStyle = TextStyle(color = palette.text, fontSize = 13.sp, fontFamily = TypeFonts.JetBrainsMono),
                cursorBrush = SolidColor(palette.text),
            )
            if (query.isEmpty()) {
                Text(
                    text = "pesquisar...",
                    color = palette.textDim.copy(alpha = 0.4f),
                    fontSize = 13.sp,
                    fontFamily = TypeFonts.JetBrainsMono,
                )
            }
        }
        val filtered = remember(notes, query) { NotesModel.filterNotes(notes, query) }
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), Alignment.Center) {
                Text(
                    text = if (notes.isEmpty()) "sem notas" else "nada encontrado",
                    color = palette.textDim,
                    fontSize = 12.sp,
                    fontFamily = TypeFonts.JetBrainsMono,
                )
            }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(filtered, key = { it.id }) { note ->
                    val interaction = remember { MutableInteractionSource() }
                    val hovered by interaction.collectIsHoveredAsState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (note.id == activeId || hovered) palette.hover else palette.surface)
                            .hoverable(interactionSource = interaction)
                            .clickable { onNoteClick(note) }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = note.title,
                            color = palette.text,
                            fontSize = 13.sp,
                            fontFamily = TypeFonts.JetBrainsMono,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        if (hovered) {
                            Text(
                                text = "✕",
                                color = palette.textDim,
                                fontSize = 12.sp,
                                fontFamily = TypeFonts.JetBrainsMono,
                                modifier = Modifier
                                    .clickable { onDelete(note) }
                                    .padding(start = 8.dp, top = 2.dp, bottom = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
