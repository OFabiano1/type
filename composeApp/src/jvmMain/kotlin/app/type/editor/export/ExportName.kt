package app.type.editor.export

object ExportName {
    private val validExts = setOf("md", "txt")

    fun exportFileName(base: String, ext: String): String {
        var baseName = base
        val lowerBase = baseName.lowercase()
        for (validExt in validExts) {
            if (lowerBase.endsWith(".$validExt")) {
                baseName = baseName.removeSuffix(".$validExt")
                break
            }
        }

        var finalExt = ext.trim().lowercase().trimStart('.')
        if (finalExt !in validExts) finalExt = "txt"

        return "$baseName.$finalExt"
    }
}