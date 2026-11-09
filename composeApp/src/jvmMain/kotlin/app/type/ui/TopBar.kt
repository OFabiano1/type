package app.type.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.data.Note
import app.type.data.Theme
import app.type.editor.Export
import app.type.editor.export.ExportName
import app.type.theme.LocalTypePalette
import app.type.theme.TypeFonts
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun TopBar(
    theme: Theme,
    onToggleTheme: () -> Unit,
    onExport: () -> Unit,
    currentNote: Note? = null,
) {
    val palette = LocalTypePalette.current
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(palette.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            text = if (theme == Theme.DARK) "tema: escuro" else "tema: claro",
            color = palette.textDim,
            fontSize = 12.sp,
            fontFamily = TypeFonts.JetBrainsMono,
            modifier = Modifier.clickable { onToggleTheme() },
        )

        Spacer(Modifier.width(12.dp))
        Text(
            text = "\u25BE exportar",
            color = palette.textDim,
            fontSize = 12.sp,
            fontFamily = TypeFonts.JetBrainsMono,
            modifier = Modifier.clickable { expanded = true },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("como .md") },
                onClick = {
                    currentNote?.let { note ->
                        exportNoteToDialog(note, ".md", onExport)
                    }
                    expanded = false
                },
            )

            DropdownMenuItem(
                text = { Text("como .txt") },
                onClick = {
                    currentNote?.let { note ->
                        exportNoteToDialog(note, ".txt", onExport)
                    }
                    expanded = false
                },
            )
        }
    }
}

private fun exportNoteToDialog(note: Note, ext: String, onExport: () -> Unit) {
    val fileChooser = JFileChooser()
    fileChooser.fileFilter = FileNameExtensionFilter("Documentos de texto", ext.removePrefix("."))
    fileChooser.setCurrentDirectory(File(System.getProperty("user.home")))
    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        val selectedFile = fileChooser.selectedFile
        val fileName = ExportName.exportFileName(note.title, ext)
        val finalPath = selectedFile.absolutePath.removeSuffix(ext) + fileName
        Export.exportNote(note, finalPath)
        onExport()
    }
}