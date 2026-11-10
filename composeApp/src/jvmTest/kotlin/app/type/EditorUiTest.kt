package app.type

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import app.type.data.FontChoice
import app.type.data.NoteRepository
import app.type.data.Theme
import app.type.editor.EditorState
import app.type.theme.TypeTheme
import app.type.ui.Editor
import app.type.ui.EditorSettings
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

// Editor isolado num vault temporário: não toca nas notas reais.
@OptIn(ExperimentalTestApi::class, ExperimentalComposeUiApi::class)
class EditorUiTest {
    @Test
    fun `enter continua lista numerada e de bolinhas, e item vazio sai da lista`() = runDesktopComposeUiTest(900, 600) {
        val dir = Files.createTempDirectory("type-ui")
        val repo = NoteRepository(dir.resolve("n"), dir.resolve("l"))
        val state = EditorState(repo, CoroutineScope(Dispatchers.Unconfined))
        state.openNote(repo.create(LocalDateTime.now(), ""))
        setContent { TypeTheme(Theme.DARK) { Editor(state, EditorSettings(FontChoice.MONO, 17, 165, 780)) } }
        waitForIdle()
        val field = onNode(hasSetTextAction())

        field.performTextInput("Meu título")
        field.performKeyInput { pressKey(Key.Enter) }
        field.performTextInput("1. um")
        field.performKeyInput { pressKey(Key.Enter) }
        waitForIdle()
        assertEquals("Meu título\n1. um\n2. ", state.text.value)

        field.performTextInput("dois")
        field.performKeyInput { pressKey(Key.Enter) }
        field.performKeyInput { pressKey(Key.Enter) } // item vazio: sai da lista
        waitForIdle()
        assertEquals("Meu título\n1. um\n2. dois\n", state.text.value)

        field.performTextInput("- a")
        field.performKeyInput { pressKey(Key.Enter) }
        field.performTextInput("b")
        waitForIdle()
        assertEquals("Meu título\n1. um\n2. dois\n- a\n- b", state.text.value)

        val dirShots = File("build/ui-shots").apply { mkdirs() }
        ImageIO.write(onRoot().captureToImage().toAwtImage(), "png", File(dirShots, "editor-lists.png"))
    }
}
