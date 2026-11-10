package app.type.data

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.writeText
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AppSettingsTest {

    private lateinit var dir: Path

    @BeforeTest
    fun setup() {
        dir = Files.createTempDirectory("type-settings")
    }

    @AfterTest
    fun teardown() {
        dir.toFile().deleteRecursively()
    }

    @Test
    fun `tema padrão é dark quando arquivo ausente`() {
        assertEquals(Theme.DARK, AppSettings(dir).theme)
    }

    @Test
    fun `tema persiste entre instâncias`() {
        AppSettings(dir).theme = Theme.LIGHT
        assertEquals(Theme.LIGHT, AppSettings(dir).theme)
    }

    @Test
    fun `arquivo de settings corrompido cai no padrão dark`() {
        Files.createDirectories(dir)
        dir.resolve("settings.properties").writeText("lixo incompreensível")
        assertEquals(Theme.DARK, AppSettings(dir).theme)
    }

    @Test
    fun `ajustes de escrita persistem e respeitam limites`() {
        AppSettings(dir).apply {
            font = FontChoice.SERIF
            fontSize = 99
            lineHeightPct = 180
            editorWidth = 600
            lastNote = "abc"
        }
        val loaded = AppSettings(dir)
        assertEquals(FontChoice.SERIF, loaded.font)
        assertEquals(24, loaded.fontSize)
        assertEquals(180, loaded.lineHeightPct)
        assertEquals(600, loaded.editorWidth)
        assertEquals("abc", loaded.lastNote)
    }

    @Test
    fun `ajustes ausentes usam padrao`() {
        val s = AppSettings(dir)
        assertEquals(17, s.fontSize)
        assertEquals(720, s.editorWidth)
        assertEquals(null, s.lastNote)
    }
}
