package com.lukeneedham.snake.game

/** The timing of the story at the start: how long each part lasts, in seconds. */
object IntroTuning {
    const val sleepSeconds = 1.0f
    const val rumbleSeconds = 2.6f
    const val thinkSeconds = 3.6f
    const val popSeconds = 0.8f

    /** How long after one thought bubble appears the next one does. */
    const val bubbleGap = 0.55f

    /** How long a bubble takes to grow to its size. */
    const val bubbleGrowSeconds = 0.3f

    /** How fast the snake curls round while it sleeps and thinks, and while the bubble pops: a part of its normal speed. */
    const val curledSpeed = 0.25f

    /** How long the rumble takes to fade in and out. */
    const val rumbleFadeSeconds = 0.3f

    /** How hard the screen shakes while the stomach rumbles. */
    const val rumbleShake = 0.6f
}
