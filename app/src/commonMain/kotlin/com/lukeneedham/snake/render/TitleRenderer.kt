package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * The title screen: a bouncing title, and a blinking line that tells the player to tap. With the story at the start the snake sleeps in
 * its den and the line, over the den, says to wake it.
 */
internal fun DrawScope.drawTitle(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val centerX = world.arena.width.toInt() / 2
    val bounce = (sin(world.time * 3f) * 1.5f).roundToInt()
    drawCenteredPixelText(texts.title, centerX, topInset + 18 + bounce, HudPalette.gold, scale = 3)
    if ((world.time * 1.6f).toInt() % 2 == 0) {
        if (world.intro != null) {
            val aboveHouse = (world.layout.houseY - world.cameraY).roundToInt() - wakeAboveHouse
            drawCenteredPixelText(texts.tapToWake, centerX, aboveHouse, HudPalette.white, scale = 2)
        } else {
            drawCenteredPixelText(texts.tapToStart, centerX, world.arena.height.toInt() - 30, HudPalette.white)
        }
    }
}

/** How far above the middle of the house the line that says to wake the snake is. */
private const val wakeAboveHouse = 66
