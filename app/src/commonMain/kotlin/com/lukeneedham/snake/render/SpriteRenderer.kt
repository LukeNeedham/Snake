package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.ToadKind

/** Draws [art] with its top left corner at [left], [top], mirrored if [flip]; each run of one letter is one rectangle. */
internal fun DrawScope.drawSprite(art: SpriteArt, kind: ToadKind?, left: Int, top: Int, flip: Boolean) {
    drawRows(art.rows, art.width, left, top, flip) { letter -> spriteColor(letter, kind) }
}

/** Draws pixel art made of [rows] of letters, [width] wide, with the colour of each letter from [colorOf] (none for a gap). */
internal inline fun DrawScope.drawRows(
    rows: List<String>,
    width: Int,
    left: Int,
    top: Int,
    flip: Boolean,
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
            val runLeft = if (flip) left + (width - 1 - end) else left + column
            pixelRect(runLeft, top + row, end - column + 1, 1, color)
            column = end + 1
        }
    }
}
