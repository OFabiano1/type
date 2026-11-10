package app.type.data

import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

enum class Theme { DARK, LIGHT }

enum class FontChoice { MONO, SANS, SERIF }

class AppSettings(directory: Path) {

    private val file = directory.resolve(FILE_NAME)
    private val props = Properties()

    init {
        runCatching {
            Files.newInputStream(file).use { props.load(it) }
        }
    }

    var theme: Theme
        get() = runCatching { Theme.valueOf(props.getProperty(KEY_THEME)) }.getOrDefault(Theme.DARK)
        set(value) = put(KEY_THEME, value.name)

    var font: FontChoice
        get() = runCatching { FontChoice.valueOf(props.getProperty(KEY_FONT)) }.getOrDefault(FontChoice.MONO)
        set(value) = put(KEY_FONT, value.name)

    var fontSize: Int
        get() = int(KEY_FONT_SIZE, 17, FONT_SIZE)
        set(value) = put(KEY_FONT_SIZE, value.coerceIn(FONT_SIZE).toString())

    // Em % do tamanho da fonte (165 = 1.65x).
    var lineHeightPct: Int
        get() = int(KEY_LINE_HEIGHT, 165, LINE_HEIGHT)
        set(value) = put(KEY_LINE_HEIGHT, value.coerceIn(LINE_HEIGHT).toString())

    var editorWidth: Int
        get() = int(KEY_WIDTH, 720, WIDTH)
        set(value) = put(KEY_WIDTH, value.coerceIn(WIDTH).toString())

    var lastNote: String?
        get() = props.getProperty(KEY_LAST_NOTE)?.takeIf { it.isNotBlank() }
        set(value) = put(KEY_LAST_NOTE, value.orEmpty())

    private fun int(key: String, default: Int, range: IntRange): Int =
        props.getProperty(key)?.toIntOrNull()?.coerceIn(range) ?: default

    private fun put(key: String, value: String) {
        props.setProperty(key, value)
        Files.createDirectories(file.parent)
        Files.newOutputStream(file).use { props.store(it, "TYPE settings") }
    }

    companion object {
        private const val FILE_NAME = "settings.properties"
        private const val KEY_THEME = "theme"
        private const val KEY_FONT = "font"
        private const val KEY_FONT_SIZE = "fontSize"
        private const val KEY_LINE_HEIGHT = "lineHeight"
        private const val KEY_WIDTH = "editorWidth"
        private const val KEY_LAST_NOTE = "lastNote"

        val FONT_SIZE = 13..24
        val LINE_HEIGHT = 130..220
        val WIDTH = 480..960

        fun default(): AppSettings = AppSettings(Documents.resolve().resolve("TYPE").resolve(".type"))
    }
}
