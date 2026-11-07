package app.type.ui

import app.type.data.Note

object NotesModel {
    fun filterNotes(notes: List<Note>, query: String): List<Note> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return notes
        return notes.filter { it.title.lowercase().contains(q) || it.content.lowercase().contains(q) }
    }
}
