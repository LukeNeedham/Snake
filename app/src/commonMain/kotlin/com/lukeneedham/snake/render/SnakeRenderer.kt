package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import com.lukeneedham.snake.game.BodyPoint
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.SnakeTuning
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

/** Draws the whole snake: the outline of the body, its skin from the tail to the head, then the head and what hovers over it. */
internal fun DrawScope.drawSnake(world: GameWorld) {
    val points = world.snake.body.points
    val deadAmount = if (world.phase == GamePhase.GameOver) (world.gameOverTime * 2f).coerceIn(0f, 1f) else 0f

    for (i in points.indices step 2) {
        val point = points[i]
        pixelDisc(point.x, point.y, bodyRadius(point, world) + 1f, SnakePalette.outline)
    }
    for (i in points.indices.reversed()) drawSkinDisc(points[i], world, deadAmount)

    drawSnakeHead(world, deadAmount)
    if (world.effects.level(Effect.Confused) > 0.05f) drawConfusedSpiral(world)
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
