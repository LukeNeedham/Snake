package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.IntroStage
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * What is on the screen during the story at the start: nothing while it plays by itself, the lesson once the bubble has popped,
 * and a hint for the way out while the player steers.
 */
internal fun DrawScope.drawIntroHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val intro = world.intro ?: return
    val centerX = world.arena.width.toInt() / 2
    when (intro.stage) {
        IntroStage.Teaching -> {
            // Where the big bubble was, on the screen
            val top = (world.den.denY - world.cameraY).roundToInt() - lessonAboveDen
            val bounce = (sin(world.time * 4f) * 1.2f).roundToInt()
            texts.tapToChangeDirection.lines().forEachIndexed { index, line ->
                drawCenteredPixelText(line, centerX, top + bounce + index * lineHeight, HudPalette.gold, scale = 2)
            }
        }
        IntroStage.Steering -> drawCenteredPixelText(texts.followThePath, centerX, topInset + hintFromTop, HudPalette.white)
        else -> Unit
    }
}

/** How far above the middle of the den the lesson starts, and how tall one of its lines is. */
private const val lessonAboveDen = 74
private const val lineHeight = 13

/** How far under the status bar the hint is. */
private const val hintFromTop = 18
