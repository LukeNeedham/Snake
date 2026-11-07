package com.lukeneedham.snake.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.snake.game.Cell
import com.lukeneedham.snake.ui.GamePageContent
import com.lukeneedham.snake.ui.GameStatus
import com.lukeneedham.snake.ui.GameUiState
import com.lukeneedham.snake.ui.theme.AppTheme

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePage.png. */
@Preview
@Composable
private fun GamePageScreenshot() {
    val state = GameUiState(
        width = 15,
        height = 20,
        snake = listOf(
            Cell(9, 8), Cell(8, 8), Cell(7, 8), Cell(6, 8), Cell(6, 9), Cell(6, 10), Cell(7, 10), Cell(8, 10),
        ),
        food = Cell(11, 4),
        score = 5,
        best = 12,
        status = GameStatus.Running,
    )
    AppTheme { GamePageContent(state = state, onSwipe = {}, onTap = {}, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageGameOver.png. */
@Preview
@Composable
private fun GamePageGameOverScreenshot() {
    val state = GameUiState(
        width = 15,
        height = 20,
        snake = listOf(
            Cell(0, 6), Cell(1, 6), Cell(2, 6), Cell(3, 6), Cell(3, 7), Cell(3, 8), Cell(2, 8), Cell(1, 8), Cell(1, 9),
        ),
        food = Cell(9, 13),
        score = 6,
        best = 12,
        status = GameStatus.GameOver,
    )
    AppTheme { GamePageContent(state = state, onSwipe = {}, onTap = {}, onOpenSettings = {}) }
}
