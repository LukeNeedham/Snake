package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope

/** The way an arrow points. */
internal enum class ArrowDirection { Up, Down, Left, Right }

/** The arrow is this long, and its head the first rows of that. */
internal const val arrowLength = 12
private const val arrowHead = 5

/** A big arrow with its tip at [tipX], [tipY]: a head and a stem, pointing the way of [direction], in gold with a dark outline. */
internal fun DrawScope.drawArrow(tipX: Int, tipY: Int, direction: ArrowDirection) {
    for (outline in booleanArrayOf(true, false)) {
        for (row in 0 until arrowLength) {
            val thickness = if (row < arrowHead) 1 + row * 2 else 3
            val color = if (outline) PixelFont.outline else HudPalette.gold
            val grow = if (outline) 1 else 0
            when (direction) {
                ArrowDirection.Up -> pixelRect(tipX - thickness / 2 - grow, tipY + row - grow, thickness + grow * 2, 1 + grow * 2, color)
                ArrowDirection.Down -> pixelRect(tipX - thickness / 2 - grow, tipY - row - grow, thickness + grow * 2, 1 + grow * 2, color)
                ArrowDirection.Left -> pixelRect(tipX + row - grow, tipY - thickness / 2 - grow, 1 + grow * 2, thickness + grow * 2, color)
                ArrowDirection.Right -> pixelRect(tipX - row - grow, tipY - thickness / 2 - grow, 1 + grow * 2, thickness + grow * 2, color)
            }
        }
    }
}
