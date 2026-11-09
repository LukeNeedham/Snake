package com.lukeneedham.snake.game

import kotlin.math.max

/** Which effects of the toads are on, how long they have left, and how strongly each one shows right now. */
class EffectTracker {
    private val remaining = FloatArray(Effect.entries.size)
    private val levels = FloatArray(Effect.entries.size)

    /** The toad whose poison was eaten last, shown as a banner for a moment. */
    var bannerKind: ToadKind? = null
        private set
    var bannerTime = 0f
        private set

    fun apply(kind: ToadKind) {
        remaining[kind.effect.ordinal] = kind.effect.durationSeconds
        bannerKind = kind
        bannerTime = bannerSeconds
    }

    /** Turns an effect on without a toad or a banner: the tests use it for the effects that no toad gives. */
    internal fun turnOn(effect: Effect) {
        remaining[effect.ordinal] = effect.durationSeconds
    }

    /** 0 to 1: how strongly the effect shows, so that it fades in and out. */
    fun level(effect: Effect): Float = levels[effect.ordinal]

    fun remaining(effect: Effect): Float = remaining[effect.ordinal]

    fun isOn(effect: Effect): Boolean = remaining[effect.ordinal] > 0f

    /** [isCountingDown] lets the time run out; [isShowing] is false once the game is over, so that every effect fades away. */
    fun update(dt: Float, isCountingDown: Boolean, isShowing: Boolean) {
        for (effect in Effect.entries) {
            val i = effect.ordinal
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
