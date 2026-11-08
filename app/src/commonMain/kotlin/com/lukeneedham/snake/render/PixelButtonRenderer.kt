package com.lukeneedham.snake.render

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.roundToInt

private val buttonFill = Color(0xFF3A3340)
private val buttonLight = Color(0xFF5B5166)
private val buttonShade = Color(0xFF282230)

/** A button in pixel art: a dark outline, a flat face with a light top edge and a shaded bottom edge, and corners cut off. */
internal fun DrawScope.drawPixelButton(rect: Rect) {
    val left = rect.left.roundToInt()
    val top = rect.top.roundToInt()
    val width = rect.width.roundToInt()
    val height = rect.height.roundToInt()
    pixelRect(left + 1, top, width - 2, height, PixelFont.outline)
    pixelRect(left, top + 1, width, height - 2, PixelFont.outline)
    pixelRect(left + 1, top + 1, width - 2, height - 2, buttonFill)
    pixelRect(left + 2, top + 1, width - 4, 1, buttonLight)
    pixelRect(left + 2, top + height - 2, width - 4, 1, buttonShade)
}
