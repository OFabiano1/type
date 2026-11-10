package app.type.editor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ListsTest {
    @Test
    fun `numerada continua com o proximo numero`() {
        assertEquals("1. um\n2. " to 9, Lists.onEnter("1. um", 5))
        assertEquals("9. x\n10. " to 9, Lists.onEnter("9. x", 4))
    }

    @Test
    fun `bolinha continua e preserva indentacao`() {
        assertEquals("- a\n- " to 6, Lists.onEnter("- a", 3))
        assertEquals("  - a\n  - " to 10, Lists.onEnter("  - a", 5))
    }

    @Test
    fun `item vazio sai da lista`() {
        assertEquals("1. um\n" to 6, Lists.onEnter("1. um\n2. ", 9))
        assertEquals("" to 0, Lists.onEnter("- ", 2))
    }

    @Test
    fun `linha comum e cursor dentro do marcador seguem Enter normal`() {
        assertNull(Lists.onEnter("texto", 5))
        assertNull(Lists.onEnter("1. um", 1))
        assertNull(Lists.onEnter("", 0))
    }

    @Test
    fun `enter no meio do item quebra o resto para o proximo`() {
        assertEquals("1. ab\n2. cd" to 9, Lists.onEnter("1. abcd", 5))
    }
}
