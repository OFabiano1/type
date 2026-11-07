package app.type.data

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class NotesTest {

    @Test
    fun `título de texto vazio é Sem título`() {
        assertEquals("Sem título", Notes.noteTitle(""))
        assertEquals("Sem título", Notes.noteTitle("   \r\n\t "))
    }

    @Test
    fun `título é a primeira linha não vazia`() {
        assertEquals("Olá mundo", Notes.noteTitle("\r\n  Olá mundo \r\nsegunda linha"))
    }

    @Test
    fun `título limpa marcas markdown`() {
        assertEquals("Ideia grande", Notes.noteTitle("### Ideia grande"))
    }

    @Test
    fun `título com mais de sessenta chars cap em sessenta`() {
        val long = "a".repeat(80)
        assertEquals("a".repeat(60), Notes.noteTitle(long))
    }

    @Test
    fun `slug limpa pontuação e acentos`() {
        assertEquals("ola-mundo", Notes.noteSlug("Olá, Mundo!"))
        assertEquals("acao", Notes.noteSlug("ação"))
        assertEquals("nota", Notes.noteSlug(""))
        assertEquals("nota", Notes.noteSlug("  "))
    }

    @Test
    fun `slug cap em vinte`() {
        assertEquals("a".repeat(20), Notes.noteSlug("a".repeat(50)))
    }

    @Test
    fun `nome de arquivo nova nota usa slug e timestamp`() {
        val name = Notes.newNoteFileName(LocalDateTime.of(2026, 11, 7, 15, 0), emptySet())
        assertEquals("nota-20261107-150000.md", name)
    }

    @Test
    fun `nome de arquivo colidiu usa sufixo -1`() {
        val now = LocalDateTime.of(2026, 11, 7, 15, 0)
        val name = Notes.newNoteFileName(now, setOf("nota-20261107-150000.md"))
        assertEquals("nota-20261107-150000-1.md", name)
    }

    @Test
    fun `slug entra no nome de arquivo`() {
        val name = Notes.newNoteFileName(LocalDateTime.of(2026, 11, 7, 15, 0), emptySet(), slug = "ideia")
        assertEquals("ideia-20261107-150000.md", name)
    }
}
