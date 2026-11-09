package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import com.lukeneedham.snake.game.BodyPoint
import com.lukeneedham.snake.game.PowerDown
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.SnakeTuning
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Draws the whole snake from the tail to the head, then the head and what hovers over it. The body is drawn a stretch at a time, each
 * with its outline first, so that where the snake crosses itself the stretch nearer the head lies over the other one with a dark
 * edge between them. A few discs of the stretch before are drawn again over the new outline, so that no ring shows along the body.
 */
internal fun DrawScope.drawSnake(world: GameWorld) {
    val points = world.snake.body.points
    val deadAmount = if (world.phase == GamePhase.GameOver) (world.gameOverTime * 2f).coerceIn(0f, 1f) else 0f

    val fromTail = points.asReversed()
    var start = 0
    while (start < fromTail.size) {
        val end = minOf(start + stretchSize, fromTail.size)
        for (i in start until end step 2) {
            val point = fromTail[i]
            pixelDisc(point.x, point.y, bodyRadius(point, world) + 1f, SnakePalette.outline)
        }
        for (i in maxOf(0, start - stretchOverlap) until end) drawSkinDisc(fromTail[i], world, deadAmount)
        start = end
    }

    drawSnakeHead(world, deadAmount)
    if (world.powerDowns.level(PowerDown.Confused) > 0.05f) drawConfusedSpiral(world)
}

private fun DrawScope.drawSkinDisc(point: BodyPoint, world: GameWorld, deadAmount: Float) {
    val skin = SnakePalette.skin
    val pattern = skin[((point.index % skin.size) + skin.size) % skin.size]
    val radius = bodyRadius(point, world)
    pixelDisc(point.x, point.y, radius, lerp(pattern, SnakePalette.dead, deadAmount))
    if (point.index % 2 == 0 && radius > 1.8f) {
        pixel(point.x.roundToInt() - 1, point.y.roundToInt() - 1, Color.White.copy(alpha = 0.22f))
    }
}

/** The body is thinner towards the tail, breathes in a wave, and bulges where it swallowed a mouse. */
private fun bodyRadius(point: BodyPoint, world: GameWorld): Float {
    var radius = SnakeTuning.bodyRadius
    if (point.distanceFromTail < taperLength) radius *= 0.3f + 0.7f * (point.distanceFromTail / taperLength)
    radius += 0.25f * sin(point.cumulative * 0.45f - world.time * 5f)
    for (bulge in world.snake.body.bulges) {
        val distance = abs(point.cumulative - bulge)
        if (distance < bulgeReach) radius += 1.5f * (1f - distance / bulgeReach)
    }
    return radius
}

/** The last stretch of the tail that gets thinner. */
private const val taperLength = 12f

/** How far along the body a bulge reaches. */
private const val bulgeReach = 5f

/** How many discs are drawn at a time, and how many of the stretch before are drawn again over the outline of the next. */
private const val stretchSize = 12
private const val stretchOverlap = 3
