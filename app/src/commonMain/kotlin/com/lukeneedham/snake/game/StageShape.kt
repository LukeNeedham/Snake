package com.lukeneedham.snake.game

/**
 * The shape of the stage of a level: its oval, as a fraction of the biggest one that fits the screen, wide and high. Smaller than 1 leaves
 * more grass around it, and the snake less room to live in.
 */
class StageShape(val widthFraction: Float = 1f, val heightFraction: Float = 1f) {
    companion object {
        /** The biggest oval that fits the screen. */
        val full = StageShape()
    }
}
