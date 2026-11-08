package com.lukeneedham.snake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.Canvas as GraphicsCanvas
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.render.SceneTexts
import com.lukeneedham.snake.render.createBackground
import com.lukeneedham.snake.render.createFrontGrass
import com.lukeneedham.snake.render.drawScene
import com.lukeneedham.snake.render.presentScene
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * The game itself: the world is drawn into a small bitmap at one unit per art pixel, then scaled up without smoothing. Every
 * finger is told apart by its id, so that two thumbs can tap in turn; the world decides if a finger was a tap or a hold.
 */
@Composable
internal fun GameScene(
    world: GameWorld,
    texts: SceneTexts,
    onPress: (pointerId: Long) -> Unit,
    onRelease: (pointerId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
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
    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val currentOnPress by rememberUpdatedState(onPress)
    val currentOnRelease by rememberUpdatedState(onRelease)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        for (change in event.changes) {
                            if (change.changedToDownIgnoreConsumed()) currentOnPress(change.id.value)
                            if (change.changedToUpIgnoreConsumed()) currentOnRelease(change.id.value)
                        }
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
}

/** For Android Studio while building the scene: the title screen. */
@Preview
@Composable
private fun Preview() {
    GameScene(
        world = GameWorld(Arena(108f, 240f)),
        texts = sceneTexts(score = 0, bestScore = 0, isNewBest = false),
        onPress = {},
        onRelease = {},
    )
}
