package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.PowerUp
import com.lukeneedham.snake.game.SnakeTuning
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/** The head is widest at the back and narrows to the nose: discs of shrinking radius, each as far in front of the centre. */
private val headShape = listOf(-0.4f to SnakeTuning.headRadius, 1.4f to 3.4f, 3.0f to 2.7f, 4.4f to 1.9f, 5.4f to 1.1f)

/** With the big mouth power up the snout is swollen: wider all the way to the nose. */
private val bigMouthHeadShape = listOf(-0.4f to SnakeTuning.headRadius, 1.4f to 4.3f, 3.0f to 4.1f, 4.4f to 3.3f, 5.4f to 2.3f)

/** Draws the head: its shape, the mouth or the flicking tongue (or the cheese the snake carries), and the eyes, which look where the snake turns, blink and turn to crosses when dead. */
internal fun DrawScope.drawSnakeHead(world: GameWorld, deadAmount: Float) {
    val snake = world.snake
    val dx = cos(snake.heading)
    val dy = sin(snake.heading)
    val color = lerp(SnakePalette.head, SnakePalette.dead, deadAmount)

    val hasBigMouth = world.powerUps.isOn(PowerUp.BiggerMouth)
    val shape = if (hasBigMouth) bigMouthHeadShape else headShape
    for ((offset, radius) in shape) {
        pixelDisc(snake.x + dx * offset, snake.y + dy * offset, radius + 1f, SnakePalette.outline)
    }
    for ((offset, radius) in shape) {
        pixelDisc(snake.x + dx * offset, snake.y + dy * offset, radius, color)
    }

    if (world.powerUps.isOn(PowerUp.Cheese)) {
        drawCheese(snake.x + dx * SnakeTuning.mouthReach, snake.y + dy * SnakeTuning.mouthReach, dx, dy)
    } else if (hasBigMouth && deadAmount < 0.5f) {
        // The wide mouth always gapes a little, and opens wider than normal when food is near
        drawOpenMouth(snake.x + dx * bigMouthReach, snake.y + dy * bigMouthReach, maxOf(snake.mouthOpen, bigMouthRest), bigMouthScale)
    } else if (snake.mouthOpen > 0.25f) {
        drawOpenMouth(snake.x + dx * SnakeTuning.mouthReach, snake.y + dy * SnakeTuning.mouthReach, snake.mouthOpen, 1f)
    } else if (deadAmount < 0.5f && world.intro?.isAsleep != true) {
        drawTongue(world, snake.x + dx * tongueRoot, snake.y + dy * tongueRoot, dx, dy)
    }
    drawEyes(world, deadAmount, dx, dy)
}

private fun DrawScope.drawOpenMouth(x: Float, y: Float, openness: Float, scale: Float) {
    pixelDisc(x, y, (2.2f * openness + 0.4f) * scale, SnakePalette.outline)
    pixelDisc(x, y, 1.4f * openness * scale, SnakePalette.mouthInside)
}

/** The forked tongue flicks out every few seconds, from the very tip of the nose: it never shows over the head. */
private fun DrawScope.drawTongue(world: GameWorld, tipX: Float, tipY: Float, dx: Float, dy: Float) {
    val sticksOut = world.intro?.tongueProgress ?: 0f
    val length = if (sticksOut > 0f) {
        // Sticking its tongue out on purpose: it flicks out twice, further than usual
        (5f * abs(sin(sticksOut * 2f * PI.toFloat()))).roundToInt()
    } else {
        val phase = (world.time % tongueCycle) / tongueCycle
        if (phase > tongueOutPart) return
        (4f * sin(phase / tongueOutPart * PI.toFloat())).roundToInt()
    }
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
    val isBlinking = (world.time + 1.3f) % blinkCycle < blinkSeconds || world.intro?.eyesClosed == true
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

/** The big mouth is this many times as wide, sits a little further forward, and is at least this open even when nothing is near. */
private const val bigMouthScale = 1.6f
private const val bigMouthReach = 5.2f
private const val bigMouthRest = 0.55f
