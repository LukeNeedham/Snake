package com.lukeneedham.snake.game

/**
 * The parts of the story and of the lessons at the start, in order. The player starts it with a tap, and then it plays by itself until
 * [Teaching]: taps do nothing before that. The lessons along the path come after: each one waits for the player to do what it teaches.
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

    /** The player steers the snake out of the den and along the path, and learns to sprint half way along it. */
    Steering(null),

    /** The hunger bar shows for the first time, full, with an arrow, and runs down. */
    Hunger(null),

    /** A mouse sits on the path, and the snake has to eat it. */
    Mouse(null),

    /** The snake goes on a bit. */
    WalkToToad(null),

    /** A toad sits on the path, and the snake has to eat it. */
    Toad(null),

    /** The snake is dizzy, and has to stay so until it is over. */
    Dizzy(null),

    /** The snake goes on a bit. */
    WalkToBird(null),

    /** A bird sits on the path, and the snake has to eat it. */
    Bird(null),

    /** All is learned: the path leads on into the oval. */
    Leaving(null),
    ;

    /** Whether this part of the story plays by itself, or waits for the player to wake the snake. */
    val isStory: Boolean get() = this < Teaching

    /** Whether this is one of the lessons that come after the sprint. */
    val isLesson: Boolean get() = this >= Hunger
}
