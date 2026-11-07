package app.type.data

import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.Normalizer

data class Note(
    val id: String, // nome do arquivo sem extensão
    val path: Path,
    val title: String,
    val content: String,
    val modifiedAt: FileTime,
)

object Notes {
    const val MAX_SLUG_LENGTH = 20
    const val MAX_TITLE_LENGTH = 60
    private val fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")

    fun noteTitle(text: String): String {
        val first = text.lines().firstOrNull { it.isNotBlank() } ?: return "Sem título"
        val clean = first.trimStart().dropWhile { it == '#' }.trimStart().trimEnd()
        return if (clean.isBlank()) "Sem título" else clean.take(MAX_TITLE_LENGTH)
    }

    fun noteSlug(text: String, max: Int = MAX_SLUG_LENGTH): String {
        val cleaned = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .take(max)
            .trim('-')
        return cleaned.ifEmpty { "nota" }
    }

    fun newNoteFileName(now: LocalDateTime, existing: Set<String>, slug: String? = null): String {
        val base = (slug ?: "").ifBlank { "nota" }
        var name = "${base}-${now.format(fileNameFormatter)}.md"
        var suffix = 1
        while (name in existing) {
            name = "$base-${now.format(fileNameFormatter)}-$suffix.md"
            suffix++
        }
        return name
    }

    fun isNoteFile(fileName: String): Boolean =
        fileName.endsWith(".md", ignoreCase = true) || fileName.endsWith(".txt", ignoreCase = true)

    fun noteId(fileName: String): String {
        val lower = fileName.lowercase().removeSuffix(".md").removeSuffix(".txt")
        return fileName.substring(0, lower.length)
    }
}
