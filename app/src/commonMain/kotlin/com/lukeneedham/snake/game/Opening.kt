package com.lukeneedham.snake.game

/** How a game opens: where the snake starts, and whether the story and the lessons are told. */
enum class Opening {
    /** The snake starts in the middle of the oval, with nothing to tell: a restart. */
    Oval,

    /** The first game: the snake sleeps in its den until it is woken, then the story, and the lessons in the room of the tutorial, to the left, before the stones on the path up are gone. */
    Tutorial,

    /** Every game after the tutorial: the snake wakes in its house, the path up is open, and no lessons are told. */
    Returning,
}
