package app.type.editor

import app.type.data.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.io.path.readText
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EditorStateTest {

    private lateinit var vault: Path
    private lateinit var repo: NoteRepository

    @BeforeTest
    fun setup() {
        vault = Files.createTempDirectory("type-vault")
        repo = NoteRepository(vault, vault.resolveSibling("type-trash"))
    }

    @AfterTest
    fun teardown() {
        vault.toFile().deleteRecursively()
        vault.resolveSibling("type-trash").toFile().deleteRecursively()
    }

    private fun newNote(note: String = "nota") =
        repo.create(LocalDateTime.of(2026, 11, 7, 12, 0), note)

    @Test
    fun `autosave grava depois do debounce`() = runTest {
        val state = EditorState(repo, backgroundScope, UnconfinedTestDispatcher(testScheduler))
        state.openNote(newNote("inicial"))
        state.updateText("novo texto")

        advanceTimeBy(EditorState.DEBOUNCE_MS - 1)
        runCurrent()
        assertEquals("inicial", repo.read(state.note.value!!.id))

        advanceTimeBy(1)
        runCurrent()
        assertEquals("novo texto", repo.read(state.note.value!!.id))
        assertIs<EditorState.SaveStatus.Saved>(state.status.value)
    }

    @Test
    fun `digitação contínua adia o save até a pausa`() = runTest {
        val state = EditorState(repo, backgroundScope, UnconfinedTestDispatcher(testScheduler))
        state.openNote(newNote("inicial"))
        state.updateText("a")
        advanceTimeBy(200)
        state.updateText("ab")
        advanceTimeBy(200)
        state.updateText("abc")
        advanceTimeBy(EditorState.DEBOUNCE_MS)
        runCurrent()
        assertEquals("abc", repo.read(state.note.value!!.id))
    }

    @Test
    fun `save imediato grava sem esperar o debounce`() = runTest {
        val state = EditorState(repo, backgroundScope, UnconfinedTestDispatcher(testScheduler))
        state.openNote(newNote("inicial"))
        state.updateText("ctrl+s")
        state.save()
        runCurrent()
        assertEquals("ctrl+s", repo.read(state.note.value!!.id))
    }

    @Test
    fun `erro de disco mantém texto no editor e status erro`() = runTest {
        val state = EditorState(repo, backgroundScope, UnconfinedTestDispatcher(testScheduler))
        state.openNote(newNote("inicial"))
        vault.toFile().deleteRecursively()
        state.updateText("novo texto")
        advanceTimeBy(EditorState.DEBOUNCE_MS)
        runCurrent()
        assertEquals("novo texto", state.text.value)
        assertIs<EditorState.SaveStatus.Error>(state.status.value)
        assertTrue(state.text.value.isNotEmpty())
    }
}
