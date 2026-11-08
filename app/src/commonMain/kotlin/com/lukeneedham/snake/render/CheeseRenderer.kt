package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.roundToInt

private val rind = Color(0xFFD99A1E)
private val cheese = Color(0xFFF7CF4A)
private val hole = Color(0xFFC98512)

/** The piece of cheese the snake carries in its mouth: a round yellow lump with holes, held out in front of the nose. */
internal fun DrawScope.drawCheese(noseX: Float, noseY: Float, dx: Float, dy: Float) {
    val x = noseX + dx * cheeseOffset
    val y = noseY + dy * cheeseOffset
    pixelDisc(x, y, 3.2f, SnakePalette.outline)
    pixelDisc(x, y, 2.4f, rind)
    pixelDisc(x - dx * 0.4f, y - dy * 0.4f, 1.7f, cheese)
    pixel((x - dy).roundToInt(), (y + dx).roundToInt(), hole)
    pixel((x + dx).roundToInt(), (y + dy).roundToInt(), hole)
}

/** How far in front of the nose the middle of the cheese is: it sits in the mouth, so only the front sticks out. */
private const val cheeseOffset = 1.8f
