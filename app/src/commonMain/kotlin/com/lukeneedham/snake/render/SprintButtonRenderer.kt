package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.SprintZone
import kotlin.math.abs
import kotlin.math.roundToInt

/** The round button in a bottom corner that shows where to hold to sprint: it lights up while the snake sprints. */
internal fun DrawScope.drawSprintButton(world: GameWorld) {
    val centerX = SprintZone.buttonX(world.arena, world.sprintSide)
    val centerY = SprintZone.buttonY(world.arena)
    val lit = world.sprint.level
    pixelDisc(centerX, centerY, SprintZone.buttonRadius + 1f, PixelFont.outline)
    pixelDisc(centerX, centerY, SprintZone.buttonRadius, if (lit > 0.5f) HudPalette.gold.copy(alpha = 0.55f) else Color(0x66000000))
    val color = if (lit > 0.5f) HudPalette.gold else HudPalette.white.copy(alpha = 0.85f)
    val x = centerX.roundToInt()
    val y = centerY.roundToInt()
    // Two chevrons that point up
    for (base in intArrayOf(y - 5, y + 1)) {
        for (dx in -4..4) pixel(x + dx, base + abs(dx), color)
    }
}
