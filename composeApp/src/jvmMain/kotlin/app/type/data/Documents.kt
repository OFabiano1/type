package app.type.data

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

// Documentos OneDrive-safe: o Windows move "Documentos" para OneDrive e o
// caminho real vive no registro (User Shell Folders, chave "Personal").
object Documents {
    private const val USER_SHELL_FOLDERS =
        "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders"
    private const val PERSONAL = "Personal"

    fun resolve(): Path {
        val fromRegister = readPersonalFromRegistry()?.let { Paths.get(it) }
        val home = System.getProperty("user.home")
        val candidates = listOfNotNull(
            fromRegister,
            Paths.get(home, "OneDrive", "Documents"),
            Paths.get(home, "OneDrive", "Documentos"),
            Paths.get(home, "Documents"),
            Paths.get(home, "Documentos"),
        )
        return candidates.firstOrNull { it.exists() } ?: candidates.last()
    }

    private fun readPersonalFromRegistry(): String? = runCatching {
        val process = ProcessBuilder("reg", "query", USER_SHELL_FOLDERS, "/v", PERSONAL)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.readBytes().decodeToString()
        process.waitFor()
        // Personal    REG_EXPAND_SZ    %USERPROFILE%\OneDrive\Documentos
        output.lineSequence()
            .firstOrNull { "REG_" in it && PERSONAL in it }
            ?.substringAfter("REG_EXPAND_SZ", "")
            ?.substringAfter("REG_SZ", "")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }.getOrNull()
}
