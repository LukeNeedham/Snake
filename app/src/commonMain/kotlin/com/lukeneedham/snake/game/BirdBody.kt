package com.lukeneedham.snake.game

import kotlin.random.Random

/** Where a bird is in its visit: coming in, on the ground, crouched to take off, or leaving. */
internal enum class BirdPhase { Arriving, Perched, Startled, Leaving }

/**
 * The body of a bird. It flies in from off the screen and lands at a spot that is not in front of the snake, stays a while, and
 * flies off. It sees the snake all around: when the snake gets closer than its alarm distance it crouches for a moment, then
 * takes off straight away from it, and flies out of the screen. Only a bird on the ground, or just leaving it, can be eaten.
 */
internal class BirdBody(
    x: Float,
    y: Float,
    landX: Float,
    landY: Float,
    private val alarmDistance: Float,
    staySeconds: Float,
    isLanded: Boolean,
    private val isTame: Boolean = false,
) : CritterBody {
    private val course = CritterCourse(x, y)
    private val flight = BirdFlight(course, landX, landY)
    private val perch = BirdPerch(course, staySeconds)
    private var phase = if (isLanded) BirdPhase.Perched else BirdPhase.Arriving
    private var crouchTime = 0f

    override val x: Float get() = course.x
    override val y: Float get() = course.y
    override val facing: Int get() = course.facing
    override val mode: CritterMode
        get() = when (phase) {
            BirdPhase.Arriving -> CritterMode.Entering
            BirdPhase.Perched, BirdPhase.Startled -> CritterMode.Roaming
            BirdPhase.Leaving -> CritterMode.Leaving
        }
    override val isMoving: Boolean get() = isAirborne || perch.isHopping

    /** A bird that has seen the snake and is about to take off. */
    override val isFleeing: Boolean get() = phase == BirdPhase.Startled
    override val height: Float get() = if (isAirborne) flight.height else perch.height

    /** Set when the bird lands or takes off, for the dust: the reader clears it. */
    override var landed = false
    override val isGone: Boolean get() = flight.isOut
    override val isAirborne: Boolean get() = phase == BirdPhase.Arriving || phase == BirdPhase.Leaving
    override val isEdible: Boolean get() = height <= BirdTuning.eatHeight

    override fun update(dt: Float, arena: Arena, random: Random, snakeHead: Vec2?, aura: SnakeAura) {
        when (phase) {
            BirdPhase.Arriving -> arrive(dt, arena, random, snakeHead)
            BirdPhase.Perched -> sit(dt, arena, random, snakeHead, aura)
            BirdPhase.Startled -> crouch(dt)
            BirdPhase.Leaving -> flight.leave(dt, arena)
        }
    }

    private fun arrive(dt: Float, arena: Arena, random: Random, snakeHead: Vec2?) {
        // A tame bird comes down where it was sent, whatever the snake does
        flight.approach(dt, arena, random, if (isTame) null else snakeHead)
        if (flight.hasLanded) {
            phase = BirdPhase.Perched
            landed = true
        }
    }

    private fun sit(dt: Float, arena: Arena, random: Random, snakeHead: Vec2?, aura: SnakeAura) {
        if (isTame) return
        perch.update(dt, arena, random)
        if (snakeHead != null && course.isNear(snakeHead.x, snakeHead.y, alarmDistance * aura.noticeScale)) {
            perch.freeze()
            flight.pointAwayFrom(snakeHead)
            crouchTime = BirdTuning.reactionSeconds
            phase = BirdPhase.Startled
        } else if (perch.isDone) {
            perch.freeze()
            flight.pointOut(arena, random)
            takeOff()
        }
    }

    private fun crouch(dt: Float) {
        crouchTime -= dt
        if (crouchTime <= 0f) takeOff()
    }

    private fun takeOff() {
        flight.takeOff()
        landed = true
        phase = BirdPhase.Leaving
    }
}
