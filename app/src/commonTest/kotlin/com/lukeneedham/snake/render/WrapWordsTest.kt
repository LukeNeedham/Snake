package com.lukeneedham.snake.render

import kotlin.test.Test
import kotlin.test.assertEquals

class WrapWordsTest {

    @Test
    fun aShortTextStaysOnOneLine() {
        assertEquals(listOf("The snake goes faster."), wrapWords("The snake goes faster.", 21))
    }

    @Test
    fun aLongTextBreaksBetweenWords() {
        assertEquals(
            listOf("Sprinting is faster", "and costs less food."),
            wrapWords("Sprinting is faster and costs less food.", 21),
        )
    }
}
