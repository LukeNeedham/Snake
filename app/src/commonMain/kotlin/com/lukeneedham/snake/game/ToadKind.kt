package com.lukeneedham.snake.game

import androidx.compose.ui.graphics.Color

/** A colour of toad: each one is poisonous in its own way. */
enum class ToadKind(val powerDown: PowerDown, val body: Color, val shade: Color, val light: Color) {
    Purple(PowerDown.Hallucinating, Color(0xFF7A5CC8), Color(0xFF5238A0), Color(0xFFC9B8F5)),
    Teal(PowerDown.Imposters, Color(0xFF2FB5A3), Color(0xFF1B7F73), Color(0xFFA6EDE2)),
    Pink(PowerDown.Confused, Color(0xFFE8629F), Color(0xFFB03A74), Color(0xFFFFC0DC)),
    Grey(PowerDown.Hayfever, Color(0xFF7C8196), Color(0xFF444859), Color(0xFFD3D6E4)),
    Red(PowerDown.Greedy, Color(0xFFE5532D), Color(0xFF9A3016), Color(0xFFFFB79E)),
    Olive(PowerDown.Lazy, Color(0xFF9CAF3A), Color(0xFF66742A), Color(0xFFDCE8A0)),
    Brown(PowerDown.Cannibal, Color(0xFF9A6A3C), Color(0xFF5F3E1F), Color(0xFFE2BC8C)),
}
