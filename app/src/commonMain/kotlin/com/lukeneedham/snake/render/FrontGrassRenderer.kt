package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.WorldTuning
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/** The blades along the inner edge of the grass: drawn in front of the animals, so that an animal in the grass is hidden. */
internal fun createFrontGrass(arena: Arena): List<GrassBlade> {
    val random = Random(99)
    val width = arena.width.toInt()
    val height = arena.height.toInt()
    val colors = GrassPalette.bladesFront
    val blades = ArrayList<GrassBlade>()
    repeat(2400) {
        val x = random.nextInt(width)
        val y = random.nextInt(height)
        val distance = arena.signedDistance(x + 0.5f, y + 0.5f)
        if (distance in -0.5f..WorldTuning.grassDepth) {
            blades.add(GrassBlade(x, y, 6 + random.nextInt(6), random.nextFloat() * 2f * PI.toFloat(), random.nextInt(colors.size)))
        }
    }
    blades.sortBy { it.y }
    return blades
}

/** The blades sway all the time, and shake where an animal hides in the grass. */
internal fun DrawScope.drawFrontGrass(blades: List<GrassBlade>, world: GameWorld) {
    val arena = world.arena
    val hidden = world.critters.filter { arena.signedDistance(it.x, it.y) > -1f }
    val colors = GrassPalette.bladesFront
    for (blade in blades) {
        var sway = sin(world.time * 2.2f + blade.phase + blade.x * 0.15f) * 1.4f
        for (critter in hidden) {
            if (abs(blade.x - critter.x) < 7f && abs(blade.y - critter.y) < 7f) {
                sway += sin(world.time * 30f + blade.phase) * 1.7f
            }
        }
        val offset = sway.roundToInt()
        val color = colors[blade.shade]
        val lower = blade.height / 2
        pixelRect(blade.x, blade.y - lower, 1, lower + 1, color)
        pixelRect(blade.x + offset, blade.y - blade.height, 1, blade.height - lower, color)
        pixel(blade.x + offset, blade.y - blade.height, GrassPalette.bladeTip)
        if (blade.shade == 2) pixelRect(blade.x + 1, blade.y - lower, 1, lower, color)
    }
}
