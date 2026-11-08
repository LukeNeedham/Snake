package com.lukeneedham.snake.game

import androidx.compose.ui.graphics.Color

/** A colour of toad: each one is poisonous in its own way. */
enum class ToadKind(val effect: Effect, val body: Color, val shade: Color, val light: Color) {
    PURPLE(Effect.HALLUCINATING, Color(0xFF7A5CC8), Color(0xFF5238A0), Color(0xFFC9B8F5)),
    TEAL(Effect.DISGUISED, Color(0xFF2FB5A3), Color(0xFF1B7F73), Color(0xFFA6EDE2)),
    ORANGE(Effect.SPEEDY, Color(0xFFF08A2E), Color(0xFFC25E10), Color(0xFFFFD9A8)),
    PINK(Effect.CONFUSED, Color(0xFFE8629F), Color(0xFFB03A74), Color(0xFFFFC0DC)),
}
