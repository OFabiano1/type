package app.type.ui

import app.type.data.Note
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object NotesModel {
    fun filterNotes(notes: List<Note>, query: String): List<Note> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return notes
        return notes.filter { it.title.lowercase().contains(q) || it.content.lowercase().contains(q) }
    }

    // Agrupa por recência mantendo a ordem recebida; grupos vazios somem.
    fun groupByRecency(
        notes: List<Note>,
        today: LocalDate = LocalDate.now(),
        zone: ZoneId = ZoneId.systemDefault(),
    ): List<Pair<String, List<Note>>> =
        notes.groupBy { note ->
            val days = ChronoUnit.DAYS.between(note.modifiedAt.toInstant().atZone(zone).toLocalDate(), today)
            when {
                days <= 0 -> "hoje"
                days == 1L -> "ontem"
                days <= 7 -> "esta semana"
                else -> "antes"
            }
        }.toList()
}
