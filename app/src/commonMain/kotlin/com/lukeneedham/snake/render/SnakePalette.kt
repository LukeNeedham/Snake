package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color

/** The colours of the snake. */
internal object SnakePalette {
    val outline = Color(0xFF14261B)
    val head = Color(0xFF38A866)
    val dead = Color(0xFF8A908C)
    val tongue = Color(0xFFE8465A)
    val mouthInside = Color(0xFFD43A55)

    /** The skin: a diamond pattern of eight discs that repeats along the whole body, however long it grows. */
    val skin = listOf(
        Color(0xFF1F6B45),
        Color(0xFF2F8F5B),
        Color(0xFF52C27D),
        Color(0xFFF4D35E),
        Color(0xFFF4D35E),
        Color(0xFF52C27D),
        Color(0xFF2F8F5B),
        Color(0xFF1F6B45),
    )
}
