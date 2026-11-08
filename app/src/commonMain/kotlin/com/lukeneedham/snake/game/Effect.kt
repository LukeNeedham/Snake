package com.lukeneedham.snake.game

/** The bad things a poisonous toad does to the snake. */
enum class Effect(val durationSeconds: Float) {
    /** The whole screen turns wavy. */
    HALLUCINATING(9f),

    /** Every animal but the snake looks like a random animal. */
    DISGUISED(9f),

    /** The snake moves faster. */
    SPEEDY(6f),

    /** The snake ignores taps. */
    CONFUSED(3.5f),
}
