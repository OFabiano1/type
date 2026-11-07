package app.type.data

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.LocalDateTime
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoteRepositoryTest {

    private lateinit var vault: Path
    private lateinit var trash: Path
    private lateinit var repo: NoteRepository

    @BeforeTest
    fun setup() {
        vault = Files.createTempDirectory("type-vault")
        trash = Files.createTempDirectory("type-trash")
        repo = NoteRepository(vault, trash)
    }

    @AfterTest
    fun teardown() {
        vault.toFile().deleteRecursively()
        trash.toFile().deleteRecursively()
    }

    @Test
    fun `vault vazia lista nada`() {
        assertEquals(0, repo.list().size)
    }

    @Test
    fun `roundtrip utf-8 preserva acento emoji e crlf`() {
        val text = "ação 🫟\r\nlinha 2"
        repo.create(LocalDateTime.of(2026, 11, 7, 15, 0), text)
        val note = repo.list().single()
        assertEquals(text, note.content)
    }

    @Test
    fun `read retorna vazio quando arquivo ausente`() {
        assertEquals("", repo.read("nao-existe"))
    }

    @Test
    fun `lista ordenada por modificacao desc`() {
        repo.create(LocalDateTime.of(2026, 11, 7, 15, 0), "primeira")
        repo.create(LocalDateTime.of(2026, 11, 7, 15, 1), "segunda")
        Files.setAttribute(vault.resolve("primeira-20261107-150000.md"), "basic:lastModifiedTime", FileTime.from(Instant.ofEpochMilli(1000L)))
        Files.setAttribute(vault.resolve("segunda-20261107-150100.md"), "basic:lastModifiedTime", FileTime.from(Instant.ofEpochMilli(2000L)))
        assertEquals(listOf("segunda-20261107-150100", "primeira-20261107-150000"), repo.list().map { it.id })
    }

    @Test
    fun `delete move para lixeira`() {
        val note = repo.create(LocalDateTime.of(2026, 11, 7, 15, 0), "meu texto")
        repo.delete(note.id)
        assertFalse(vault.resolve(note.path.name).exists())
        assertTrue(trash.resolve(note.path.name).exists())
        assertEquals(0, repo.list().size)
    }

    @Test
    fun `delete colisao na lixeira usa sufixo -1 sem sobrescrever`() {
        val note = repo.create(LocalDateTime.of(2026, 11, 7, 15, 0), "um")
        val target = trash.resolve(note.path.name)
        target.writeText("conteúdo antigo")
        repo.delete(note.id)
        assertEquals("conteúdo antigo", target.readText())
        assertTrue(trash.resolve(note.path.name.replace(".md", "-1.md")).exists())
        assertEquals(0, repo.list().size)
    }

    @Test
    fun `só md e txt entram na lista`() {
        vault.resolve("outro.csv").writeText("x")
        repo.create(LocalDateTime.of(2026, 11, 7, 15, 0), "nota")
        assertEquals(listOf("nota-20261107-150000"), repo.list().map { it.id })
    }

    @Test
    fun `título da nota é a primeira linha`() {
        repo.create(LocalDateTime.of(2026, 11, 7, 15, 0), "# Ideia grande\r\ncorpo")
        assertEquals("Ideia grande", repo.list().single().title)
    }

    @Test
    fun `nota vazia tem título Sem título`() {
        repo.create(LocalDateTime.of(2026, 11, 7, 15, 0), "")
        assertEquals("Sem título", repo.list().single().title)
    }
}
