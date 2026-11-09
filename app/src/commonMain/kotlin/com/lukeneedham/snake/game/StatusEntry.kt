package com.lukeneedham.snake.game

import kotlin.math.ceil

/** One effect of a toad (a power down) or power up of a bird that is on, with the time it has left. */
class StatusEntry(val effect: Effect?, val powerUp: PowerUp?, val remaining: Float, val duration: Float) {
    /** Power ups are shown with an arrow pointing up, effects of the toads with one pointing down. */
    val isPowerUp: Boolean get() = powerUp != null

    /** 1 when it has just started, 0 when it runs out. */
    val fraction: Float get() = (remaining / duration).coerceIn(0f, 1f)

    /** The whole seconds left, counted up: the last fraction of a second still shows 1. */
    val seconds: Int get() = ceil(remaining).toInt()
}

/** Which effects and power ups are on: the effects come first, then the power ups. */
object ActiveStatuses {
    fun of(effects: EffectTracker, powerUps: PowerUpTracker): List<StatusEntry> {
        val entries = ArrayList<StatusEntry>()
        for (effect in Effect.entries) {
            val remaining = effects.remaining(effect)
            if (remaining > 0f) entries.add(StatusEntry(effect, null, remaining, effect.durationSeconds))
        }
        for (powerUp in PowerUp.entries) {
            val remaining = powerUps.remaining(powerUp)
            if (remaining > 0f) entries.add(StatusEntry(null, powerUp, remaining, powerUp.durationSeconds))
        }
        return entries
    }
}
