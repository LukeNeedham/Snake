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
private val boulderBody = Color(0xFF6F6B73)
private val boulderLight = Color(0xFF9A969D)
private val boulderShade = Color(0xFF55515A)
private val pebbleBody = Color(0xFF9B979F)
private val pebbleLight = Color(0xFFC2BEC7)
private val pebbleShade = Color(0xFF4F4B56)

/** About how far apart the middles of two rocks of the ring are. */
private const val rockSpacing = 5.2f

/** How many pebbles lie on the floor of the house. */
private const val pebbleCount = 16

/**
 * The ring of rocks round the edge of the house, with an opening where each path leaves it: the one up to the stage, and the one to the
 * left, to the room of the tutorial. They are drawn from the back to the front, so that the front ones overlap the others.
 */
internal fun DrawScope.drawHouseRocks(layout: WorldLayout) {
    val random = Random(31)
    val rocks = ArrayList<Triple<Float, Float, Float>>()
    val rockCount = (2f * PI.toFloat() * DenTuning.houseRadius * 1.1f / rockSpacing).toInt()
    for (i in 0 until rockCount) {
        val angle = i * 2f * PI.toFloat() / rockCount + (random.nextFloat() - 0.5f) * 0.12f
        val reach = layout.house.radiusAt(angle) + 1.5f + random.nextFloat()
        val x = layout.houseX + cos(angle) * reach
        val y = layout.houseY + sin(angle) * reach
        val size = 2.3f + random.nextFloat() * 1.2f
        val isInUpOpening = abs(x - layout.houseX) < DenTuning.pathHalfWidth + 1f && y < layout.houseY
        val isInLeftOpening = abs(y - layout.houseY) < DenTuning.leftPathHalfWidth + 1f && x < layout.houseX
        if (isInUpOpening || isInLeftOpening) continue
        rocks.add(Triple(x, y, size))
    }
    for ((x, y, size) in rocks.sortedBy { it.second }) drawRock(x, y, size)
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

/**
 * The pile of stones that blocks the path up from the house while the tutorial is not done: big boulders in two rows across the path, and
 * small stones in the cracks. It is drawn over the ground each frame, since it goes away.
 */
internal fun DrawScope.drawGateStones(world: GameWorld) {
    val layout = world.layout
    if (!layout.isGateClosed) return
    val gate = layout.gate
    val left = gate.centerX - gate.halfWidth
    val count = 8
    val step = gate.halfWidth * 2f / count
    val stones = ArrayList<Triple<Float, Float, Float>>()
    for (row in 0..1) {
        for (i in 0 until count) {
            val wobble = sin((i * 12.9898f + row * 78.233f)) * 1.4f
            val x = left + (i + 0.5f + row * 0.5f) * step + wobble
            val y = gate.centerY + (row - 0.5f) * 5f + cos(i * 4.1f + row) * 1.2f
            val size = 4.2f + (sin(i * 7.3f + row * 2.1f) + 1f) * 0.6f
            stones.add(Triple(x, y, size))
        }
    }
    for ((x, y, size) in stones.sortedBy { it.second }) {
        pixelDisc(x, y, size + 1f, rockOutline)
        pixelDisc(x, y, size, boulderBody)
        pixelDisc(x - 1f, y - 1.2f, size * 0.55f, boulderLight)
        pixelRect(x.roundToInt() - size.toInt() + 1, y.roundToInt() + size.toInt(), size.toInt() * 2 - 1, 1, boulderShade)
    }
}
