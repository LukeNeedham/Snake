package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.SnakeTuning
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/** The head is widest at the back and narrows to the nose: discs of shrinking radius, each as far in front of the centre. */
private val headShape = listOf(-0.4f to SnakeTuning.headRadius, 1.4f to 3.4f, 3.0f to 2.7f, 4.4f to 1.9f, 5.4f to 1.1f)

/** Draws the head: its shape, the mouth or the flicking tongue, and the eyes, which look where the snake turns, blink and turn to crosses when dead. */
internal fun DrawScope.drawSnakeHead(world: GameWorld, deadAmount: Float) {
    val snake = world.snake
    val dx = cos(snake.heading)
    val dy = sin(snake.heading)
    val color = lerp(SnakePalette.head, SnakePalette.dead, deadAmount)

    for ((offset, radius) in headShape) {
        pixelDisc(snake.x + dx * offset, snake.y + dy * offset, radius + 1f, SnakePalette.outline)
    }
    for ((offset, radius) in headShape) {
        pixelDisc(snake.x + dx * offset, snake.y + dy * offset, radius, color)
    }

    if (snake.mouthOpen > 0.25f) {
        drawOpenMouth(snake.x + dx * SnakeTuning.mouthReach, snake.y + dy * SnakeTuning.mouthReach, snake.mouthOpen)
    } else if (deadAmount < 0.5f) {
        drawTongue(world, snake.x + dx * tongueRoot, snake.y + dy * tongueRoot, dx, dy)
    }
    drawEyes(world, deadAmount, dx, dy)
}

private fun DrawScope.drawOpenMouth(x: Float, y: Float, openness: Float) {
    pixelDisc(x, y, 2.2f * openness + 0.4f, SnakePalette.outline)
    pixelDisc(x, y, 1.4f * openness, SnakePalette.mouthInside)
}

/** The forked tongue flicks out every few seconds, from the very tip of the nose: it never shows over the head. */
private fun DrawScope.drawTongue(world: GameWorld, tipX: Float, tipY: Float, dx: Float, dy: Float) {
    val phase = (world.time % tongueCycle) / tongueCycle
    if (phase > tongueOutPart) return
    val length = (4f * sin(phase / tongueOutPart * PI.toFloat())).roundToInt()
    for (i in 0..length) {
        pixel((tipX + dx * i).roundToInt(), (tipY + dy * i).roundToInt(), SnakePalette.tongue)
    }
    if (length >= 2) {
        val endX = tipX + dx * length
        val endY = tipY + dy * length
        pixel((endX - dy + dx).roundToInt(), (endY + dx + dy).roundToInt(), SnakePalette.tongue)
        pixel((endX + dy + dx).roundToInt(), (endY - dx + dy).roundToInt(), SnakePalette.tongue)
    }
}

private fun DrawScope.drawEyes(world: GameWorld, deadAmount: Float, dx: Float, dy: Float) {
    val snake = world.snake
    val isBlinking = (world.time + 1.3f) % blinkCycle < blinkSeconds
    for (side in listOf(-1f, 1f)) {
        val ex = (snake.x + dx * 1.4f - dy * side * 2.3f).roundToInt()
        val ey = (snake.y + dy * 1.4f + dx * side * 2.3f).roundToInt()
        when {
            deadAmount > 0.5f -> drawCrossEye(ex, ey)
            isBlinking -> pixelRect(ex - 1, ey, 2, 1, SnakePalette.outline)
            else -> {
                pixelRect(ex - 1, ey - 1, 2, 2, Color.White)
                // The pupils look the way the snake is turning: to the side of its heading, and a little ahead
                val lookX = dx * 0.4f - dy * snake.turnDirection
                val lookY = dy * 0.4f + dx * snake.turnDirection
                pixel(ex - 1 + if (lookX > 0f) 1 else 0, ey - 1 + if (lookY > 0f) 1 else 0, SnakePalette.outline)
            }
        }
    }
}

private fun DrawScope.drawCrossEye(x: Int, y: Int) {
    val ink = SnakePalette.outline
    pixel(x - 1, y - 1, ink)
    pixel(x + 1, y - 1, ink)
    pixel(x, y, ink)
    pixel(x - 1, y + 1, ink)
    pixel(x + 1, y + 1, ink)
}

/** How far from the centre of the head the front of its outline is: the tongue starts just past it. */
private const val tongueRoot = 7.6f
private const val tongueCycle = 2.6f
private const val tongueOutPart = 0.22f
private const val blinkCycle = 4.2f
private const val blinkSeconds = 0.14f
