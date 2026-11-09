package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.BirdKind
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.PowerUp
import com.lukeneedham.snake.game.SprintSide
import com.lukeneedham.snake.game.SprintZone
import com.lukeneedham.snake.game.ToadKind

/**
 * One arrow per effect of a toad (pointing down) and per power up of a bird (pointing up) that is on, in a column up the right edge of
 * the screen from the bottom corner, where there is always grass behind them, in the colour of its animal; each one empties from the top down as the time runs out. The effects come first, then the power ups.
 */
internal fun DrawScope.drawEffectBars(world: GameWorld) {
    var row = 0
    for (effect in Effect.entries) {
        val remaining = world.effects.remaining(effect)
        if (remaining <= 0f) continue
        val kind = ToadKind.entries.first { it.effect == effect }
        drawTimerArrow(world, row, isUp = false, remaining / effect.durationSeconds, kind.body)
        row++
    }
    for (powerUp in PowerUp.entries) {
        val remaining = world.powerUps.remaining(powerUp)
        if (remaining <= 0f) continue
        val palette = birdPalette(BirdKind.entries.first { it.powerUp == powerUp })
        drawTimerArrow(world, row, isUp = true, remaining / powerUp.durationSeconds, palette.barLight)
        row++
    }
}

/** An arrow that is filled by the time left; [row] counts up from the bottom right corner (above the sprint button when that is on the right), and [fraction] is how much time is left. */
private fun DrawScope.drawTimerArrow(world: GameWorld, row: Int, isUp: Boolean, fraction: Float, color: Color) {
    val left = world.arena.width.toInt() - ArrowIcon.width - 2
    val buttonRoom = if (world.sprintSide == SprintSide.Right) SprintZone.size.toInt() else 0
    val top = world.arena.height.toInt() - 11 - buttonRoom - row * 11
    drawFilledArrow(left, top, isUp, fraction, color)
}
