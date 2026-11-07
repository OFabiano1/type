package app.type.editor

import kotlin.test.Test
import kotlin.test.assertEquals

class StatsTest {

    @Test
    fun `conta palavras separadas por qualquer whitespace`() {
        assertEquals(3, Stats.countWords("olá mundo\r\ntestando"))
    }

    @Test
    fun `texto só de whitespace conta zero`() {
        assertEquals(0, Stats.countWords("   \r\n\t "))
    }

    @Test
    fun `string vazia conta zero`() {
        assertEquals(0, Stats.countWords(""))
    }

    @Test
    fun `countChars é o length da string incluindo espaços e quebras`() {
        assertEquals(5, Stats.countChars("ab\r\nc"))
    }
}
