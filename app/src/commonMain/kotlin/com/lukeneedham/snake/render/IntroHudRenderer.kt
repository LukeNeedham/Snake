package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.IntroStage
import com.lukeneedham.snake.game.SprintSide
import com.lukeneedham.snake.game.SprintZone
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * What is on the screen during the story at the start: nothing while it plays by itself, the lesson of the turn once the bubble has popped,
 * and, from half way along the path, the sprint button with an arrow and the lesson of the sprint until the player has learned it.
 */
internal fun DrawScope.drawIntroHud(world: GameWorld, texts: SceneTexts) {
    val intro = world.intro ?: return
    when (intro.stage) {
        IntroStage.Teaching -> {
            // Where the big bubble was, on the screen
            val top = (world.den.denY - world.cameraY).roundToInt() - lessonAboveDen
            val bounce = (sin(world.time * 4f) * 1.2f).roundToInt()
            drawCenteredPixelText(texts.tapToTurn, world.arena.width.toInt() / 2, top + bounce, HudPalette.gold, scale = 2)
        }
        IntroStage.Steering -> if (intro.reachedHalfway) {
            drawSprintButton(world)
            if (!intro.hasZoomed) drawZoomLesson(world, texts)
        }
        else -> Unit
    }
}

/** An arrow that bounces over the sprint button, and the words that say what to do with it. */
private fun DrawScope.drawZoomLesson(world: GameWorld, texts: SceneTexts) {
    val arena = world.arena
    val centerX = SprintZone.buttonX(arena, world.sprintSide).roundToInt()
    val bounce = (sin(world.time * 6f) * 2f).roundToInt()
    val tipY = SprintZone.buttonY(arena).roundToInt() - SprintZone.buttonRadius.toInt() - 3 + bounce
    // An arrow that points down at the button: a stem and a head
    pixelRect(centerX - 2, tipY - 11, 5, 6, PixelFont.outline)
    pixelRect(centerX - 1, tipY - 10, 3, 5, HudPalette.gold)
    for (row in 0..4) {
        pixelRect(centerX - 4 + row - 1, tipY - 5 + row - 1, 9 - row * 2 + 2, 3, PixelFont.outline)
    }
    for (row in 0..4) {
        pixelRect(centerX - 4 + row, tipY - 5 + row, 9 - row * 2, 1, HudPalette.gold)
    }
    val textWidth = PixelFont.width(texts.holdToZoom, 2)
    val left = when (world.sprintSide) {
        SprintSide.Left -> zoomTextMargin
        SprintSide.Right -> arena.width.toInt() - zoomTextMargin - textWidth
    }
    drawPixelText(texts.holdToZoom, left, tipY - 11 - 4 - PixelFont.glyphHeight * 2, HudPalette.gold, scale = 2)
}

/** How far above the middle of the den the lesson of the turn is. */
private const val lessonAboveDen = 66

/** How far the words of the lesson of the sprint are from the edge of the screen. */
private const val zoomTextMargin = 3
