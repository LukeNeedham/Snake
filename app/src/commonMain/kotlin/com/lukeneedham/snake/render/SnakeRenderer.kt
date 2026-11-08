package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import com.lukeneedham.snake.game.BodyPoint
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameTuning
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val SnakeOutline = Color(0xFF14261B)

/** The skin: a diamond pattern of eight discs that repeats along the whole body, however long it grows. */
private val SkinPattern = listOf(
    Color(0xFF1F6B45),
    Color(0xFF2F8F5B),
    Color(0xFF52C27D),
    Color(0xFFF4D35E),
    Color(0xFFF4D35E),
    Color(0xFF52C27D),
    Color(0xFF2F8F5B),
    Color(0xFF1F6B45),
)
private val HeadColor = Color(0xFF38A866)
private val DeadColor = Color(0xFF8A908C)
private val TongueColor = Color(0xFFE8465A)

private fun bodyRadius(point: BodyPoint, world: GameWorld): Float {
    var radius = GameTuning.BODY_RADIUS
    if (point.distanceFromTail < 12f) radius *= 0.3f + 0.7f * (point.distanceFromTail / 12f)
    radius += 0.25f * sin(point.cumulative * 0.45f - world.time * 5f)
    for (bulge in world.snake.bulges) {
        val distance = abs(point.cumulative - bulge)
        if (distance < 5f) radius += 1.5f * (1f - distance / 5f)
    }
    return radius
}

internal fun DrawScope.drawSnake(world: GameWorld) {
    val snake = world.snake
    val body = snake.body
    val dead = world.phase == GamePhase.GAME_OVER
    val deadAmount = if (dead) (world.gameOverTime * 2f).coerceIn(0f, 1f) else 0f

    for (i in body.indices step 2) {
        val point = body[i]
        pixelDisc(point.x, point.y, bodyRadius(point, world) + 1f, SnakeOutline)
    }
    for (i in body.indices.reversed()) {
        val point = body[i]
        val pattern = SkinPattern[((point.index % SkinPattern.size) + SkinPattern.size) % SkinPattern.size]
        val color = lerp(pattern, DeadColor, deadAmount)
        val radius = bodyRadius(point, world)
        pixelDisc(point.x, point.y, radius, color)
        if (point.index % 2 == 0 && radius > 1.8f) {
            pixel(point.x.roundToInt() - 1, point.y.roundToInt() - 1, Color.White.copy(alpha = 0.22f))
        }
    }
    drawHead(world, deadAmount)
    if (world.effectLevel(Effect.CONFUSED) > 0.05f) drawSpiral(world)
}

private fun DrawScope.drawHead(world: GameWorld, deadAmount: Float) {
    val snake = world.snake
    val hx = snake.x
    val hy = snake.y
    val dx = cos(snake.heading)
    val dy = sin(snake.heading)
    val px = -dy
    val py = dx
    val color = lerp(HeadColor, DeadColor, deadAmount)
    val radius = GameTuning.HEAD_RADIUS

    pixelDisc(hx, hy, radius + 1f, SnakeOutline)
    pixelDisc(hx + dx * 2.4f, hy + dy * 2.4f, 3.9f, SnakeOutline)
    pixelDisc(hx, hy, radius, color)
    pixelDisc(hx + dx * 2.4f, hy + dy * 2.4f, 2.7f, color)

    if (snake.mouthOpen > 0.25f) {
        val mouthX = hx + dx * 4.6f
        val mouthY = hy + dy * 4.6f
        pixelDisc(mouthX, mouthY, 2.2f * snake.mouthOpen + 0.4f, SnakeOutline)
        pixelDisc(mouthX, mouthY, 1.4f * snake.mouthOpen, Color(0xFFD43A55))
    } else if (deadAmount < 0.5f) {
        drawTongue(world, hx + dx * 5f, hy + dy * 5f, dx, dy, px, py)
    }

    val blink = (world.time + 1.3f) % 4.2f < 0.14f
    for (side in listOf(-1f, 1f)) {
        val ex = (hx + dx * 0.9f + px * side * 2.3f).roundToInt()
        val ey = (hy + dy * 0.9f + py * side * 2.3f).roundToInt()
        if (deadAmount > 0.5f) {
            val ink = Color(0xFF14261B)
            pixel(ex - 1, ey - 1, ink)
            pixel(ex + 1, ey - 1, ink)
            pixel(ex, ey, ink)
            pixel(ex - 1, ey + 1, ink)
            pixel(ex + 1, ey + 1, ink)
        } else if (blink) {
            pixelRect(ex - 1, ey, 2, 1, SnakeOutline)
        } else {
            pixelRect(ex - 1, ey - 1, 2, 2, Color.White)
            pixel(ex - 1 + (dx + 1f).roundToInt().coerceIn(0, 1), ey - 1 + (dy + 1f).roundToInt().coerceIn(0, 1), SnakeOutline)
        }
    }
}

/** The forked tongue flicks out every few seconds. */
private fun DrawScope.drawTongue(world: GameWorld, tipX: Float, tipY: Float, dx: Float, dy: Float, px: Float, py: Float) {
    val phase = (world.time % 2.6f) / 2.6f
    if (phase > 0.22f) return
    val length = (4f * sin(phase / 0.22f * PI.toFloat())).roundToInt()
    for (i in 0..length) {
        pixel((tipX + dx * i).roundToInt(), (tipY + dy * i).roundToInt(), TongueColor)
    }
    if (length >= 2) {
        val endX = tipX + dx * length
        val endY = tipY + dy * length
        pixel((endX + px + dx).roundToInt(), (endY + py + dy).roundToInt(), TongueColor)
        pixel((endX - px + dx).roundToInt(), (endY - py + dy).roundToInt(), TongueColor)
    }
}

/** A spinning spiral above the head shows that the snake is confused. */
private fun DrawScope.drawSpiral(world: GameWorld) {
    val centerX = world.snake.x
    val centerY = world.snake.y - 10f
    val shake = if (world.rejectedTapTime > 0f) sin(world.time * 60f) * 1.2f else 0f
    for (pass in 0..1) {
        for (i in 0..24) {
            val angle = i * 0.55f + world.time * 8f
            val radius = i * 0.16f
            val x = (centerX + shake + cos(angle) * radius).roundToInt() + (1 - pass)
            val y = (centerY + sin(angle) * radius * 0.8f).roundToInt() + (1 - pass)
            pixel(x, y, if (pass == 0) Color(0xFF14261B) else Color(0xFFFFE9F4))
        }
    }
}
