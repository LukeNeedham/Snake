package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.Critter
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt
import kotlin.math.sin

/** Animals deeper in the grass than this are hidden by it. */
private const val hiddenDepth = 4f

private val shadowColor = Color(0x44000000)
private val tailColor = Color(0xFFF28FA9)

private fun GameWorld.isHidden(critter: Critter): Boolean = arena.signedDistance(critter.x, critter.y) > hiddenDepth

internal fun DrawScope.drawCritterShadows(world: GameWorld) {
    for (critter in world.critters) {
        if (world.isHidden(critter)) continue
        val x = critter.x.roundToInt()
        val y = critter.y.roundToInt()
        val lift = (critter.height / 3f).roundToInt()
        pixelRect(x - 3 + lift, y, 7 - lift * 2, 1, shadowColor)
        pixelRect(x - 2 + lift, y + 1, 5 - lift * 2, 1, shadowColor)
    }
}

/** Animals deep in the grass are not drawn: only those peeking out are, and the front blades then hide their feet. */
internal fun DrawScope.drawCritters(world: GameWorld) {
    for (critter in world.critters.sortedBy { it.y }) {
        if (world.isHidden(critter)) continue
        val look = appearanceOf(critter, world)
        val art = look.art
        val left = critter.x.roundToInt() - art.width / 2
        val top = (critter.y - critter.height).roundToInt() - art.height + 1 - bounce(critter, world)
        if (art == SpriteArt.Mouse) drawMouseTail(critter, world, top + art.height - 2)
        drawSprite(art, look.kind, left, top, flip = critter.facing < 0)
    }
}

/** How many pixels the animal is lifted by its own movement: mice bob as they run, toads breathe as they sit. */
private fun bounce(critter: Critter, world: GameWorld): Int = when (critter) {
    is Critter.Mouse -> if (critter.isMoving && (world.time * 14f).toInt() % 2 == 0) 1 else 0
    is Critter.Toad -> if (!critter.isMoving && sin(world.time * 5f + critter.id) > 0.6f) 1 else 0
}

private fun DrawScope.drawMouseTail(critter: Critter, world: GameWorld, y: Int) {
    val back = -critter.facing
    val x = critter.x.roundToInt() + back * 6
    val wag = if (sin(world.time * 9f + critter.id) > 0f) 0 else 1
    pixel(x, y, tailColor)
    pixel(x + back, y - wag, tailColor)
    pixel(x + back * 2, y - 1 - wag, tailColor)
}
