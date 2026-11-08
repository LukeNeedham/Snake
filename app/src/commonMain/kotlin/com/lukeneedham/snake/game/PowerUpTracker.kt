package com.lukeneedham.snake.game

import kotlin.math.max

/** Which power ups the snake has, and how long each one has left. They run at the same time and run out on their own. */
class PowerUpTracker {
    private val remaining = FloatArray(PowerUp.entries.size)

    /** The bird that was eaten last, shown as a banner for a moment. */
    var bannerKind: BirdKind? = null
        private set
    var bannerTime = 0f
        private set

    /** Eating a bird gives its power up: one that is already on starts over. */
    fun apply(kind: BirdKind) {
        remaining[kind.powerUp.ordinal] = kind.powerUp.durationSeconds
        bannerKind = kind
        bannerTime = bannerSeconds
    }

    fun remaining(powerUp: PowerUp): Float = remaining[powerUp.ordinal]

    fun isOn(powerUp: PowerUp): Boolean = remaining[powerUp.ordinal] > 0f

    fun update(dt: Float, isCountingDown: Boolean) {
        if (isCountingDown) {
            for (powerUp in PowerUp.entries) {
                val i = powerUp.ordinal
                remaining[i] = max(0f, remaining[i] - dt)
            }
        }
        bannerTime = max(0f, bannerTime - dt)
    }

    private companion object {
        const val bannerSeconds = 2f
    }
}
