package com.lukeneedham.snake.game

/** The parts of the story at the start, in order. The first four play by themselves; the player takes over in [Teaching]. */
enum class IntroStage(val seconds: Float?) {
    /** The snake is curled up asleep in its den. */
    Sleeping(IntroTuning.sleepSeconds),

    /** Its stomach rumbles. */
    Rumbling(IntroTuning.rumbleSeconds),

    /** Three bubbles come up, the last one with a mouse in it. */
    Thinking(IntroTuning.thinkSeconds),

    /** The big bubble pops. */
    Popped(IntroTuning.popSeconds),

    /** The snake circles and a text says how to turn it, until the player taps. */
    Teaching(null),

    /** The player steers the snake out of the den and along the path. */
    Steering(null),
    ;

    /** Whether this part of the story plays by itself. */
    val isStory: Boolean get() = this < Teaching
}
