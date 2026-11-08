package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val spiralInk = Color(0xFF14261B)
private val spiralLight = Color(0xFFFFE9F4)

/** A spinning spiral above the head shows that the snake is confused, and shakes when it ignores a tap. */
internal fun DrawScope.drawConfusedSpiral(world: GameWorld) {
    val centerX = world.snake.x
    val centerY = world.snake.y - 10f
    val shake = if (world.rejectedTapTime > 0f) sin(world.time * 60f) * 1.2f else 0f
    for (pass in 0..1) {
        for (i in 0..24) {
            val angle = i * 0.55f + world.time * 8f
            val radius = i * 0.16f
            val x = (centerX + shake + cos(angle) * radius).roundToInt() + (1 - pass)
            val y = (centerY + sin(angle) * radius * 0.8f).roundToInt() + (1 - pass)
            pixel(x, y, if (pass == 0) spiralInk else spiralLight)
        }
    }
}
