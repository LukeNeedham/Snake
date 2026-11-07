package com.lukeneedham.snake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import com.lukeneedham.snake.game.Cell
import com.lukeneedham.snake.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Draws the board, the snake and the food. */
@Composable
internal fun GameBoard(
    width: Int,
    height: Int,
    snake: List<Cell>,
    food: Cell?,
    modifier: Modifier = Modifier,
) {
    val theme = AppTheme.current
    Canvas(modifier.aspectRatio(width.toFloat() / height)) {
        val cell = size.width / width
        drawRoundRect(color = theme.surface, cornerRadius = CornerRadius(cell / 2))
        drawRoundRect(
            color = theme.outline,
            cornerRadius = CornerRadius(cell / 2),
            style = Stroke(width = 2f),
        )
        val inset = cell * 0.08f
        snake.forEachIndexed { index, part ->
            drawRoundRect(
                color = if (index == 0) theme.primary else theme.primary.copy(alpha = 0.75f),
                topLeft = Offset(part.x * cell + inset, part.y * cell + inset),
                size = Size(cell - 2 * inset, cell - 2 * inset),
                cornerRadius = CornerRadius(cell * 0.3f),
            )
        }
        if (food != null) {
            drawCircle(
                color = theme.error,
                radius = cell * 0.38f,
                center = Offset((food.x + 0.5f) * cell, (food.y + 0.5f) * cell),
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        GameBoard(
            width = 15,
            height = 20,
            snake = listOf(Cell(8, 8), Cell(7, 8), Cell(6, 8)),
            food = Cell(11, 5),
        )
    }
}
