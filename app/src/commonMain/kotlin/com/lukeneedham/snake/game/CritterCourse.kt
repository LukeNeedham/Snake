package com.lukeneedham.snake.game

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/** Where a critter is, which way it looks, and the direction it goes in. */
internal class CritterCourse(var x: Float, var y: Float) {

    /** 1 looks right, -1 looks left. */
    var facing = 1
        private set

    var directionX = 0f
        private set
    var directionY = 0f
        private set

    fun moveAlong(speed: Float, dt: Float) {
        x += directionX * speed * dt
        y += directionY * speed * dt
    }

    fun lookTheOtherWay() {
        facing = -facing
    }

    /** Picks the next direction that suits [mode]: in from the grass, anywhere, or out into the grass. */
    fun chooseDirection(mode: CritterMode, arena: Arena, random: Random) {
        when (mode) {
            CritterMode.Entering -> aimInside(arena, random)
            CritterMode.Leaving -> {
                val normal = arena.normal(x, y)
                setDirection(normal.x, normal.y)
            }
            CritterMode.Roaming -> {
                if (arena.signedDistance(x, y) > -nearWallDepth && random.nextFloat() < inwardChance) {
                    aimInside(arena, random)
                } else {
                    val angle = random.nextFloat() * 2f * PI.toFloat()
                    setDirection(cos(angle), sin(angle))
                }
            }
        }
    }

    /** Whether the point is in front of the critter, the side it looks to, and closer than [range]. Behind its back is not seen. */
    fun sees(pointX: Float, pointY: Float, range: Float): Boolean {
        val dx = pointX - x
        val dy = pointY - y
        return dx * facing > 0f && dx * dx + dy * dy < range * range
    }

    /** Turns to go straight away from the point. */
    fun runAwayFrom(pointX: Float, pointY: Float) = setDirection(x - pointX, y - pointY)

    /** Whether the critter is going out of the oval. */
    fun isHeadingOut(arena: Arena): Boolean {
        val normal = arena.normal(x, y)
        return directionX * normal.x + directionY * normal.y > 0f
    }

    fun aimInside(arena: Arena, random: Random) {
        val targetX = arena.centerX + (random.nextFloat() * 2f - 1f) * arena.radiusX * 0.6f
        val targetY = arena.centerY + (random.nextFloat() * 2f - 1f) * arena.radiusY * 0.6f
        setDirection(targetX - x, targetY - y)
    }

    private fun setDirection(dx: Float, dy: Float) {
        val size = sqrt(dx * dx + dy * dy)
        if (size < 1e-5f) return
        directionX = dx / size
        directionY = dy / size
        if (directionX > 0.15f) facing = 1 else if (directionX < -0.15f) facing = -1
    }

    private companion object {
        /** Closer to the wall than this, a roaming critter mostly turns back in. */
        const val nearWallDepth = 12f
        const val inwardChance = 0.75f
    }
}
