package com.lukeneedham.snake.game

/** The numbers of the power ups. */
object PowerUpTuning {
    /** The snake is this many times as fast. */
    const val zoomSpeedFactor = 1.35f

    /** How much faster than normal the snake goes while it sprints flat out, and how many times as fast the hunger bar drains then. */
    const val fastSprintBoost = 1.4f
    const val fastSprintDrainFactor = 1.5f

    /** The mouth reaches this many times as far. */
    const val biggerMouthFactor = 1.8f

    /** Animals notice the snake from this fraction of the distance. */
    const val sneakyNoticeFactor = 0.4f

    /** With the cheese, mice come this many times as fast, and this many more can be out at once. */
    const val cheeseSpawnFactor = 2.5f
    const val cheeseExtraMice = 2
}
