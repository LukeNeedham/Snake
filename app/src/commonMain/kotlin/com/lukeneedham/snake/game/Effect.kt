package com.lukeneedham.snake.game

/** The bad things a poisonous toad does to the snake. */
enum class Effect(val durationSeconds: Float) {
    /** The whole screen turns wavy. */
    Hallucinating(9f),

    /** Every animal but the snake looks like one of a few other animals, and keeps that look for as long as it lasts. */
    Disguised(9f),

    /** The snake ignores taps. */
    Confused(2.5f),

    /** Hay fever: the snake dies the moment it touches the grass. Without it, it just bounces off the edge of the oval. */
    HayFever(12f),

    /** Ravenous: the hunger bar drains faster. */
    Ravenous(10f),

    /** Sluggish: the snake goes slower, both normally and while it sprints. */
    Sluggish(10f),

    /** Cannibal: the snake dies if it touches itself. Without it, the snake can cross its own body safely. The brown toad gives it. */
    Cannibal(12f),
}
