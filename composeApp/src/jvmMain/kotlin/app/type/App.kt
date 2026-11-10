package app.type

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import app.type.data.AppSettings
import app.type.data.Note
import app.type.data.NoteRepository
import app.type.data.Notes
import app.type.data.Theme
import app.type.editor.EditorState
import app.type.theme.LocalTypePalette
import app.type.theme.Motion
import app.type.theme.TypeTheme
import app.type.ui.ConfirmDeleteDialog
import app.type.ui.Editor
import app.type.ui.EditorSettings
import app.type.ui.RenameDialog
import app.type.ui.SettingsPanel
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
    var font by remember { mutableStateOf(settings.font) }
    var fontSize by remember { mutableStateOf(settings.fontSize) }
    var lineHeight by remember { mutableStateOf(settings.lineHeightPct) }
    var width by remember { mutableStateOf(settings.editorWidth) }
    var focusMode by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Note?>(null) }
    var renaming by remember { mutableStateOf<Note?>(null) }

    fun toggleTheme() {
        theme = if (theme == Theme.DARK) Theme.LIGHT else Theme.DARK
        settings.theme = theme
    }

    fun reload() {
        notes = repo.list()
    }

    fun openNote(note: Note) {
        state.openNote(note)
        settings.lastNote = note.id
        query = ""
        reload()
    }

    fun createNote() {
        openNote(repo.create(LocalDateTime.now()))
    }

    fun deleteNote(note: Note) {
        val wasActive = state.note.value?.id == note.id
        repo.delete(note.id)
        reload()
        if (wasActive) openNote(notes.firstOrNull() ?: repo.create(LocalDateTime.now()))
    }

    fun renameNote(note: Note, title: String) {
        if (state.note.value?.id == note.id) {
            state.updateText(Notes.withTitle(state.text.value, title)) // autosave grava e recarrega a lista
        } else {
            repo.write(note.id, Notes.withTitle(repo.read(note.id), title))
            reload()
        }
    }

    // Texto atual (inclusive o ainda não salvo) para exportar.
    fun currentNote(): Note? = state.note.value?.copy(content = state.text.value)

    LaunchedEffect(Unit) {
        state.onAfterSave = { reload() }
        val all = repo.list()
        val first = all.firstOrNull { it.id == settings.lastNote } ?: all.firstOrNull() ?: repo.create(LocalDateTime.now())
        state.openNote(first)
        reload()
    }

    TypeTheme(theme) {
        Box(
            modifier = Modifier
                .background(LocalTypePalette.current.background)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when {
                        event.key == Key.N && event.isCtrlPressed -> { createNote(); true }
                        event.key == Key.S && event.isCtrlPressed -> { state.save(); true }
                        event.key == Key.F && event.isCtrlPressed -> { focusMode = false; searchFocus.requestFocus(); true }
                        event.key == Key.Backslash && event.isCtrlPressed -> { focusMode = !focusMode; true }
                        event.key == Key.Comma && event.isCtrlPressed -> { showSettings = !showSettings; true }
                        event.key == Key.F2 -> { state.note.value?.let { renaming = it }; true }
                        event.key == Key.Escape && showSettings -> { showSettings = false; true }
                        else -> false
                    }
                },
        ) {
            Column {
                TopBar(
                    onExport = ::reload,
                    onFocus = { focusMode = !focusMode },
                    onSettings = { showSettings = !showSettings },
                    currentNote = ::currentNote,
                )
                Row(Modifier.weight(1f)) {
                    AnimatedVisibility(
                        visible = !focusMode,
                        enter = fadeIn(Motion.spec(Motion.PANEL)) + expandHorizontally(Motion.spec(Motion.PANEL)),
                        exit = fadeOut(Motion.spec(Motion.PANEL)) + shrinkHorizontally(Motion.spec(Motion.PANEL)),
                    ) {
                        Sidebar(
                            notes = notes,
                            activeId = state.note.value?.id,
                            query = query,
                            onQueryChange = { query = it },
                            onNoteClick = ::openNote,
                            onCreate = ::createNote,
                            onRename = { renaming = it },
                            onDelete = { pendingDelete = it },
                            searchFocus = searchFocus,
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Editor(state, EditorSettings(font, fontSize, lineHeight, width))
                    }
                }
                StatusBar(state)
            }
            AnimatedVisibility(
                visible = showSettings,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp, end = 16.dp),
                enter = fadeIn(Motion.spec(Motion.PANEL)) + slideInVertically(Motion.spec(Motion.PANEL)) { -it / 8 },
                exit = fadeOut(Motion.spec(Motion.PANEL)) + slideOutVertically(Motion.spec(Motion.PANEL)) { -it / 8 },
            ) {
                SettingsPanel(
                    settings = EditorSettings(font, fontSize, lineHeight, width),
                    theme = theme,
                    onFont = { font = it; settings.font = it },
                    onFontSize = { fontSize = it.coerceIn(AppSettings.FONT_SIZE); settings.fontSize = fontSize },
                    onLineHeight = { lineHeight = it.coerceIn(AppSettings.LINE_HEIGHT); settings.lineHeightPct = lineHeight },
                    onWidth = { width = it.coerceIn(AppSettings.WIDTH); settings.editorWidth = width },
                    onToggleTheme = ::toggleTheme,
                )
            }
            pendingDelete?.let { note ->
                ConfirmDeleteDialog(note, onConfirm = { deleteNote(note); pendingDelete = null }, onDismiss = { pendingDelete = null })
            }
            renaming?.let { note ->
                RenameDialog(note, onRename = { renameNote(note, it); renaming = null }, onDismiss = { renaming = null })
            }
        }
    }
}
