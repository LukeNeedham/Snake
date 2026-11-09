package com.lukeneedham.snake.game

import kotlin.math.ceil

/** One power down of a toad (a power down) or power up of a bird that is on, with the time it has left. */
class StatusEntry(val powerDown: PowerDown?, val powerUp: PowerUp?, val remaining: Float, val duration: Float) {
    /** Power ups are shown with an arrow pointing up, power downs of the toads with one pointing down. */
    val isPowerUp: Boolean get() = powerUp != null

    /** 1 when it has just started, 0 when it runs out. */
    val fraction: Float get() = (remaining / duration).coerceIn(0f, 1f)

    /** The whole seconds left, counted up: the last fraction of a second still shows 1. */
    val seconds: Int get() = ceil(remaining).toInt()
}

/** Which power downs and power ups are on: the power downs come first, then the power ups. */
object ActiveStatuses {
    fun of(powerDowns: PowerDownTracker, powerUps: PowerUpTracker): List<StatusEntry> {
        val entries = ArrayList<StatusEntry>()
        for (powerDown in PowerDown.entries) {
            val remaining = powerDowns.remaining(powerDown)
            if (remaining > 0f) entries.add(StatusEntry(powerDown, null, remaining, powerDown.durationSeconds))
        }
        for (powerUp in PowerUp.entries) {
            val remaining = powerUps.remaining(powerUp)
            if (remaining > 0f) entries.add(StatusEntry(null, powerUp, remaining, powerUp.durationSeconds))
        }
        return entries
    }
}
