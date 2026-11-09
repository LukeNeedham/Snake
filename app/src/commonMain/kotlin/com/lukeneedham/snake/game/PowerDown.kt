package com.lukeneedham.snake.game

/** The bad things a poisonous toad does to the snake. */
enum class PowerDown(val durationSeconds: Float) {
    /** The whole screen turns wavy. */
    Hallucinating(9f),

    /** Every animal but the snake looks like one of a few other animals, and keeps that look for as long as it lasts. They all shuffle along in the same jerky way. */
    Imposters(9f),

    /** The snake ignores taps. */
    Confused(2.5f),

    /** Hayfever: the snake dies the moment it touches the grass. Without it, it just bounces off the edge of the oval. */
    Hayfever(12f),

    /** Greedy: what the snake eats feeds it only half as much. */
    Greedy(10f),

    /** The hunger bar drains faster. No toad gives it for now, until it gets a better name. */
    Ravenous(10f),

    /** Lazy: the snake goes slower, both normally and while it sprints. */
    Lazy(10f),

    /** Cannibal: the snake dies if it touches itself. Without it, the snake can cross its own body safely. The brown toad gives it. */
    Cannibal(12f),
}
