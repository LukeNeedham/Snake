package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val wingColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFD1E8), Color(0xFFBFE3FF))
private val butterflyBody = Color(0xFF2B2230)

/** A few butterflies that drift over the sand. */
internal fun DrawScope.drawButterflies(world: GameWorld) {
    val arena = world.arena
    for (i in wingColors.indices) {
        val t = world.time * (0.22f + 0.05f * i) + i * 2.1f
        val x = (arena.centerX + sin(t) * arena.radiusX * 0.7f).roundToInt()
        val y = (arena.centerY + cos(t * 0.8f + i) * arena.radiusY * 0.7f).roundToInt()
        val isWingUp = ((world.time * 12f + i).toInt() % 2) == 0
        val wings = wingColors[i]
        pixel(x, y, butterflyBody)
        if (isWingUp) {
            pixelRect(x - 2, y - 1, 2, 2, wings)
            pixelRect(x + 1, y - 1, 2, 2, wings)
        } else {
            pixel(x - 1, y, wings)
            pixel(x + 1, y, wings)
        }
    }
}
