package com.lukeneedham.snake.game

/** One drawn disc of the body. [index] is anchored to the skin, so the pattern moves with the snake. */
class BodyPoint(
    val x: Float,
    val y: Float,
    /** The unit direction the snake moves along here. */
    val dirX: Float,
    val dirY: Float,
    val index: Int,
    /** Where on the whole path travelled by the head this disc is. */
    val cumulative: Float,
    val distanceFromHead: Float,
    val distanceFromTail: Float,
)
