package com.lukeneedham.snake.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** The scene is drawn at one unit per art pixel into a small bitmap, so every shape is made of whole pixels. */
internal fun DrawScope.pixelRect(x: Int, y: Int, width: Int, height: Int, color: Color) {
    if (width <= 0 || height <= 0) return
    drawRect(color = color, topLeft = Offset(x.toFloat(), y.toFloat()), size = Size(width.toFloat(), height.toFloat()))
}

internal fun DrawScope.pixel(x: Int, y: Int, color: Color) = pixelRect(x, y, 1, 1, color)

/** A round blob made of rows of pixels, centred on the nearest pixel. */
internal fun DrawScope.pixelDisc(centerX: Float, centerY: Float, radius: Float, color: Color) {
    val cx = centerX.roundToInt()
    val cy = centerY.roundToInt()
    val reach = radius + 0.4f
    val rows = reach.toInt()
    for (dy in -rows..rows) {
        val remaining = reach * reach - dy * dy
        if (remaining < 0f) continue
        val half = sqrt(remaining).toInt()
        pixelRect(cx - half, cy + dy, half * 2 + 1, 1, color)
    }
}
