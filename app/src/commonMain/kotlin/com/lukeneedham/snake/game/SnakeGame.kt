package com.lukeneedham.snake.game

/** An immutable snapshot of a game. */
data class SnakeGame(
    val width: Int,
    val height: Int,
    /** The cells of the snake, the head first. */
    val snake: List<Cell>,
    val direction: Direction,
    /** Null only when the snake fills the whole board. */
    val food: Cell?,
    val score: Int,
    val isOver: Boolean,
)
