package com.lukeneedham.snake.ui

import com.lukeneedham.snake.game.SprintSide

data class SettingsUiState(
    /** Whether the debug pages are on (debug builds): the page shows the way to them only then. */
    val isDebugEnabled: Boolean,
    /** Which bottom corner of the screen sprints. */
    val sprintSide: SprintSide = SprintSide.Left,
)
