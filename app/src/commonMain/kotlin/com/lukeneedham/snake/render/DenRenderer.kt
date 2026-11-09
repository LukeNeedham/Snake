package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.DenLayout
import com.lukeneedham.snake.game.DenTuning
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private val rockOutline = Color(0xFF3E3A3F)
private val rockBody = Color(0xFF8E8A90)
private val rockLight = Color(0xFFB9B5BB)
private val rockShade = Color(0xFF6F6B73)

/** About how far apart the middles of two rocks of the ring are. */
private const val rockSpacing = 5.2f

/** The ring of rocks round the edge of the den, with an opening where the path leaves it. They are drawn from the back to the front, so that the front ones overlap the others. */
internal fun DrawScope.drawDenRocks(den: DenLayout) {
    val random = Random(31)
    val rocks = ArrayList<Triple<Float, Float, Float>>()
    val rockCount = (2f * PI.toFloat() * DenTuning.denRadius * 1.1f / rockSpacing).toInt()
    for (i in 0 until rockCount) {
        val angle = i * 2f * PI.toFloat() / rockCount + (random.nextFloat() - 0.5f) * 0.12f
        val reach = den.den.radiusAt(angle) + 1.5f + random.nextFloat()
        val x = den.denX + cos(angle) * reach
        val y = den.denY + sin(angle) * reach
        val size = 2.3f + random.nextFloat() * 1.2f
        if (abs(x - den.denX) < DenTuning.pathHalfWidth + 1f && y < den.denY) continue
        // The door of the tutorial takes the place of some rocks
        if (den.hasDoor && hypot(x - den.doorX, y - den.doorY) < doorGap) continue
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

/** How far from the middle of the door of the tutorial there are no rocks. */
private const val doorGap = 9f
