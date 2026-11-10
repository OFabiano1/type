package app.type.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.data.Note
import app.type.theme.Dimens
import app.type.theme.LocalTypePalette
import app.type.theme.Motion
import app.type.theme.TypeFonts

@Composable
fun Sidebar(
    notes: List<Note>,
    activeId: String?,
    query: String,
    onQueryChange: (String) -> Unit,
    onNoteClick: (Note) -> Unit,
    onCreate: () -> Unit,
    onRename: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    searchFocus: FocusRequester,
) {
    val palette = LocalTypePalette.current
    Column(
        modifier = Modifier
            .width(Dimens.sidebarWidth)
            .fillMaxHeight()
            .background(palette.sidebar)
            .padding(8.dp),
    ) {
        SoftButton("+ nova nota", onCreate, Modifier.fillMaxWidth())
        Box(
            Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.radius))
                .background(palette.surface)
                .padding(horizontal = 10.dp, vertical = 7.dp),
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().focusRequester(searchFocus),
                textStyle = TextStyle(color = palette.text, fontSize = 13.sp, fontFamily = TypeFonts.JetBrainsMono),
                cursorBrush = SolidColor(palette.accent),
            )
            val hint by animateFloatAsState(if (query.isEmpty()) 1f else 0f, Motion.spec(Motion.HOVER), label = "hint")
            Text(
                text = "pesquisar...",
                color = palette.textDim.copy(alpha = 0.6f * hint),
                fontSize = 13.sp,
                fontFamily = TypeFonts.JetBrainsMono,
            )
        }
        val groups = remember(notes, query) { NotesModel.groupByRecency(NotesModel.filterNotes(notes, query)) }
        if (groups.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(
                    text = if (notes.isEmpty()) "sem notas" else "nada encontrado",
                    color = palette.textDim,
                    fontSize = 12.sp,
                    fontFamily = TypeFonts.JetBrainsMono,
                )
            }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                groups.forEach { (label, group) ->
                    item(key = "h-$label") {
                        Text(
                            text = label,
                            color = palette.textDim,
                            fontSize = 11.sp,
                            fontFamily = TypeFonts.JetBrainsMono,
                            modifier = Modifier.padding(start = 8.dp, top = 10.dp, bottom = 4.dp),
                        )
                    }
                    items(group, key = { it.id }) { note ->
                        NoteRow(note, note.id == activeId, { onNoteClick(note) }, { onRename(note) }, { onDelete(note) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteRow(note: Note, active: Boolean, onClick: () -> Unit, onRename: () -> Unit, onDelete: () -> Unit) {
    val palette = LocalTypePalette.current
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val bg by animateColorAsState(
        when {
            active -> palette.selection
            hovered -> palette.hover
            else -> palette.hover.copy(alpha = 0f)
        },
        Motion.spec(if (active) Motion.SELECT else Motion.HOVER),
        label = "row-bg",
    )
    val closeAlpha by animateFloatAsState(if (hovered) 1f else 0f, Motion.spec(Motion.HOVER), label = "row-x")
    // Nota nova entra com fade e um pequeno deslocamento.
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, Motion.spec(Motion.NOTE)) }

    ContextMenuArea(items = { listOf(ContextMenuItem("renomear", onRename), ContextMenuItem("excluir", onDelete)) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = appear.value
                    translationY = (1f - appear.value) * -6f
                }
                .clip(RoundedCornerShape(Dimens.radius))
                .background(bg)
                .hoverable(source)
                .clickable(interactionSource = source, indication = null, onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 6.dp),
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
            Text(
                text = "✕",
                color = palette.textDim,
                fontSize = 12.sp,
                fontFamily = TypeFonts.JetBrainsMono,
                modifier = Modifier
                    .graphicsLayer { alpha = closeAlpha }
                    .clickable(enabled = hovered, onClick = onDelete)
                    .padding(start = 8.dp, top = 2.dp, bottom = 2.dp),
            )
        }
    }
}
