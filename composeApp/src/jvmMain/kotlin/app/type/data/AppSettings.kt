package app.type.data

import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

enum class Theme { DARK, LIGHT }

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
        set(value) {
            props.setProperty(KEY_THEME, value.name)
            save()
        }

    private fun save() {
        Files.createDirectories(file.parent)
        Files.newOutputStream(file).use { props.store(it, "TYPE settings") }
    }

    companion object {
        private const val FILE_NAME = "settings.properties"
        private const val KEY_THEME = "theme"

        fun default(): AppSettings = AppSettings(Documents.resolve().resolve("TYPE").resolve(".type"))
    }
}
