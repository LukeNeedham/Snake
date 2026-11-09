package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.roundToInt

/** The arrow that marks a power up (pointing up) or a power down (pointing down). */
internal object ArrowIcon {
    const val width = 7
    const val height = 8

    /** The shape of an arrow pointing up, row by row from the top. */
    private val shape = listOf(
        "...#...",
        "..###..",
        ".#####.",
        "#######",
        "..###..",
        "..###..",
        "..###..",
        "..###..",
    )

    /** Whether the arrow covers the pixel at [column] and [row] (counted from the top). */
    fun covers(column: Int, row: Int, isUp: Boolean): Boolean {
        if (column !in 0 until width || row !in 0 until height) return false
        val shapeRow = if (isUp) row else height - 1 - row
        return shape[shapeRow][column] == '#'
    }
}

/** A plain arrow with a dark edge, at [left] and [top]. */
internal fun DrawScope.drawArrow(left: Int, top: Int, isUp: Boolean, color: Color, outline: Color = PixelFont.outline) {
    drawFilledArrow(left, top, isUp, fraction = 1f, color = color, emptyColor = color, outline = outline)
}

/**
 * An arrow with a dark edge that is filled with [color] from the bottom up, like a stomach filling: [fraction] is how much of it is
 * filled (1 is all of it), and the rest shows [emptyColor].
 */
internal fun DrawScope.drawFilledArrow(
    left: Int,
    top: Int,
    isUp: Boolean,
    fraction: Float,
    color: Color,
    emptyColor: Color = HudPalette.barBack,
    outline: Color = PixelFont.outline,
) {
    for (row in -1..ArrowIcon.height) {
        for (column in -1..ArrowIcon.width) {
            if (ArrowIcon.covers(column, row, isUp)) continue
            val isEdge = (-1..1).any { dr -> (-1..1).any { dc -> ArrowIcon.covers(column + dc, row + dr, isUp) } }
            if (isEdge) pixel(left + column, top + row, outline)
        }
    }
    val filledRows = (ArrowIcon.height * fraction.coerceIn(0f, 1f)).roundToInt()
    for (row in 0 until ArrowIcon.height) {
        val isFilled = row >= ArrowIcon.height - filledRows
        for (column in 0 until ArrowIcon.width) {
            if (ArrowIcon.covers(column, row, isUp)) pixel(left + column, top + row, if (isFilled) color else emptyColor)
        }
    }
}
