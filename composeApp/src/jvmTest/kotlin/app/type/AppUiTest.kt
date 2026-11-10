package app.type

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.test.captureToImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test

// Renderiza o App de verdade (sem janela do SO) e dirige com teclado/clique; lê o vault real, não escreve nada.
@OptIn(ExperimentalTestApi::class, ExperimentalComposeUiApi::class)
class AppUiTest {

    // Com diálogo aberto há 2 roots: a janela (0) e a camada do diálogo (1); compõe os dois numa imagem só.
    private fun androidx.compose.ui.test.ComposeUiTest.shot(name: String) {
        val dir = File("build/ui-shots").apply { mkdirs() }
        val roots = onAllNodes(isRoot())
        val base = roots[0].captureToImage().toAwtImage()
        val g = base.createGraphics()
        if (roots.fetchSemanticsNodes().size > 1) g.drawImage(roots[1].captureToImage().toAwtImage(), 0, 0, null)
        g.dispose()
        ImageIO.write(base, "png", File(dir, "$name.png"))
    }

    @Test
    fun `F1 abre o About, Esc fecha, clique em fechar fecha`() = runDesktopComposeUiTest(1200, 760) {
        setContent { App() }
        waitForIdle()
        onNodeWithText("sobre").assertIsDisplayed()
        shot("1-main")

        onRoot().performKeyInput { pressKey(Key.F1) }
        waitForIdle()
        onNodeWithText("a quiet place to write.").assertIsDisplayed()
        onNodeWithText("fechar").assertIsDisplayed()
        shot("2-about")

        onAllNodes(isRoot())[1].performKeyInput { pressKey(Key.Escape) }
        waitForIdle()
        onNodeWithText("a quiet place to write.").assertDoesNotExist()
        shot("3-closed")

        onNodeWithText("sobre").performClick()
        waitForIdle()
        onNodeWithText("a quiet place to write.").assertIsDisplayed()
        onNodeWithText("fechar").performClick()
        waitForIdle()
        onNodeWithText("a quiet place to write.").assertDoesNotExist()
    }
}
