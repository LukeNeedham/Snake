package com.lukeneedham.snake.game

/** What the power ups do in a level, and how long each one lasts. */
class PowerUpRules(
    /** The snake is this many times as fast. */
    val zoomSpeedFactor: Float = PowerUpTuning.zoomSpeedFactor,
    /** How much faster than normal the snake goes while it sprints flat out, and how many times as fast the hunger bar drains then. */
    val fastSprintBoost: Float = PowerUpTuning.fastSprintBoost,
    val fastSprintDrainFactor: Float = PowerUpTuning.fastSprintDrainFactor,
    /** The mouth reaches this many times as far. */
    val biggerMouthFactor: Float = PowerUpTuning.biggerMouthFactor,
    /** Animals notice the snake from this fraction of the distance. */
    val sneakyNoticeFactor: Float = PowerUpTuning.sneakyNoticeFactor,
    /** With the cheese, mice come this many times as fast, and this many more can be out at once. */
    val cheeseSpawnFactor: Float = PowerUpTuning.cheeseSpawnFactor,
    val cheeseExtraMice: Int = PowerUpTuning.cheeseExtraMice,
    /** How long each power up lasts, in seconds; one that is not here lasts as long as it usually does. */
    val durations: Map<PowerUp, Float> = emptyMap(),
) {
    fun duration(powerUp: PowerUp): Float = durations[powerUp] ?: powerUp.durationSeconds

    companion object {
        val standard = PowerUpRules()
    }
}
