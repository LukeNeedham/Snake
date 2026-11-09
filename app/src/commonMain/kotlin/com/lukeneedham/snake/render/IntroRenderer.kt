package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.IntroDirector
import com.lukeneedham.snake.game.IntroStage
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val bubbleFill = Color(0xFFF7F4FF)
private val bubbleEdge = Color(0xFF3A3340)
private val rumbleColor = Color(0xFFFFF3B0)

/** What the story at the start draws over the world: the lines that shake round the stomach, the thought bubbles, and the pop. */
internal fun DrawScope.drawIntroOverlay(world: GameWorld) {
    val intro = world.intro ?: return
    if (intro.rumble > 0f) drawRumble(world, intro)
    drawBubbles(world, intro)
    if (intro.popProgress > 0f) drawPop(world, intro)
}

/** Short lines that flicker and shake round the middle of the body, where the stomach is, like the lines of a cartoon that shakes. */
private fun DrawScope.drawRumble(world: GameWorld, intro: IntroDirector) {
    val points = world.snake.body.points
    if (points.isEmpty()) return
    val stomach = points[points.size / 2]
    val phase = (world.time * rumbleRate).toInt()
    val jitter = if (phase % 2 == 0) 1 else -1
    val reach = rumbleReach + intro.rumble * 2f
    for (line in 0 until rumbleLines) {
        // Lines on and off in turn, each a little different every flicker
        if ((line + phase) % 3 == 0) continue
        val angle = line * 2f * PI.toFloat() / rumbleLines + 0.4f
        val dx = cos(angle)
        val dy = sin(angle)
        val length = 3 + (line + phase) % 2
        for (step in 0 until length) {
            val distance = reach + step
            val x = (stomach.x + dx * distance).roundToInt() + jitter
            val y = (stomach.y + dy * distance).roundToInt()
            pixelRect(x - 1, y - 1, 4, 4, PixelFont.outline)
            pixelRect(x, y, 2, 2, rumbleColor)
        }
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

private const val rumbleRate = 14f
private const val rumbleReach = 8f
private const val rumbleLines = 10
private const val mouseScale = 2

/** The ring of the pop only lasts the first part of it. */
private const val popRingPart = 0.45f
