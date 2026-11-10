package app.type.ui

import app.type.data.Note
import kotlin.test.Test
import kotlin.test.assertEquals
import java.nio.file.Paths
import java.nio.file.attribute.FileTime

class NotesModelTest {

    private fun note(id: String, title: String, content: String) = Note(
        id = id,
        path = Paths.get("v", "$id.md"),
        title = title,
        content = content,
        modifiedAt = FileTime.fromMillis(0),
    )

    private val notes = listOf(
        note("receita", "Receita de bolo", "farinha, açúcar"),
        note("ideas", "Ideias de projeto", "app de notas minimalista"),
        note("lorem", "Ipsum", "texto qualquer"),
    )

    @Test
    fun `query vazia devolve lista toda`() {
        assertEquals(notes, NotesModel.filterNotes(notes, ""))
        assertEquals(notes, NotesModel.filterNotes(notes, "   "))
    }

    @Test
    fun `match por título`() {
        assertEquals(listOf("receita"), NotesModel.filterNotes(notes, "bolo").map { it.id })
    }

    @Test
    fun `match por conteúdo`() {
        assertEquals(listOf("ideas"), NotesModel.filterNotes(notes, "minimalista").map { it.id })
    }

    @Test
    fun `busca case-insensitive com acento`() {
        assertEquals(listOf("ideas"), NotesModel.filterNotes(notes, "ideias").map { it.id })
        assertEquals(listOf("receita"), NotesModel.filterNotes(notes, "RECEITA").map { it.id })
    }

    @Test
    fun `sem match devolve vazia`() {
        assertEquals(0, NotesModel.filterNotes(notes, "xyz").size)
    }

    @Test
    fun `agrupa por recencia e omite grupos vazios`() {
        val zone = java.time.ZoneId.of("UTC")
        val today = java.time.LocalDate.of(2026, 11, 10)
        fun at(d: java.time.LocalDate) = FileTime.from(d.atStartOfDay(zone).toInstant())
        val ns = listOf(
            note("a", "A", "").copy(modifiedAt = at(today)),
            note("b", "B", "").copy(modifiedAt = at(today.minusDays(1))),
            note("c", "C", "").copy(modifiedAt = at(today.minusDays(30))),
        )
        assertEquals(listOf("hoje", "ontem", "antes"), NotesModel.groupByRecency(ns, today, zone).map { it.first })
    }
}
