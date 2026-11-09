package app.type.editor

import app.type.data.Note
import app.type.editor.export.ExportName
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object Export {
    fun exportNote(note: Note, fileName: String): Path {
        val exportPath = Path.of(fileName)
        val fileContent = note.content
        
        try {
            Files.newBufferedWriter(exportPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { writer ->
                writer.write(fileContent)
            }
            return exportPath
        } catch (e: IOException) {
            throw FileNotFoundException("Falha ao exportar nota ${note.title} para $fileName: ${e.message}")
        }
    }

    fun exportToNote(note: Note, fileName: String) {
        val exportFileName = ExportName.exportFileName(note.title, fileName)
        val exportPath = exportNote(note, exportFileName)
    }
}