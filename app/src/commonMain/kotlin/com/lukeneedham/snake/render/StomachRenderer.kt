package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt

private const val margin = 4

private val shape = listOf(
    "..###..........",
    "..###..........",
    ".####..........",
    "######.........",
    "########.......",
    "##########.....",
    "###########.##.",
    "###############",
    "##############.",
    "##############.",
    "##############.",
    ".############..",
    "..##########...",
    "...########....",
    ".....####......",
)
private val shapeWidth = shape.first().length
private val shapeHeight = shape.size

/**
 * The hunger as a stomach (a bean with the gullet at the top) in the top right corner that fills up from the bottom: it runs empty, turns red and blinks when the
 * snake is about to starve, and glows while it sprints. What was just eaten shows as a lighter part on top of the filled part,
 * which then fills in.
 */
internal fun DrawScope.drawStomach(world: GameWorld, topInset: Int) {
    val left = world.arena.width.toInt() - margin - shapeWidth
    val top = topInset + 3
    val hunger = world.hunger
    val filledRows = (shapeHeight * hunger.filled).roundToInt()
    val totalRows = (shapeHeight * hunger.level).roundToInt()
    val fillColor = hungerColor(world)
    forEachCell { column, row -> pixelRect(left + column - 1, top + row - 1, 3, 3, PixelFont.outline) }
    forEachCell { column, row ->
        val fromBottom = shapeHeight - 1 - row
        val color = when {
            fromBottom < filledRows -> fillColor
            fromBottom < totalRows -> HudPalette.hungerGain
            else -> HudPalette.barBack
        }
        pixel(left + column, top + row, color)
    }
}

private inline fun forEachCell(block: (column: Int, row: Int) -> Unit) {
    for (row in 0 until shapeHeight) {
        for (column in 0 until shapeWidth) {
            if (shape[row][column] == '#') block(column, row)
        }
    }
}

private fun hungerColor(world: GameWorld): Color = when {
    world.hunger.isLow -> if ((world.time * 4f).toInt() % 2 == 0) HudPalette.red else HudPalette.hungerLow
    world.sprint.level > 0.5f -> HudPalette.gold
    else -> HudPalette.hungerFull
}
