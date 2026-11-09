package com.lukeneedham.snake.game

/** The numbers that shape how fast the snake gets hungry and what fills it up. The bar is 1 when full and 0 when empty. */
object HungerTuning {
    /** How much of the bar is lost every second: a full snake starves in about half a minute. */
    const val drainPerSecond = 0.035f

    /** While sprinting at full strength the bar drains this many times as fast. */
    const val sprintDrainFactor = 3f

    const val mouseFills = 0.3f

    /** A bird is a little more than a mouse: it is the power up that counts. */
    const val birdFills = 0.4f

    /** A toad is hardly a meal. */
    const val toadFills = 0.05f

    /** What was just eaten shows in its own colour for this long before the bar fills up into it. */
    const val gainShowSeconds = 0.5f

    /** How much of the bar the new part fills in per second. */
    const val gainFillPerSecond = 0.6f

    /** In the lesson of the hunger the bar drains this fast, to show that it does. */
    const val demoDrainPerSecond = 0.25f

    /** Below this the bar is low, and warns. */
    const val lowLevel = 0.25f
}
