package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.BirdKind
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.StatusEntry
import com.lukeneedham.snake.game.StatusLayout
import com.lukeneedham.snake.game.ToadKind

/** The colours of a power down or power up: the one of its arrow and the lighter one of its name. */
internal class StatusColors(val arrow: Color, val text: Color)

internal fun statusColors(entry: StatusEntry): StatusColors {
    val powerUp = entry.powerUp
    if (powerUp != null) {
        val palette = birdPalette(BirdKind.entries.first { it.powerUp == powerUp })
        return StatusColors(arrow = palette.barLight, text = palette.barLight)
    }
    val kind = ToadKind.entries.first { it.powerDown == entry.powerDown }
    return StatusColors(arrow = kind.body, text = kind.light)
}

/**
 * One arrow per power down of a toad (pointing down) and per power up of a bird (pointing up) that is on, in a column up the right edge of
 * the screen from the bottom corner, where there is always grass behind them, in the colour of its animal; each one empties from the
 * top down as the time runs out. The power downs come first, then the power ups. A tap on the column opens the list of what is on.
 */
internal fun DrawScope.drawStatusBars(world: GameWorld) {
    world.statuses.forEachIndexed { row, entry ->
        drawFilledArrow(
            left = StatusLayout.arrowLeft(world.arena),
            top = StatusLayout.arrowTop(world.arena, world.sprintSide, row),
            isUp = entry.isPowerUp,
            fraction = entry.fraction,
            color = statusColors(entry).arrow,
        )
    }
}
