package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.ToadKind

/** Draws [art] with its top left corner at [left], [top], mirrored if [flip]; each run of one letter is one rectangle. */
internal fun DrawScope.drawSprite(art: SpriteArt, kind: ToadKind?, left: Int, top: Int, flip: Boolean) {
    val width = art.width
    art.rows.forEachIndexed { row, line ->
        var column = 0
        while (column < line.length) {
            val letter = line[column]
            val color = spriteColor(letter, kind)
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
