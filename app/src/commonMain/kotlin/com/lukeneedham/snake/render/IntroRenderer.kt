package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.IntroDirector
import com.lukeneedham.snake.game.SnakeTuning
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private val bubbleFill = Color(0xFFF7F4FF)
private val bubbleEdge = Color(0xFF3A3340)
private val rumbleColor = Color(0xFFFFF3B0)
private val sleepColor = Color(0xFFDDEBFF)

/** What the story at the start draws over the world: the Zs of the sleeping snake, the zigzag lines of its rumbling stomach, the thought bubbles, and the pop. */
internal fun DrawScope.drawIntroOverlay(world: GameWorld) {
    val intro = world.intro ?: return
    if (intro.isAsleep) drawSleepingZs(world)
    if (intro.rumble > 0f) drawRumble(world, intro)
    drawBubbles(world, intro)
    if (intro.popProgress > 0f) drawPop(world, intro)
}

/** Zs come out of the mouth of the sleeping snake one after another, float up and to the side, and grow and fade. */
private fun DrawScope.drawSleepingZs(world: GameWorld) {
    val snake = world.snake
    val mouthX = snake.x + cos(snake.heading) * SnakeTuning.mouthReach
    val mouthY = snake.y + sin(snake.heading) * SnakeTuning.mouthReach
    for (i in 0 until sleepZs) {
        val progress = (world.time * sleepZRate + i.toFloat() / sleepZs) % 1f
        val alpha = min(1f, (1f - progress) * 2f)
        val x = mouthX + 6f + progress * 14f + sin(progress * 2f * PI.toFloat() * 1.5f) * 2f
        val y = mouthY - 7f - progress * 26f
        val scale = if (progress < 0.5f) 1 else 2
        drawPixelText("Z", x.roundToInt(), y.roundToInt(), sleepColor.copy(alpha = alpha), scale, PixelFont.outline.copy(alpha = alpha))
    }
}

/** Zigzag lines that shake along both sides of the middle of the body, where the stomach is. */
private fun DrawScope.drawRumble(world: GameWorld, intro: IntroDirector) {
    val points = world.snake.body.points
    if (points.size < rumbleSpan * 2 + 2) return
    val middle = points.size / 2
    val flicker = (world.time * rumbleRate).toInt()
    for (side in intArrayOf(-1, 1)) {
        var previousX = 0
        var previousY = 0
        for (i in max(0, middle - rumbleSpan)..min(points.lastIndex, middle + rumbleSpan)) {
            val point = points[i]
            // Every other point is further from the body, and which ones changes with every flicker: so the line zigzags and shakes
            val stand = SnakeTuning.bodyRadius + 1.5f + ((i + flicker) % 2) * 2.5f * intro.rumble
            val x = (point.x - point.dirY * side * stand).roundToInt()
            val y = (point.y + point.dirX * side * stand).roundToInt()
            if (i > max(0, middle - rumbleSpan)) {
                pixelLine(previousX + 1, previousY + 1, x + 1, y + 1, PixelFont.outline)
                pixelLine(previousX, previousY, x, y, rumbleColor)
            }
            previousX = x
            previousY = y
        }
    }
}

/** A line of single pixels from one point to another. */
private fun DrawScope.pixelLine(fromX: Int, fromY: Int, toX: Int, toY: Int, color: Color) {
    val steps = max(abs(toX - fromX), abs(toY - fromY))
    if (steps == 0) {
        pixel(fromX, fromY, color)
        return
    }
    for (step in 0..steps) {
        pixel(fromX + (toX - fromX) * step / steps, fromY + (toY - fromY) * step / steps, color)
    }
}

/** Three bubbles that come up one after the other: two small ones and the big one, with the mouse the snake thinks of in it. */
private fun DrawScope.drawBubbles(world: GameWorld, intro: IntroDirector) {
    val bubbles = world.den.bubbles
    for ((index, bubble) in bubbles.withIndex()) {
        val growth = intro.bubbleGrowth(index)
        if (growth <= 0f) continue
        val isBig = index == bubbles.lastIndex
        val wobble = if (isBig) sin(world.time * 5f) * 0.5f else 0f
        val radius = bubble.radius * growth + wobble
        val bob = (sin(world.time * 3f + index) * 1.2f)
        pixelDisc(bubble.x, bubble.y + bob, radius + 1f, bubbleEdge)
        pixelDisc(bubble.x, bubble.y + bob, radius, bubbleFill)
        if (isBig && growth > 0.7f) {
            val spriteLeft = bubble.x.roundToInt() - SpriteArt.Mouse.width * mouseScale / 2
            val spriteTop = (bubble.y + bob).roundToInt() - SpriteArt.Mouse.rows.size * mouseScale / 2
            drawSprite(SpriteArt.Mouse, null, spriteLeft, spriteTop, flip = false, scale = mouseScale)
        }
    }
}

/** The big bubble bursts: a ring that grows and breaks into dashes as it fades (the sparkles are particles). */
private fun DrawScope.drawPop(world: GameWorld, intro: IntroDirector) {
    val bubble = world.den.bubbles.last()
    val progress = intro.popProgress
    if (progress > popRingPart) return
    val t = progress / popRingPart
    val radius = bubble.radius * (1f + 0.5f * t)
    val dashes = 16
    for (i in 0 until dashes) {
        if (i % 2 == 1 && t > 0.5f) continue
        val angle = i * 2f * PI.toFloat() / dashes
        val x = (bubble.x + cos(angle) * radius).roundToInt()
        val y = (bubble.y + sin(angle) * radius).roundToInt()
        pixelRect(x, y, 2, 2, Color.White.copy(alpha = 1f - t))
    }
}

private const val sleepZs = 3
private const val sleepZRate = 0.4f
private const val rumbleRate = 14f
private const val rumbleSpan = 5
private const val mouseScale = 2

/** The ring of the pop only lasts the first part of it. */
private const val popRingPart = 0.45f
