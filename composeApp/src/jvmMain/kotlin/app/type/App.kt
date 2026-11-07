package app.type

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import app.type.data.AppSettings
import app.type.data.Note
import app.type.data.NoteRepository
import app.type.data.Theme
import app.type.editor.EditorState
import app.type.theme.LocalTypePalette
import app.type.theme.TypeTheme
import app.type.ui.Editor
import app.type.ui.Sidebar
import app.type.ui.StatusBar
import app.type.ui.TopBar
import java.time.LocalDateTime

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val repo = remember { NoteRepository.vault() }
    val settings = remember { AppSettings.default() }
    val state = remember { EditorState(repo, scope) }
    val searchFocus = remember { FocusRequester() }

    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var theme by remember { mutableStateOf(settings.theme) }

    fun toggleTheme() {
        theme = if (theme == Theme.DARK) Theme.LIGHT else Theme.DARK
        settings.theme = theme
    }

    fun reload() {
        notes = repo.list()
    }

    state.onAfterSave = { reload() }

    fun openNote(note: Note) {
        state.openNote(note)
        query = ""
    }

    fun createNote() {
        val note = repo.create(LocalDateTime.now())
        openNote(note)
        reload()
    }

    fun deleteNote(note: Note) {
        val wasActive = state.note.value?.id == note.id
        repo.delete(note.id)
        reload()
        if (wasActive) {
            val next = notes.firstOrNull() ?: repo.create(LocalDateTime.now())
            openNote(next)
            reload()
        }
    }

    LaunchedEffect(Unit) {
        val first = repo.list().firstOrNull() ?: repo.create(LocalDateTime.now())
        state.openNote(first)
        reload()
    }

    TypeTheme(theme) {
        Column(
            modifier = Modifier
                .background(LocalTypePalette.current.background)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when {
                        event.key == Key.N && event.isCtrlPressed -> {
                            createNote()
                            true
                        }
                        event.key == Key.S && event.isCtrlPressed -> {
                            state.save()
                            true
                        }
                        event.key == Key.F && event.isCtrlPressed -> {
                            searchFocus.requestFocus()
                            true
                        }
                        else -> false
                    }
                },
        ) {
            TopBar(theme = theme, onToggleTheme = ::toggleTheme)
            Row(Modifier.weight(1f)) {
                Sidebar(
                    notes = notes,
                    activeId = state.note.value?.id,
                    query = query,
                    onQueryChange = { query = it },
                    onNoteClick = ::openNote,
                    onCreate = ::createNote,
                    onDelete = ::deleteNote,
                    searchFocus = searchFocus,
                )
                Column(Modifier.weight(1f)) {
                    Editor(state)
                }
            }
            StatusBar(state)
        }
    }
}
