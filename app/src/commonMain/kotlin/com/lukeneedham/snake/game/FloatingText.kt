package com.lukeneedham.snake.game

import androidx.compose.ui.graphics.Color

/** A short text that floats up and fades, like the +1 of a mouse. */
class FloatingText(
    val text: String,
    var x: Float,
    var y: Float,
    val color: Color,
    val maxLife: Float = 1.1f,
) {
    var life = maxLife
        private set

    fun update(dt: Float) {
        life -= dt
        y -= 9f * dt
    }
}
