package app.type.editor

import app.type.data.Note
import app.type.data.NoteRepository
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EditorState(
    private val repo: NoteRepository,
    private val scope: CoroutineScope,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) {
    sealed interface SaveStatus {
        data object Idle : SaveStatus
        data object Saving : SaveStatus
        data class Saved(val at: LocalDateTime) : SaveStatus
        data object Error : SaveStatus
    }

    val text = MutableStateFlow("")
    val status = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val note = MutableStateFlow<Note?>(null)
    var onAfterSave: (() -> Unit)? = null
    private var dirty = false
    private var autosave: Job? = null

    fun openNote(value: Note) {
        note.value = value
        text.value = value.content
        dirty = false
        status.value = SaveStatus.Idle
    }

    fun updateText(value: String) {
        text.value = value
        if (note.value == null) return
        dirty = true
        scheduleAutosave()
    }

    fun save() {
        val current = note.value ?: return
        if (!dirty) return
        dirty = false
        autosave?.cancel()
        scope.launch(io) {
            status.value = SaveStatus.Saving
            try {
                repo.write(current.id, text.value)
                note.value = current.copy(content = text.value)
                status.value = SaveStatus.Saved(LocalDateTime.now())
                onAfterSave?.invoke()
            } catch (_: IOException) {
                status.value = SaveStatus.Error
            }
        }
    }

    private fun scheduleAutosave() {
        if (note.value == null) return
        autosave?.cancel()
        autosave = scope.launch {
            delay(DEBOUNCE_MS)
            save()
        }
    }

    companion object {
        const val DEBOUNCE_MS = 500L

        fun savedLabel(at: LocalDateTime): String =
            "salvo às ${at.format(DateTimeFormatter.ofPattern("HH:mm"))}"
    }
}
