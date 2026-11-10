package app.type.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import app.type.editor.Export
import app.type.editor.export.ExportName
import app.type.theme.LocalTypePalette
import app.type.theme.TypeFonts
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun TopBar(
    onExport: () -> Unit,
    onFocus: () -> Unit,
    onSettings: () -> Unit,
    currentNote: () -> Note?,
) {
    val palette = LocalTypePalette.current
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(palette.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Wordmark: "type" em minúsculas, ponto na cor de acento.
        Text("type", color = palette.text, fontSize = 15.sp, fontFamily = TypeFonts.JetBrainsMono)
        Text(".", color = palette.accent, fontSize = 15.sp, fontFamily = TypeFonts.JetBrainsMono)
        Spacer(Modifier.weight(1f))
        SoftButton("foco", onFocus)
        Box {
            SoftButton("▾ exportar", { expanded = true })
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                for (ext in listOf(".md", ".txt")) {
                    DropdownMenuItem(
                        text = { Text("como $ext") },
                        onClick = {
                            expanded = false
                            currentNote()?.let { exportNoteToDialog(it, ext, onExport) }
                        },
                    )
                }
            }
        }
        SoftButton("ajustes", onSettings)
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
