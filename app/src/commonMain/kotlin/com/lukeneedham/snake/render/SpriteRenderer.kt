package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.ToadKind

/** Draws [art] with its top left corner at [left], [top], mirrored if [flip] and with each of its pixels [scale] pixels big; each run of one letter is one rectangle. */
internal fun DrawScope.drawSprite(art: SpriteArt, kind: ToadKind?, left: Int, top: Int, flip: Boolean, scale: Int = 1) {
    drawRows(art.rows, art.width, left, top, flip, scale) { letter -> spriteColor(letter, kind) }
}

/** Draws pixel art made of [rows] of letters, [width] wide, each pixel [scale] pixels big, with the colour of each letter from [colorOf] (none for a gap). */
internal inline fun DrawScope.drawRows(
    rows: List<String>,
    width: Int,
    left: Int,
    top: Int,
    flip: Boolean,
    scale: Int = 1,
    colorOf: (Char) -> Color?,
) {
    rows.forEachIndexed { row, line ->
        var column = 0
        while (column < line.length) {
            val letter = line[column]
            val color = colorOf(letter)
            if (color == null) {
                column++
                continue
            }
            var end = column
            while (end + 1 < line.length && line[end + 1] == letter) end++
            val runLeft = if (flip) left + (width - 1 - end) * scale else left + column * scale
            pixelRect(runLeft, top + row * scale, (end - column + 1) * scale, scale, color)
            column = end + 1
        }
    }
}
