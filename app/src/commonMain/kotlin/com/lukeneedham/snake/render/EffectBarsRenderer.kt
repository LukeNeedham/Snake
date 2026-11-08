package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.ToadKind
import kotlin.math.roundToInt

private const val barWidth = 32

/** One bar per effect that is on, at the bottom of the screen between the corners, in the colour of its toad; it runs down with the time left. */
internal fun DrawScope.drawEffectBars(world: GameWorld) {
    val centerX = world.arena.width.toInt() / 2
    val height = world.arena.height.toInt()
    var row = 0
    for (effect in Effect.entries) {
        val remaining = world.effects.remaining(effect)
        if (remaining <= 0f) continue
        val kind = ToadKind.entries.first { it.effect == effect }
        val y = height - 9 - row * 8
        pixelRect(centerX - 22, y - 1, 7, 7, PixelFont.outline)
        pixelRect(centerX - 21, y, 5, 5, kind.body)
        pixel(centerX - 20, y + 1, kind.light)
        pixelRect(centerX - 13, y, barWidth + 2, 5, PixelFont.outline)
        pixelRect(centerX - 12, y + 1, barWidth, 3, HudPalette.barBack)
        pixelRect(centerX - 12, y + 1, (barWidth * remaining / effect.durationSeconds).roundToInt(), 3, kind.body)
        row++
    }
}
