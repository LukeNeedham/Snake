package com.lukeneedham.snake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.Canvas as GraphicsCanvas
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.generated.resources.Res
import com.lukeneedham.snake.generated.resources.best_value
import com.lukeneedham.snake.generated.resources.effect_confused
import com.lukeneedham.snake.generated.resources.effect_disguised
import com.lukeneedham.snake.generated.resources.effect_hallucinating
import com.lukeneedham.snake.generated.resources.effect_speedy
import com.lukeneedham.snake.generated.resources.game_over
import com.lukeneedham.snake.generated.resources.game_title
import com.lukeneedham.snake.generated.resources.new_best
import com.lukeneedham.snake.generated.resources.paused
import com.lukeneedham.snake.generated.resources.score_value
import com.lukeneedham.snake.generated.resources.settings_title
import com.lukeneedham.snake.generated.resources.tap_to_resume
import com.lukeneedham.snake.generated.resources.tap_to_retry
import com.lukeneedham.snake.generated.resources.tap_to_start
import com.lukeneedham.snake.render.SceneTexts
import com.lukeneedham.snake.render.createBackground
import com.lukeneedham.snake.render.createFrontGrass
import com.lukeneedham.snake.render.drawScene
import com.lukeneedham.snake.render.presentScene
import com.lukeneedham.snake.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * All of the page's UI: the scene is drawn into a small bitmap at one unit per art pixel, then scaled up without smoothing.
 * `internal`: the screenshots of the app use it too.
 */
@Composable
internal fun GamePageContent(
    state: GameUiState,
    onTap: () -> Unit,
    onSceneSize: (widthPx: Int, heightPx: Int) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val world = state.world
    val arena = world.arena
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val sceneWidth = arena.width.toInt()
    val sceneHeight = arena.height.toInt()

    val sceneBitmap = remember(arena) { ImageBitmap(sceneWidth, sceneHeight) }
    val sceneCanvas = remember(sceneBitmap) { GraphicsCanvas(sceneBitmap) }
    val sceneDrawScope = remember { CanvasDrawScope() }
    val background = remember(arena, density, layoutDirection) { createBackground(arena, density, layoutDirection) }
    val frontGrass = remember(arena) { createFrontGrass(arena) }

    val texts = SceneTexts(
        title = stringResource(Res.string.game_title),
        tapToStart = stringResource(Res.string.tap_to_start),
        gameOver = stringResource(Res.string.game_over),
        score = stringResource(Res.string.score_value, state.score),
        best = stringResource(Res.string.best_value, state.bestScore),
        newBest = if (state.isNewBest) stringResource(Res.string.new_best) else "",
        tapToRetry = stringResource(Res.string.tap_to_retry),
        paused = stringResource(Res.string.paused),
        tapToResume = stringResource(Res.string.tap_to_resume),
        effectNames = Effect.entries.map { effect ->
            when (effect) {
                Effect.HALLUCINATING -> stringResource(Res.string.effect_hallucinating)
                Effect.DISGUISED -> stringResource(Res.string.effect_disguised)
                Effect.SPEEDY -> stringResource(Res.string.effect_speedy)
                Effect.CONFUSED -> stringResource(Res.string.effect_confused)
            }
        },
    )
    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val currentOnTap by rememberUpdatedState(onTap)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1C4528)).onSizeChanged { onSceneSize(it.width, it.height) }) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // Every finger that goes down is a tap, so that two thumbs can tap in turn
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.any { it.changedToDownIgnoreConsumed() }) currentOnTap()
                        }
                    }
                },
        ) {
            // Reading the tick makes this draw again after every step of the world
            if (world.tick < 0L) return@Canvas
            val topInset = (topInsetPx / (size.width / sceneWidth)).toInt()
            sceneDrawScope.draw(density, layoutDirection, sceneCanvas, Size(sceneWidth.toFloat(), sceneHeight.toFloat())) {
                drawScene(world, background, frontGrass, texts, topInset)
            }
            presentScene(sceneBitmap, world)
        }

        if (state.phase == GamePhase.READY || state.phase == GamePhase.GAME_OVER) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(8.dp)
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .background(Color(0x66000000), RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenSettings)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = stringResource(Res.string.settings_title),
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                    ),
                )
            }
        }
    }
}

/** For Android Studio while building the page: the title screen. */
@Preview
@Composable
private fun Preview() {
    val world = GameWorld(Arena(108f, 234f))
    AppTheme {
        GamePageContent(
            state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 0, isNewBest = false),
            onTap = {},
            onSceneSize = { _, _ -> },
            onOpenSettings = {},
        )
    }
}
