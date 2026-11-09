package com.lukeneedham.snake.game

import kotlin.math.max

/** Which power downs of the toads are on, how long they have left, and how strongly each one shows right now. */
class PowerDownTracker {
    private val remaining = FloatArray(PowerDown.entries.size)
    private val levels = FloatArray(PowerDown.entries.size)

    /** The toad whose poison was eaten last, shown as a banner for a moment. */
    var bannerKind: ToadKind? = null
        private set
    var bannerTime = 0f
        private set

    fun apply(kind: ToadKind) {
        remaining[kind.powerDown.ordinal] = kind.powerDown.durationSeconds
        bannerKind = kind
        bannerTime = bannerSeconds
    }

    /** Takes all the power downs off at once, and the banner with them: the tutorial starts again as it was. */
    fun clear() {
        remaining.fill(0f)
        levels.fill(0f)
        bannerKind = null
        bannerTime = 0f
    }

    /** 0 to 1: how strongly the power down shows, so that it fades in and out. */
    fun level(powerDown: PowerDown): Float = levels[powerDown.ordinal]

    fun remaining(powerDown: PowerDown): Float = remaining[powerDown.ordinal]

    fun isOn(powerDown: PowerDown): Boolean = remaining[powerDown.ordinal] > 0f

    /** [isCountingDown] lets the time run out; [isShowing] is false once the game is over, so that every power down fades away. */
    fun update(dt: Float, isCountingDown: Boolean, isShowing: Boolean) {
        for (powerDown in PowerDown.entries) {
            val i = powerDown.ordinal
            if (isCountingDown) remaining[i] = max(0f, remaining[i] - dt)
            val target = if (remaining[i] > 0f && isShowing) 1f else 0f
            levels[i] += (target - levels[i]).coerceIn(-dt * fadeSpeed, dt * fadeSpeed)
        }
        bannerTime = max(0f, bannerTime - dt)
    }

    private companion object {
        const val bannerSeconds = 2f
        const val fadeSpeed = 2.5f
    }
}
