package com.lukeneedham.snake.game

/** The numbers that shape how the mice and the toads move. */
object CritterTuning {
    const val mouseSpeed = 26f
    const val hopHeight = 5f
    const val hopDuration = 0.42f

    /** A mouse sees the snake in front of it from this far away. */
    const val noticeDistance = 26f

    /** A mouse that runs from the snake is a little faster than the snake itself, so that it can get away. */
    const val fleeSpeed = 34f
    const val fleeSeconds = 0.9f
}
