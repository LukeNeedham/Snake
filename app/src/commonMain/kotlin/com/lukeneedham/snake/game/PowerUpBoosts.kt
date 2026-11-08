package com.lukeneedham.snake.game

/** What the power ups that are on change in the game: the numbers the rest of the world asks for. */
class PowerUpBoosts(private val powerUps: PowerUpTracker) {

    /** How many times as fast the snake goes. */
    val speedFactor: Float
        get() = if (powerUps.isOn(PowerUp.Zoom)) PowerUpTuning.zoomSpeedFactor else 1f

    /** How much faster the snake goes while it sprints flat out. */
    val sprintBoost: Float
        get() = if (powerUps.isOn(PowerUp.FastSprint)) PowerUpTuning.fastSprintBoost else SprintTuning.speedBoost

    /** How many times as fast the hunger bar drains while the snake sprints flat out. */
    val sprintDrainFactor: Float
        get() = if (powerUps.isOn(PowerUp.FastSprint)) PowerUpTuning.fastSprintDrainFactor else HungerTuning.sprintDrainFactor

    /** How many times as far the mouth reaches. */
    val mouthFactor: Float
        get() = if (powerUps.isOn(PowerUp.BiggerMouth)) PowerUpTuning.biggerMouthFactor else 1f

    /** Whether more mice come, and gather around the snake. */
    val attractsMice: Boolean
        get() = powerUps.isOn(PowerUp.Cheese)

    /** Whether the snake carries something in its mouth, so that it cannot eat. */
    val mouthIsFull: Boolean
        get() = powerUps.isOn(PowerUp.Cheese)

    /** Whether no toad comes out of the grass. */
    val keepsToadsAway: Boolean
        get() = powerUps.isOn(PowerUp.ToadFree)

    /** What the other animals feel of the snake. */
    val aura: SnakeAura
        get() = SnakeAura(
            noticeScale = if (powerUps.isOn(PowerUp.Sneaky)) PowerUpTuning.sneakyNoticeFactor else 1f,
            attractsMice = attractsMice,
        )
}
