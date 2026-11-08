package com.lukeneedham.snake.ui

import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld

/**
 * What the page shows. The [world] is the live simulation, drawn every frame by reading its tick: only the rest changes the
 * state, and only when a phase or a score changes.
 */
data class GameUiState(
    val world: GameWorld,
    val phase: GamePhase,
    val score: Int,
    val bestScore: Int,
    /** Whether the score of the game that just ended beats the best one. */
    val isNewBest: Boolean,
)
