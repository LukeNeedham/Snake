package com.lukeneedham.snake.game

import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

/** A bird in the air: it flies in from off the screen and comes down at its landing spot, or it takes off and flies out of the screen. */
internal class BirdFlight(private val course: CritterCourse, private var targetX: Float, private var targetY: Float) {

    /** How high the bird is above the ground. */
    var height = BirdTuning.flightHeight
        private set

    var hasLanded = false
        private set

    /** Set once the bird has flown out of the screen. */
    var isOut = false
        private set

    private var airTime = 0f

    /** Flies towards the landing spot, slowing down and coming down on the way. */
    fun approach(dt: Float, arena: Arena, random: Random, snakeHead: Vec2?) {
        if (snakeHead != null && height > BirdTuning.commitHeight && BirdLanding.isTooClose(targetX, targetY, snakeHead)) {
            val spot = BirdLanding.spot(arena, random, snakeHead)
            targetX = spot.x
            targetY = spot.y
        }
        val dx = targetX - course.x
        val dy = targetY - course.y
        val distance = sqrt(dx * dx + dy * dy)
        if (distance < touchdownDistance) {
            course.x = targetX
            course.y = targetY
            height = 0f
            hasLanded = true
            return
        }
        course.aimAt(targetX, targetY)
        val closeness = min(1f, distance / BirdTuning.glideDistance)
        val speed = BirdTuning.landingSpeed + (BirdTuning.cruiseSpeed - BirdTuning.landingSpeed) * closeness
        course.moveBy(min(speed * dt, distance))
        height = BirdTuning.flightHeight * closeness
    }

    /** Moves the landing spot [dy] down. */
    fun shiftDown(dy: Float) {
        targetY += dy
    }

    /** Turns to fly straight away from the snake. */
    fun pointAwayFrom(head: Vec2) = course.runAwayFrom(head.x, head.y)

    /** Turns to fly out of the oval. */
    fun pointOut(arena: Arena, random: Random) = course.chooseDirection(CritterMode.Leaving, arena, random)

    fun takeOff() {
        height = 0f
        airTime = 0f
    }

    /** Climbs and flies off the way the bird points, faster and faster, until it is out of the screen. */
    fun leave(dt: Float, arena: Arena) {
        airTime += dt
        height = min(BirdTuning.flightHeight, height + BirdTuning.climbSpeed * dt)
        val ramp = takeOffStartSpeed + (1f - takeOffStartSpeed) * min(1f, airTime / BirdTuning.takeOffSeconds)
        course.moveBy(BirdTuning.leaveSpeed * ramp * dt)
        val margin = BirdTuning.exitMargin
        if (course.x < -margin || course.x > arena.width + margin || course.y < -margin || course.y > arena.height + margin) isOut = true
    }

    private companion object {
        /** The bird lands once it is this close to the spot. */
        const val touchdownDistance = 1f

        /** A bird that has just taken off flies at this fraction of its full speed. */
        const val takeOffStartSpeed = 0.35f
    }
}
