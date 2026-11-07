package app.type.editor

object Stats {
    fun countWords(text: String): Int =
        text.split(Regex("\\s+")).count { it.isNotEmpty() }

    fun countChars(text: String): Int = text.length
}
