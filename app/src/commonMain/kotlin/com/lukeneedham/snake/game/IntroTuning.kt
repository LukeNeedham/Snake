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

    /** In the lesson of the hunger the bar is shown for this long before the mouse comes, and runs down to this level. */
    const val hungerTextSeconds = 2.2f
    const val hungerDemoLevel = 0.5f

    /** How far the snake goes between one lesson and the next. */
    const val walkDistance = 150f

    /** How far ahead of the snake the animal of a lesson sits, and how far behind it the animal has to be to be put ahead again. */
    const val lessonAhead = 70f
    const val lessonBehind = 55f

    /** How long the sprint corner has to be held, without letting go, to have learned to zoom. */
    const val zoomSeconds = 2f
}
