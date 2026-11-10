package app.type.editor

// Listas no texto puro: "1. x" / "- x" continuam no Enter; Enter num item vazio sai da lista.
object Lists {
    // grupos: 1 = indentação, 2 = número (vazio em marcador de bolinha)
    val MARKER = Regex("""^(\s*)(?:(\d+)\.|[-*•])[ \t]""")

    /** Texto e cursor após um Enter em [cursor], ou null se a linha não é item de lista (Enter normal). */
    fun onEnter(text: String, cursor: Int): Pair<String, Int>? {
        val start = text.lastIndexOf('\n', cursor - 1) + 1
        val before = text.substring(start, cursor)
        val m = MARKER.find(before) ?: return null
        if (before.length == m.value.length) return text.removeRange(start, cursor) to start
        val number = m.groupValues[2]
        val next = if (number.isEmpty()) m.value else "${m.groupValues[1]}${number.toInt() + 1}. "
        return text.substring(0, cursor) + "\n" + next + text.substring(cursor) to cursor + 1 + next.length
    }
}
