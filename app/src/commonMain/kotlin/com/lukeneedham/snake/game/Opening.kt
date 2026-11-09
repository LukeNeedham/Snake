package com.lukeneedham.snake.game

/** How a game opens: where the snake starts, and whether the story and the lessons are told. */
enum class Opening {
    /** The snake starts in the middle of the oval, with nothing to tell: a restart. */
    Oval,

    /** The first game: the snake sleeps in its den until it is woken, then the story, and the lessons along a long path to the oval. */
    Tutorial,

    /** Every game after that has been learned: the snake wakes in its den and a short path leads to the oval. */
    ShortPath,
}
