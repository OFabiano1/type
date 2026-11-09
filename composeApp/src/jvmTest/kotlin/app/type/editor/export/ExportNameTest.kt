package app.type.editor.export

import kotlin.test.Test
import kotlin.test.assertEquals

class ExportNameTest {

    @Test
    fun `export file name valid extensions`() {
        assertEquals("nota.md", ExportName.exportFileName("nota", "md"))
        assertEquals("nota.txt", ExportName.exportFileName("nota", "txt"))
        assertEquals("nota.md", ExportName.exportFileName("nota", "MD"))
        assertEquals("nota.txt", ExportName.exportFileName("nota", "TXT"))
        assertEquals("nota-1.md", ExportName.exportFileName("nota-1", "md"))
    }

    @Test
    fun `export file name invalid extension becomes txt`() {
        assertEquals("nota.txt", ExportName.exportFileName("nota", "pdf"))
        assertEquals("nota-1.txt", ExportName.exportFileName("nota-1", "doc"))
        assertEquals("nota-1.txt", ExportName.exportFileName("nota-1", ""))
        assertEquals("nota-1.txt", ExportName.exportFileName("nota-1", "md.txt"))
    }

    @Test
    fun `export file name doesn't duplicate extension`() {
        assertEquals("nota.md", ExportName.exportFileName("nota.md", "md"))
        assertEquals("nota.txt", ExportName.exportFileName("nota.md", "txt"))
        assertEquals("nota-1.md", ExportName.exportFileName("nota-1.md", "md"))
        assertEquals("nota-1.txt", ExportName.exportFileName("nota-1.md", "txt"))
    }

    @Test
    fun `export file name base without extension`() {
        assertEquals("nota.md", ExportName.exportFileName("nota", "md"))
        assertEquals("nota.txt", ExportName.exportFileName("nota", "txt"))
        assertEquals("nota-1.txt", ExportName.exportFileName("nota-1", "txt"))
    }

    @Test
    fun `export file name empty base`() {
        assertEquals(".md", ExportName.exportFileName("", "md"))
        assertEquals(".txt", ExportName.exportFileName("", "txt"))
    }
}