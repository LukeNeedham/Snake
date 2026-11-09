package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.BirdKind
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.HungerTuning
import com.lukeneedham.snake.game.StomachLayout
import com.lukeneedham.snake.game.ToadKind
import kotlin.math.roundToInt

private const val stomachScale = 3
private const val rowHeight = 16

/** The sprites and what each fills: the sprite is centred in a column of this width, then comes the percentage. */
private const val spriteColumn = 24

/**
 * The second page of the pause overlay, the hunger page: the stomach, big, with how full it is, and under it a sprite for each kind
 * of animal with how much of the stomach it fills. A greedy snake gets only part of that, and the numbers show it.
 */
internal fun DrawScope.drawHungerPage(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val width = world.arena.width.toInt()
    val stomachSize = StomachLayout.size * stomachScale
    var y = topInset + PageTitle.bottom + 12
    drawStomachAt(world, (width - stomachSize) / 2, y, stomachScale)
    y += stomachSize + 6
    drawCenteredPixelText(percent(world.hunger.level), width / 2, y, HudPalette.white, scale = 2)
    y += 20
    drawCenteredPixelText(texts.hungerFills, width / 2, y, HudPalette.white.copy(alpha = 0.7f))
    y += 14
    val rowLeft = (width - spriteColumn - 4 - PixelFont.width("100%", scale = 2)) / 2
    drawAnimalRow(rowLeft, y) { centerX, bottom ->
        drawSprite(SpriteArt.Mouse, null, centerX - SpriteArt.Mouse.width / 2, bottom - SpriteArt.Mouse.height, flip = false)
    }
    drawPercent(world, HungerTuning.mouseFills, rowLeft, y)
    y += rowHeight
    drawAnimalRow(rowLeft, y) { centerX, bottom ->
        val art = BirdArt.SmallPerched
        val palette = birdPalette(BirdKind.Robin)
        drawRows(art.rows, art.width, centerX - art.width / 2, bottom - art.height, flip = false, colorOf = palette::colorOf)
    }
    drawPercent(world, HungerTuning.birdFills, rowLeft, y)
    y += rowHeight
    drawAnimalRow(rowLeft, y) { centerX, bottom ->
        drawSprite(SpriteArt.Toad, ToadKind.Purple, centerX - SpriteArt.Toad.width / 2, bottom - SpriteArt.Toad.height, flip = false)
    }
    drawPercent(world, HungerTuning.toadFills, rowLeft, y)
}

/** Calls [drawAnimal] with the middle and the bottom of the row's sprite column. */
private inline fun DrawScope.drawAnimalRow(left: Int, top: Int, drawAnimal: DrawScope.(centerX: Int, bottom: Int) -> Unit) {
    drawAnimal(left + spriteColumn / 2, top + rowHeight - 3)
}

private fun DrawScope.drawPercent(world: GameWorld, fills: Float, left: Int, top: Int) {
    drawPixelText(percent(world.foodValue(fills)), left + spriteColumn + 4, top + 1, HudPalette.white, scale = 2)
}

private fun percent(fraction: Float): String = "${(fraction * 100f).roundToInt()}%"
