package com.lukeneedham.snake.game

/** The timing of the story at the start: how long each part lasts, in seconds. */
object IntroTuning {
    /** The snake wakes up and blinks, then flicks out its tongue, then its stomach rumbles, then it thinks of a mouse, then the bubble pops. */
    const val wakeSeconds = 2.0f
    const val tasteSeconds = 1.2f
    const val rumbleSeconds = 2.6f
    const val thinkSeconds = 3.4f
    const val popSeconds = 1.0f

    /** How long after one thought bubble appears the next one does. */
    const val bubbleGap = 0.55f

    /** How long a bubble takes to grow to its size. */
    const val bubbleGrowSeconds = 0.3f

    /** How long the rumble takes to fade in and out. */
    const val rumbleFadeSeconds = 0.4f

    /** In the lesson of the hunger the bar is shown for this long, and runs down to this level (two steps of a fifth) to show that it does. */
    const val hungerTextSeconds = 2.0f
    const val hungerDemoLevel = 0.6f

    /** The sprint makes the bar run down on its own, but not below this level: nothing starves in the lessons. */
    const val sprintDemoFloor = 0.25f

    /** How long the words that zooming makes the snake hungry, and that birds give powers, are shown by themselves. */
    const val zoomedSeconds = 2.8f
    const val powersSeconds = 3.0f

    /** How long after a lesson begins its animal comes in from the grass. */
    const val mouseDelay = 1.2f
    const val toadDelay = 1.2f
    const val birdDelay = 1.2f

    /** How long the sprint corner has to be held, without letting go, to have learned to zoom. */
    const val zoomSeconds = 2f
}
