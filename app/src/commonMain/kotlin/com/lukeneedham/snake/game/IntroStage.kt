package com.lukeneedham.snake.game

/**
 * The parts of the story at the start, in order. The player starts it with a tap, and then it plays by itself until [Teaching]: taps
 * do nothing before that.
 */
enum class IntroStage(val seconds: Float?) {
    /** The snake is curled up asleep in its den, with its eyes shut, and the player has not woken it yet. */
    Sleeping(null),

    /** The snake wakes up and blinks. */
    Waking(IntroTuning.wakeSeconds),

    /** It sticks its tongue out. */
    Tasting(IntroTuning.tasteSeconds),

    /** Its stomach rumbles. */
    Rumbling(IntroTuning.rumbleSeconds),

    /** Three bubbles come up, the last one with a mouse in it. */
    Thinking(IntroTuning.thinkSeconds),

    /** The big bubble pops, and the snake starts to turn clockwise. */
    Popped(IntroTuning.popSeconds),

    /** The snake circles, and a text says that a tap turns it, until the player taps. */
    Teaching(null),

    /** The player steers the snake out of the den and along the path. */
    Steering(null),
    ;

    /** Whether this part of the story plays by itself, or waits for the player to wake the snake. */
    val isStory: Boolean get() = this < Teaching
}
