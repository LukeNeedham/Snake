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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import com.lukeneedham.snake.game.PressTarget
import com.lukeneedham.snake.render.SceneTexts
import com.lukeneedham.snake.render.createBackground
import com.lukeneedham.snake.render.createFrontGrass
import com.lukeneedham.snake.render.drawScene
import com.lukeneedham.snake.render.presentScene
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * The game itself: the world is drawn into a small bitmap at one unit per art pixel, then scaled up without smoothing. Every
 * finger is told apart by its id: one that goes down in the sprint zone sprints until it comes up, one on a button presses it, any
 * other is a tap. So one thumb can sprint while the other turns.
 */
@Composable
internal fun GameScene(
    world: GameWorld,
    texts: SceneTexts,
    onPress: (pointerId: Long, target: PressTarget) -> Unit,
    onRelease: (pointerId: Long) -> Unit,
    onPageDrag: (pages: Float) -> Unit = {},
    onPageRelease: () -> Unit = {},
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
    val background = remember(world.layout, density, layoutDirection) { createBackground(world.layout, density, layoutDirection) }
    val frontGrass = remember(world.layout) { createFrontGrass(world.layout) }
    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val bottomInsetPx = WindowInsets.safeDrawing.getBottom(density)
    val currentOnPress by rememberUpdatedState(onPress)
    val currentOnRelease by rememberUpdatedState(onRelease)
    val currentOnPageDrag by rememberUpdatedState(onPageDrag)
    val currentOnPageRelease by rememberUpdatedState(onPageRelease)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(world, topInsetPx, bottomInsetPx) {
                // Fingers that went down on the open overlay: they press when they let go, unless they dragged the pages
                val overlayTouches = HashMap<Long, OverlayTouch>()
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        for (change in event.changes) {
                            val id = change.id.value
                            val scale = size.width.toFloat() / sceneWidth
                            arena.bottomInset = bottomInsetPx / scale
                            if (change.changedToDownIgnoreConsumed()) {
                                val target = PressTarget.at(
                                    world = world,
                                    topInset = (topInsetPx / scale).toInt(),
                                    x = change.position.x / scale,
                                    y = change.position.y / scale,
                                )
                                if (world.isMenuOpen) {
                                    overlayTouches[id] = OverlayTouch(target, change.position.x)
                                } else {
                                    currentOnPress(id, target)
                                }
                            } else if (change.changedToUpIgnoreConsumed()) {
                                val touch = overlayTouches.remove(id)
                                if (touch != null) {
                                    if (touch.isDragging) currentOnPageRelease() else currentOnPress(id, touch.target)
                                }
                                currentOnRelease(id)
                            } else {
                                val touch = overlayTouches[id]
                                if (touch != null) {
                                    val moved = touch.moveTo(change.position.x, slop = dragSlop * scale)
                                    if (moved != 0f) currentOnPageDrag(moved / scale / sceneWidth)
                                }
                            }
                        }
                    }
                }
            },
    ) {
        // Reading the tick makes this draw again after every step of the world
        if (world.tick < 0L) return@Canvas
        val scale = size.width / sceneWidth
        val topInset = (topInsetPx / scale).toInt()
        arena.bottomInset = bottomInsetPx / scale
        sceneDrawScope.draw(density, layoutDirection, sceneCanvas, Size(sceneWidth.toFloat(), sceneHeight.toFloat())) {
            drawScene(world, background, frontGrass, texts, topInset)
        }
        presentScene(sceneBitmap, world)
        drawSystemBarFades(topInsetPx.toFloat(), bottomInsetPx.toFloat())
    }
}

/** How dark the edge of the screen is under the status bar and the navigation bar. */
private const val systemBarFadeAlpha = 0.7f

/**
 * The game is drawn edge to edge, behind the system bars. So that their icons can be read, the scene fades to black under them: from
 * clear on the side of the game to [systemBarFadeAlpha] black at the edge of the screen.
 */
private fun DrawScope.drawSystemBarFades(topBarHeight: Float, bottomBarHeight: Float) {
    val edge = Color.Black.copy(alpha = systemBarFadeAlpha)
    if (topBarHeight > 0f) {
        drawRect(
            brush = Brush.verticalGradient(listOf(edge, Color.Transparent), startY = 0f, endY = topBarHeight),
            topLeft = Offset.Zero,
            size = Size(size.width, topBarHeight),
        )
    }
    if (bottomBarHeight > 0f) {
        val top = size.height - bottomBarHeight
        drawRect(
            brush = Brush.verticalGradient(listOf(Color.Transparent, edge), startY = top, endY = size.height),
            topLeft = Offset(0f, top),
            size = Size(size.width, bottomBarHeight),
        )
    }
}

/** The distance in world units that a finger on the overlay has to move before it drags the pages instead of pressing. */
private const val dragSlop = 4f

/** A finger that went down on the open pause overlay on [target]; it is dragging the pages once it has moved far enough. */
private class OverlayTouch(val target: PressTarget, private var lastX: Float) {
    private val startX = lastX
    var isDragging = false
        private set

    /** The finger moved to [x]: returns how far it dragged the pages since the last move, in pixels, or 0 if it is not dragging. */
    fun moveTo(x: Float, slop: Float): Float {
        if (!isDragging && kotlin.math.abs(x - startX) > slop) {
            isDragging = true
            val moved = x - startX
            lastX = x
            return moved
        }
        if (!isDragging) return 0f
        val moved = x - lastX
        lastX = x
        return moved
    }
}

/** For Android Studio while building the scene: the title screen. */
@Preview
@Composable
private fun Preview() {
    GameScene(
        world = GameWorld(Arena(108f, 240f)),
        texts = sceneTexts(score = 0, bestScore = 0, isNewBest = false),
        onPress = { _, _ -> },
        onRelease = {},
    )
}
