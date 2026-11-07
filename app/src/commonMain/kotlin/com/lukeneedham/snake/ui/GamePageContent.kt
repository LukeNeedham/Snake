package com.lukeneedham.snake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.snake.game.Cell
import com.lukeneedham.snake.game.Direction
import com.lukeneedham.snake.generated.resources.Res
import com.lukeneedham.snake.generated.resources.game_best
import com.lukeneedham.snake.generated.resources.game_over_hint
import com.lukeneedham.snake.generated.resources.game_over_title
import com.lukeneedham.snake.generated.resources.game_paused_hint
import com.lukeneedham.snake.generated.resources.game_paused_title
import com.lukeneedham.snake.generated.resources.game_ready_hint
import com.lukeneedham.snake.generated.resources.game_ready_title
import com.lukeneedham.snake.generated.resources.game_score
import com.lukeneedham.snake.generated.resources.game_settings
import com.lukeneedham.snake.ui.theme.AppTheme
import kotlin.math.abs
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/** All of the page's UI. `internal`: the screenshots of the app use it too. */
@Composable
internal fun GamePageContent(
    state: GameUiState,
    onSwipe: (Direction) -> Unit,
    onTap: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val theme = AppTheme.current
    val currentOnSwipe by rememberUpdatedState(onSwipe)
    val currentOnTap by rememberUpdatedState(onTap)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .safeDrawingPadding()
            .padding(16.dp)
            .pointerInput(Unit) { detectTapGestures { currentOnTap() } }
            .pointerInput(Unit) {
                var total = Offset.Zero
                val threshold = 24.dp.toPx()
                detectDragGestures(
                    onDragStart = { total = Offset.Zero },
                    onDrag = { _, drag ->
                        total += drag
                        if (abs(total.x) > threshold || abs(total.y) > threshold) {
                            currentOnSwipe(
                                if (abs(total.x) > abs(total.y)) {
                                    if (total.x > 0) Direction.Right else Direction.Left
                                } else {
                                    if (total.y > 0) Direction.Down else Direction.Up
                                },
                            )
                            total = Offset.Zero
                        }
                    },
                )
            },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                BasicText(
                    text = stringResource(Res.string.game_score, state.score),
                    style = TextStyle(
                        color = theme.onBackground,
                        fontFamily = theme.fontFamily,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                BasicText(
                    text = stringResource(Res.string.game_best, state.best),
                    style = TextStyle(
                        color = theme.outline,
                        fontFamily = theme.fontFamily,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                )
            }
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(theme.surface)
                    .clickable(onClick = onOpenSettings)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = stringResource(Res.string.game_settings),
                    style = TextStyle(
                        color = theme.onSurface,
                        fontFamily = theme.fontFamily,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                )
            }
        }
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            GameBoard(width = state.width, height = state.height, snake = state.snake, food = state.food)
            when (state.status) {
                GameStatus.Running -> Unit
                GameStatus.Ready -> Message(Res.string.game_ready_title, Res.string.game_ready_hint)
                GameStatus.Paused -> Message(Res.string.game_paused_title, Res.string.game_paused_hint)
                GameStatus.GameOver -> Message(Res.string.game_over_title, Res.string.game_over_hint)
            }
        }
    }
}

/** What the board says when the game is not running. */
@Composable
private fun Message(title: StringResource, hint: StringResource) {
    val theme = AppTheme.current
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(theme.background.copy(alpha = 0.85f))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = stringResource(title),
            style = TextStyle(
                color = theme.onBackground,
                fontFamily = theme.fontFamily,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            text = stringResource(hint),
            style = TextStyle(
                color = theme.onBackground,
                fontFamily = theme.fontFamily,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

/** For Android Studio while building the page: the game running. */
@Preview
@Composable
private fun Preview() {
    AppTheme {
        GamePageContent(
            state = GameUiState(
                width = 15,
                height = 20,
                snake = listOf(Cell(8, 8), Cell(7, 8), Cell(6, 8), Cell(6, 9)),
                food = Cell(11, 5),
                score = 1,
                best = 4,
                status = GameStatus.Running,
            ),
            onSwipe = {},
            onTap = {},
            onOpenSettings = {},
        )
    }
}

@Preview
@Composable
private fun PreviewReady() {
    AppTheme {
        GamePageContent(
            state = GameUiState(
                width = 15,
                height = 20,
                snake = listOf(Cell(7, 10), Cell(6, 10), Cell(5, 10)),
                food = Cell(11, 5),
                score = 0,
                best = 0,
                status = GameStatus.Ready,
            ),
            onSwipe = {},
            onTap = {},
            onOpenSettings = {},
        )
    }
}
