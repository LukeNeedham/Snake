package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt

private val doorFrame = Color(0xFF4D4A54)
private val doorDark = Color(0xFF1B1422)
private val doorLight = Color(0xFFFFD98A)
private val doorWood = Color(0xFF9A6A3A)
private val doorWoodDark = Color(0xFF6E4A28)
private val doorSign = Color(0xFFFFE066)

/**
 * The door in the wall of the den that starts the tutorial again: a wooden door with a question mark, in a frame of stone. It swings open,
 * showing the dark, as the snake stays near it. Only a den whose snake knows everything already has it.
 */
internal fun DrawScope.drawTutorialDoor(world: GameWorld) {
    val den = world.den
    if (!den.hasDoor) return
    val x = den.doorX.roundToInt()
    val bottom = den.doorY.roundToInt() + doorSink
    val open = world.doorOpen
    pixelRect(x - doorHalfWidth - 1, bottom - doorHeight - 1, doorHalfWidth * 2 + 3, doorHeight + 2, doorFrame)
    pixelRect(x - doorHalfWidth, bottom - doorHeight, doorHalfWidth * 2 + 1, doorHeight, doorDark)
    if (open > 0.3f) {
        // The light of the tutorial in the dark
        pixelRect(x - 2, bottom - doorHeight + 3, 5, doorHeight - 5, doorLight.copy(alpha = open))
    }
    val leaf = ((1f - open) * (doorHalfWidth * 2 + 1)).roundToInt()
    if (leaf > 0) {
        pixelRect(x - doorHalfWidth, bottom - doorHeight, leaf, doorHeight, doorWood)
        for (plank in 2 until leaf step 3) pixelRect(x - doorHalfWidth + plank, bottom - doorHeight, 1, doorHeight, doorWoodDark)
        // A rounded top
        pixel(x - doorHalfWidth, bottom - doorHeight, doorFrame)
        if (leaf > 3 && open < 0.2f) drawPixelText("?", x - 1, bottom - doorHeight + 3, doorSign, 1, doorWoodDark)
    }
}

private const val doorHalfWidth = 5
private const val doorHeight = 13

/** How far the bottom of the door is outside the edge of the den: it stands in the wall. */
private const val doorSink = 3
