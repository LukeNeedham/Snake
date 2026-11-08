package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.BirdKind
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.PowerUp
import com.lukeneedham.snake.game.ToadKind
import kotlin.math.roundToInt

private const val barWidth = 32

/**
 * One bar per effect of a toad and per power up of a bird that is on, at the bottom of the screen between the corners, in the colour
 * of its animal; each runs down with the time left. The effects come first, then the power ups.
 */
internal fun DrawScope.drawEffectBars(world: GameWorld) {
    var row = 0
    for (effect in Effect.entries) {
        val remaining = world.effects.remaining(effect)
        if (remaining <= 0f) continue
        val kind = ToadKind.entries.first { it.effect == effect }
        drawTimerBar(world, row, remaining / effect.durationSeconds, kind.body, kind.light)
        row++
    }
    for (powerUp in PowerUp.entries) {
        val remaining = world.powerUps.remaining(powerUp)
        if (remaining <= 0f) continue
        val palette = birdPalette(BirdKind.entries.first { it.powerUp == powerUp })
        drawTimerBar(world, row, remaining / powerUp.durationSeconds, palette.bar, palette.barLight)
        row++
    }
}

/** A bar with a small badge in front of it; [row] counts up from the bottom of the screen, and [fraction] is how much of the bar is left. */
private fun DrawScope.drawTimerBar(world: GameWorld, row: Int, fraction: Float, color: Color, light: Color) {
    val centerX = world.arena.width.toInt() / 2
    val y = world.arena.height.toInt() - 9 - row * 8
    pixelRect(centerX - 22, y - 1, 7, 7, PixelFont.outline)
    pixelRect(centerX - 21, y, 5, 5, color)
    pixel(centerX - 20, y + 1, light)
    pixelRect(centerX - 13, y, barWidth + 2, 5, PixelFont.outline)
    pixelRect(centerX - 12, y + 1, barWidth, 3, HudPalette.barBack)
    pixelRect(centerX - 12, y + 1, (barWidth * fraction).roundToInt(), 3, color)
}
