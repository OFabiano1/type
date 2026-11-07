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
}
