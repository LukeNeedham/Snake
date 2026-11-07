package com.lukeneedham.snake.game

import kotlin.random.Random

/** Builds the engine with the size of the board. */
object SnakeEngineFactory {
    private const val BOARD_WIDTH = 15
    private const val BOARD_HEIGHT = 20

    fun create() = SnakeEngine(width = BOARD_WIDTH, height = BOARD_HEIGHT, random = Random.Default)
}
