package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.Critter
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt
import kotlin.math.sin

/** Animals deeper in the grass than this are hidden by it. */
private const val hiddenDepth = 4f

/** While disguised, every animal moves the same way: in jerky steps of this many pixels. */
private const val shuffleStep = 3f

/** Where an animal is drawn: its feet at [x], [y], with [bounce] pixels of lift. */
private class Spot(val x: Int, val y: Float, val bounce: Int)

/**
 * The feet of an animal, and the bounce of its movement. While the snake is disguise-blind every animal shuffles in the same
 * creepy way instead: it snaps to a coarse grid so that it jerks along in steps, and all of them dip together on the beat.
 */
private fun spotOf(critter: Critter, world: GameWorld): Spot {
    if (!isDisguised(world)) return Spot(critter.x.roundToInt(), critter.y, bounce(critter, world))
    val dip = if ((world.time * 5f).toInt() % 2 == 0) 0 else -1
    return Spot(snapToStep(critter.x), snapToStep(critter.y).toFloat(), dip)
}

private fun snapToStep(value: Float): Int = ((value / shuffleStep).roundToInt() * shuffleStep).roundToInt()

private val shadowColor = Color(0x44000000)
private val tailColor = Color(0xFFF28FA9)

/** A bird in the air is over the grass, not in it. */
private fun GameWorld.isHidden(critter: Critter): Boolean = !critter.isAirborne && arena.signedDistance(critter.x, critter.y) > hiddenDepth

internal fun DrawScope.drawCritterShadows(world: GameWorld) {
    for (critter in world.critters) {
        if (world.isHidden(critter)) continue
        val spot = spotOf(critter, world)
        val x = spot.x
        val y = spot.y.roundToInt()
        val lift = (critter.height / 3f).roundToInt().coerceAtMost(2)
        pixelRect(x - 3 + lift, y, 7 - lift * 2, 1, shadowColor)
        pixelRect(x - 2 + lift, y + 1, 5 - lift * 2, 1, shadowColor)
    }
}

/** Animals deep in the grass are not drawn: only those peeking out are, and the front blades then hide their feet. Birds are drawn by [drawBird]. */
internal fun DrawScope.drawCritters(world: GameWorld) {
    for (critter in world.critters.sortedBy { it.y }) {
        if (world.isHidden(critter)) continue
        val look = appearanceOf(critter, world)
        if (look == null) {
            // A bird in the air is drawn later, over everything
            if (critter is Critter.Bird && !critter.isAirborne) drawBird(critter, world)
            continue
        }
        val art = look.art
        val spot = spotOf(critter, world)
        val left = spot.x - art.width / 2
        val top = (spot.y - critter.height).roundToInt() - art.height + 1 - spot.bounce
        if (art == SpriteArt.Mouse) drawMouseTail(critter, world, spot.x, top + art.height - 2)
        drawSprite(art, look.kind, left, top, flip = critter.facing < 0)
    }
}

/** How many pixels the animal is lifted by its own movement: mice bob as they run, toads breathe as they sit. */
private fun bounce(critter: Critter, world: GameWorld): Int = when (critter) {
    is Critter.Mouse -> if (critter.isMoving && (world.time * 14f).toInt() % 2 == 0) 1 else 0
    is Critter.Toad -> if (!critter.isMoving && sin(world.time * 5f + critter.id) > 0.6f) 1 else 0
    is Critter.Bird -> 0
}

private fun DrawScope.drawMouseTail(critter: Critter, world: GameWorld, centerX: Int, y: Int) {
    val back = -critter.facing
    val x = centerX + back * 6
    val wag = if (sin(world.time * 9f + critter.id) > 0f) 0 else 1
    pixel(x, y, tailColor)
    pixel(x + back, y - wag, tailColor)
    pixel(x + back * 2, y - 1 - wag, tailColor)
}
