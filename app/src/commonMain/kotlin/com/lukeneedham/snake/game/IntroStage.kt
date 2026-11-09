package com.lukeneedham.snake.game

/**
 * The parts of the story and of the lessons at the start, in order. The player starts it with a tap, and then it plays by itself until
 * [Teaching]: taps do nothing before that. The lessons come after: the turn in the house, then out to the room of the tutorial, where
 * each lesson waits for the player to do what it teaches, and back home.
 */
enum class IntroStage(val seconds: Float?) {
    /** The snake is curled up asleep in its house, with its eyes shut, and the player has not woken it yet. */
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

    /** The player steers the snake round the house and out by the path to the left, which leads to the room of the tutorial. */
    Steering(null),

    /** In the room: the hunger bar shows for the first time, full, with an arrow and the words that say that the belly decreases, and it runs down. */
    Hunger(null),

    /** The sprint button shows, with an arrow: the player learns to sprint by holding the sprint corner, and sees it use up the belly. */
    Sprint(null),

    /** The player has sprinted: the words say that zooming makes the snake hungry. */
    Zoomed(IntroTuning.zoomedSeconds),

    /** The words say to eat so as not to starve, and a mouse runs in from the grass to the middle of the room, and the snake has to eat it. */
    Mouse(null),

    /** The words say that toads can be eaten too, and a toad hops in from the grass to the middle of the room, and the snake has to eat it. */
    Toad(null),

    /** The words say that toads poison, and the snake is dizzy, and has to stay so until it is over. */
    Dizzy(null),

    /** The words say to catch birds, and a bird flies in to the middle of the room, and the snake has to eat it. */
    Bird(null),

    /** The words say that birds give powers. */
    Powers(IntroTuning.powersSeconds),

    /** All is learned: an arrow shows the way home. */
    Return(null),

    /** The snake is home, the stones that closed the path up are gone, and an arrow shows the way to the stage. */
    Leaving(null),
    ;

    /** Whether this part of the story plays by itself, or waits for the player to wake the snake. */
    val isStory: Boolean get() = this < Teaching

    /** Whether this is one of the lessons that come in the room of the tutorial, from the hunger bar on. */
    val isLesson: Boolean get() = this >= Hunger
}
