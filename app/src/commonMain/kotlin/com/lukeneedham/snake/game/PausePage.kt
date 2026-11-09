package com.lukeneedham.snake.game

/** The pages of the pause overlay, left to right: they are all the same overlay, with a different page selected. */
enum class PausePage {
    /** The buttons: resume, restart and settings. */
    Pause,

    /** The stomach and how much each kind of animal fills it. */
    Hunger,

    /** The power downs and power ups that are on. */
    Status,
}
