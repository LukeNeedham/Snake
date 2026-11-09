package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.hypot
import kotlin.math.roundToInt

private val rind = Color(0xFFD99A1E)
private val cheese = Color(0xFFF7CF4A)
private val hole = Color(0xFFC98512)

/** Where the round end of the wedge starts, along its length. */
private const val capStart = 4f

/** The wedge is this long, and this wide (from the middle to one side) at its round end. */
private const val wedgeLength = 6.5f
private const val wedgeHalfWidth = 3.2f

/** How far in front of the nose the point of the wedge is: it is in the mouth, so only the wide end sticks out. */
private const val cheeseOffset = 0.5f

/** The holes in the cheese, as (along, across) from the point of the wedge. */
private val holes = listOf(3.2f to 0.4f, 5.0f to -1.4f, 5.4f to 1.5f)

/**
 * The piece of cheese the snake carries in its mouth: a cartoon wedge with holes in it, its point in the mouth and its
 * round end held out in front of the nose. [dx] and [dy] are the direction the snake looks.
 */
internal fun DrawScope.drawCheese(noseX: Float, noseY: Float, dx: Float, dy: Float) {
    val originX = noseX + dx * cheeseOffset
    val originY = noseY + dy * cheeseOffset
    fun fill(margin: Float, color: Color) {
        var along = -margin
        while (along <= wedgeLength + margin) {
            var across = -wedgeHalfWidth - margin
            while (across <= wedgeHalfWidth + margin) {
                if (isInsideWedge(along, across, margin)) {
                    pixel((originX + dx * along - dy * across).roundToInt(), (originY + dy * along + dx * across).roundToInt(), color)
                }
                across += 0.5f
            }
            along += 0.5f
        }
    }
    fill(1f, SnakePalette.outline)
    fill(0f, rind)
    fill(-0.9f, cheese)
    for ((along, across) in holes) {
        pixel((originX + dx * along - dy * across).roundToInt(), (originY + dy * along + dx * across).roundToInt(), hole)
    }
}

/** Whether the spot is in the wedge grown by [margin]: a triangle that widens from its point, with a round end. */
private fun isInsideWedge(along: Float, across: Float, margin: Float): Boolean {
    if (along < -margin) return false
    val capRadius = halfWidthAt(capStart) + margin
    if (along > capStart) return hypot(along - capStart, across) <= capRadius
    return kotlin.math.abs(across) <= halfWidthAt(along) + margin
}

private fun halfWidthAt(along: Float): Float = 0.5f + (wedgeHalfWidth - 0.5f) * (along.coerceAtLeast(0f) / capStart)
