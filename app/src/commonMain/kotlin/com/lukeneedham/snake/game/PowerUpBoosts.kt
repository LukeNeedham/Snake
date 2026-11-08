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

    val attractsMice: Boolean
        get() = powerUps.isOn(PowerUp.Pheromones)

    /** What the other animals feel of the snake. */
    val aura: SnakeAura
        get() = SnakeAura(
            noticeScale = if (powerUps.isOn(PowerUp.Sneaky)) PowerUpTuning.sneakyNoticeFactor else 1f,
            repelsToads = powerUps.isOn(PowerUp.ToadRepellent),
        )
}
