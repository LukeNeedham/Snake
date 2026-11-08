package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import com.lukeneedham.snake.game.ToadKind

/** The colour of a letter of the sprites. [kind] only matters for the toad body letters: `b` body, `d` shade, `l` light. */
internal fun spriteColor(letter: Char, kind: ToadKind?): Color? {
    val toad = kind ?: ToadKind.Purple
    return when (letter) {
        'o' -> Color(0xFF2B2230)
        'g' -> Color(0xFFA39AA8)
        'G' -> Color(0xFFD9D2DC)
        'p' -> Color(0xFFF28FA9)
        'e' -> Color(0xFF151218)
        'w' -> Color(0xFFFFFFFF)
        'b' -> toad.body
        'd' -> toad.shade
        'l' -> toad.light
        'k' -> Color(0xFF3A3340)
        's' -> Color(0xFF8C82A0)
        'h' -> Color(0xFF6B4A35)
        'H' -> Color(0xFF9C7452)
        'N' -> Color(0xFFE3B88F)
        else -> null
    }
}
