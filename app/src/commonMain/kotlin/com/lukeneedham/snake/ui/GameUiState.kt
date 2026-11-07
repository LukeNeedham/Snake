package com.lukeneedham.snake.ui

import com.lukeneedham.snake.game.Cell

data class GameUiState(
    val width: Int,
    val height: Int,
    /** The head first. */
    val snake: List<Cell>,
    val food: Cell?,
    val score: Int,
    /** The best score since the app started. */
    val best: Int,
    val status: GameStatus,
)
