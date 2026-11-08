package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt

private const val barWidth = 44
private const val barHeight = 4

/**
 * The hunger bar under the score: it runs down, turns red and blinks when the snake is about to starve, and glows while it
 * sprints. What was just eaten shows as a lighter part at the end of the bar, which then fills in.
 */
internal fun DrawScope.drawHungerBar(world: GameWorld, topInset: Int) {
    val left = world.arena.width.toInt() / 2 - barWidth / 2
    val top = topInset + 17
    val hunger = world.hunger
    pixelRect(left - 1, top - 1, barWidth + 2, barHeight + 2, PixelFont.outline)
    pixelRect(left, top, barWidth, barHeight, HudPalette.barBack)
    val filled = (barWidth * hunger.filled).roundToInt()
    val total = (barWidth * hunger.level).roundToInt()
    if (filled > 0) {
        pixelRect(left, top, filled, barHeight, hungerColor(world))
        pixelRect(left, top, filled, 1, Color.White.copy(alpha = 0.35f))
    }
    // What was just eaten, in its own colour, until the bar fills up into it
    if (total > filled) {
        pixelRect(left + filled, top, total - filled, barHeight, HudPalette.hungerGain)
        pixelRect(left + filled, top, total - filled, 1, Color.White.copy(alpha = 0.6f))
    }
}

private fun hungerColor(world: GameWorld): Color = when {
    world.hunger.isLow -> if ((world.time * 4f).toInt() % 2 == 0) HudPalette.red else HudPalette.hungerLow
    world.sprint.level > 0.5f -> HudPalette.gold
    else -> HudPalette.hungerFull
}
