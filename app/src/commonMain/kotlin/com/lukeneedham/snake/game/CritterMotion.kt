package com.lukeneedham.snake.game

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/** Where a critter is in its visit: it comes out of the grass, roams the oval, and goes back into the grass. */
enum class CritterMode { ENTERING, ROAMING, LEAVING }

/**
 * The movement of a [Critter]. A mouse runs fast for a moment, stands still and looks around, then runs again. A toad sits
 * still, then hops a short way in a random direction.
 */
internal class CritterMotion(
    var x: Float,
    var y: Float,
    private val lifetime: Float,
) {
    /** 1 looks right, -1 looks left. */
    var facing = 1
        private set
    var mode = CritterMode.ENTERING
        private set

    /** Running for a mouse, in the air or about to be for a toad. */
    var isMoving = false
        private set

    /** How high a hopping toad is above the ground. */
    var height = 0f
        private set

    /** Set when a toad lands, for the dust: the reader clears it. */
    var landed = false

    var isGone = false
        private set

    private var age = 0f
    private var timer = 0.2f
    private var lookTimer = 0f
    private var dirX = 0f
    private var dirY = 0f
    private var moveTime = 0f
    private var moveDuration = GameTuning.HOP_DURATION
    private var moveSpeed = 0f

    /** Moves like a mouse for [dt] seconds. */
    fun updateMouse(dt: Float, arena: Arena, random: Random) {
        if (tick(dt, arena)) runAndLook(dt, arena, random)
    }

    /** Moves like a toad for [dt] seconds. */
    fun updateToad(dt: Float, arena: Arena, random: Random) {
        if (tick(dt, arena)) hopAndSit(arena, random, dt)
    }

    /** Ages the critter and moves it on to its next mode; false once it has left the oval for good. */
    private fun tick(dt: Float, arena: Arena): Boolean {
        age += dt
        val distance = arena.signedDistance(x, y)
        if (mode == CritterMode.ENTERING && distance < -5f) mode = CritterMode.ROAMING
        if (mode == CritterMode.ENTERING && age > 25f) mode = CritterMode.LEAVING
        if (mode == CritterMode.ROAMING && age > lifetime) mode = CritterMode.LEAVING
        if (mode == CritterMode.LEAVING && distance > GameTuning.GRASS_DEPTH - 1f) {
            isGone = true
            return false
        }
        timer -= dt
        return true
    }

    private fun runAndLook(dt: Float, arena: Arena, random: Random) {
        if (isMoving) {
            x += dirX * GameTuning.MOUSE_SPEED * dt
            y += dirY * GameTuning.MOUSE_SPEED * dt
            if (mode == CritterMode.ROAMING && arena.signedDistance(x, y) > -4f) {
                val normal = arena.normal(x, y)
                if (dirX * normal.x + dirY * normal.y > 0f) aimInside(arena, random)
            }
            if (timer <= 0f) {
                isMoving = false
                timer = if (mode == CritterMode.ROAMING) 0.7f + random.nextFloat() * 1.6f else 0.15f
                lookTimer = 0.2f
            }
        } else {
            lookTimer -= dt
            if (lookTimer <= 0f) {
                facing = -facing
                lookTimer = 0.25f + random.nextFloat() * 0.5f
            }
            if (timer <= 0f) {
                chooseDirection(arena, random)
                isMoving = true
                timer = if (mode == CritterMode.ROAMING) 0.35f + random.nextFloat() * 0.5f else 0.9f
            }
        }
    }

    private fun hopAndSit(arena: Arena, random: Random, dt: Float) {
        if (isMoving) {
            moveTime += dt
            x += dirX * moveSpeed * dt
            y += dirY * moveSpeed * dt
            val t = (moveTime / moveDuration).coerceAtMost(1f)
            height = sin(PI.toFloat() * t) * GameTuning.HOP_HEIGHT
            if (t >= 1f) {
                isMoving = false
                height = 0f
                landed = true
                timer = if (mode == CritterMode.ROAMING) 0.8f + random.nextFloat() * 1.6f else 0.3f
            }
        } else if (timer <= 0f) {
            chooseDirection(arena, random)
            val hop = if (mode == CritterMode.ROAMING) 5f + random.nextFloat() * 7f else 9f
            moveDuration = GameTuning.HOP_DURATION
            moveSpeed = hop / moveDuration
            moveTime = 0f
            isMoving = true
        }
    }

    private fun chooseDirection(arena: Arena, random: Random) {
        when (mode) {
            CritterMode.ENTERING -> aimInside(arena, random)
            CritterMode.LEAVING -> {
                val normal = arena.normal(x, y)
                setDirection(normal.x, normal.y)
            }
            CritterMode.ROAMING -> {
                if (arena.signedDistance(x, y) > -12f && random.nextFloat() < 0.75f) {
                    aimInside(arena, random)
                } else {
                    val angle = random.nextFloat() * 2f * PI.toFloat()
                    setDirection(cos(angle), sin(angle))
                }
            }
        }
    }

    private fun aimInside(arena: Arena, random: Random) {
        val targetX = arena.centerX + (random.nextFloat() * 2f - 1f) * arena.radiusX * 0.6f
        val targetY = arena.centerY + (random.nextFloat() * 2f - 1f) * arena.radiusY * 0.6f
        setDirection(targetX - x, targetY - y)
    }

    private fun setDirection(dx: Float, dy: Float) {
        val size = sqrt(dx * dx + dy * dy)
        if (size < 1e-5f) return
        dirX = dx / size
        dirY = dy / size
        if (dirX > 0.15f) facing = 1 else if (dirX < -0.15f) facing = -1
    }
}
