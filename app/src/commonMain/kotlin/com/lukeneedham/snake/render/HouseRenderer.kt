package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.DenTuning
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.WorldLayout
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private val rockOutline = Color(0xFF3E3A3F)
private val rockBody = Color(0xFF8E8A90)
private val rockLight = Color(0xFFB9B5BB)
private val rockShade = Color(0xFF6F6B73)
private val pebbleBody = Color(0xFF9B979F)
private val pebbleLight = Color(0xFFC2BEC7)
private val pebbleShade = Color(0xFF4F4B56)

/** About how far apart the middles of two rocks of the ring are. */
private const val rockSpacing = 5.2f

/** How many pebbles lie on the floor of the house. */
private const val pebbleCount = 16

/** Where in the ring of rocks a rock is: not at an opening, or in the opening of the path up (where the gate closes the wall) or of the path to the left. */
private enum class RockPlace { Wall, UpOpening, LeftOpening }

private class HouseRock(val x: Float, val y: Float, val size: Float, val place: RockPlace)

/** All the rocks of the ring round the edge of the house, including the ones that would be in the openings of the paths. */
private fun houseRocks(layout: WorldLayout): List<HouseRock> {
    val random = Random(31)
    val rocks = ArrayList<HouseRock>()
    val rockCount = (2f * PI.toFloat() * DenTuning.houseRadius * 1.1f / rockSpacing).toInt()
    for (i in 0 until rockCount) {
        val angle = i * 2f * PI.toFloat() / rockCount + (random.nextFloat() - 0.5f) * 0.12f
        val reach = layout.house.radiusAt(angle) + 1.5f + random.nextFloat()
        val x = layout.houseX + cos(angle) * reach
        val y = layout.houseY + sin(angle) * reach
        val size = 2.3f + random.nextFloat() * 1.2f
        val place = when {
            abs(x - layout.houseX) < DenTuning.pathHalfWidth + 1f && y < layout.houseY -> RockPlace.UpOpening
            abs(y - layout.houseY) < DenTuning.leftPathHalfWidth + 1f && x < layout.houseX -> RockPlace.LeftOpening
            else -> RockPlace.Wall
        }
        rocks.add(HouseRock(x, y, size, place))
    }
    return rocks
}

/**
 * The ring of rocks round the edge of the house, with an opening where each path leaves it: the one up to the stage, and the one to the
 * left, to the room of the tutorial. They are drawn from the back to the front, so that the front ones overlap the others.
 */
internal fun DrawScope.drawHouseRocks(layout: WorldLayout) {
    for (rock in houseRocks(layout).filter { it.place == RockPlace.Wall }.sortedBy { it.y }) drawRock(rock.x, rock.y, rock.size)
}

/**
 * The stones that close the path up while the tutorial is not done: the rocks of the ring that would be in the opening, so that the wall of
 * the house goes on unbroken. It is drawn over the ground each frame, since it goes away.
 */
internal fun DrawScope.drawGateStones(world: GameWorld) {
    val layout = world.layout
    if (!layout.isGateClosed) return
    for (rock in houseRocks(layout).filter { it.place == RockPlace.UpOpening }) drawRock(rock.x, rock.y, rock.size)
}

private fun DrawScope.drawRock(x: Float, y: Float, size: Float) {
    pixelDisc(x, y, size + 1f, rockOutline)
    pixelDisc(x, y, size, rockBody)
    pixelDisc(x - 0.6f, y - 0.8f, size * 0.55f, rockLight)
    val shadeY = y.roundToInt() + size.toInt()
    pixelRect(x.roundToInt() - size.toInt() + 1, shadeY, size.toInt() * 2 - 1, 1, rockShade)
}

/** A few little pebbles lying here and there on the stone floor of the house, each a small round stone with a light top and a shadow under it. */
internal fun DrawScope.drawHousePebbles(layout: WorldLayout) {
    val random = Random(555)
    var placed = 0
    var tries = 0
    while (placed < pebbleCount && tries < pebbleCount * 20) {
        tries++
        val angle = random.nextFloat() * 2f * PI.toFloat()
        val reach = random.nextFloat() * DenTuning.houseRadius
        val x = layout.houseX + cos(angle) * reach
        val y = layout.houseY + sin(angle) * reach
        if (layout.house.signedDistance(x, y) > -pebbleEdgeMargin) continue
        drawPebble(x.roundToInt(), y.roundToInt(), if (random.nextFloat() < 0.3f) 2 else 1)
        placed++
    }
}

/** A pebble: [size] 1 is 2 by 1 pixels with a shadow, 2 is a little bigger. */
private fun DrawScope.drawPebble(x: Int, y: Int, size: Int) {
    pixelRect(x, y + 1, size + 2, 1, pebbleShade)
    pixelRect(x, y, size + 2, size, pebbleBody)
    pixel(x, y, pebbleLight)
}

/** Pebbles must lie this far inside the edge of the floor. */
private const val pebbleEdgeMargin = 6f
