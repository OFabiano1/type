package app.type.data

import java.io.IOException
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import kotlin.io.path.name
import kotlin.streams.asSequence

class NoteRepository(
    private val vault: Path,
    private val trashDir: Path,
) {
    init {
        Files.createDirectories(vault)
        Files.createDirectories(trashDir)
    }

    fun list(): List<Note> = Files.list(vault).use { stream ->
        stream.asSequence()
            .filter { Files.isRegularFile(it) && Notes.isNoteFile(it.name) }
            .map { path ->
                val content = try {
                    Files.readString(path, UTF_8)
                } catch (_: IOException) {
                    ""
                }
                Note(
                    id = Notes.noteId(path.name),
                    path = path,
                    title = Notes.noteTitle(content),
                    content = content,
                    modifiedAt = Files.getLastModifiedTime(path),
                )
            }
            .sortedByDescending { it.modifiedAt.toMillis() }
            .toList()
    }

    fun read(id: String): String {
        val file = noteFile(id) ?: return ""
        return Files.readString(file, UTF_8)
    }

    fun write(id: String, text: String) {
        val file = noteFile(id) ?: vault.resolve("$id.md")
        atomicWrite(file, text)
    }

    fun create(now: LocalDateTime, text: String = ""): Note {
        val existing = Files.list(vault).use { stream -> stream.asSequence().map { it.name }.toList() }.toSet()
        val name = Notes.newNoteFileName(now, existing, slug = Notes.noteSlug(text))
        val file = vault.resolve(name)
        atomicWrite(file, text)
        return Note(
            id = Notes.noteId(name),
            path = file,
            title = Notes.noteTitle(text),
            content = text,
            modifiedAt = Files.getLastModifiedTime(file),
        )
    }

    fun delete(id: String) {
        val file = noteFile(id) ?: return
        val existing = Files.list(trashDir).use { stream -> stream.asSequence().map { it.name }.toList() }.toSet()
        val name = uniqueName(file.name, existing)
        Files.move(file, trashDir.resolve(name))
    }

    fun noteFile(id: String): Path? = Files.list(vault).use { stream ->
        stream.asSequence().firstOrNull { Files.isRegularFile(it) && Notes.noteId(it.name) == id }
    }

    private fun uniqueName(base: String, existing: Set<String>): String {
        if (base !in existing) return base
        val (stem, ext) = suffixParts(base)
        var suffix = 1
        while (true) {
            val candidate = "$stem-$suffix$ext"
            if (candidate !in existing) return candidate
            suffix++
        }
    }

    private fun suffixParts(name: String): Pair<String, String> {
        val ext = name.substringAfterLast('.', "")
        val stem = if (ext.isEmpty()) name else name.removeSuffix(".$ext")
        return stem to (if (ext.isEmpty()) "" else ".$ext")
    }

    private fun atomicWrite(file: Path, text: String) {
        val tmp = file.resolveSibling(file.name + ".tmp")
        Files.writeString(tmp, text, UTF_8)
        try {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } catch (_: IOException) {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    companion object {
        fun vault(): NoteRepository =
            NoteRepository(
                Documents.resolve().resolve("TYPE").resolve("Notas"),
                Documents.resolve().resolve("TYPE").resolve("Lixeira"),
            )
    }
}
