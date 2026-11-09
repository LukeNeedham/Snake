package com.lukeneedham.snake.game

import kotlin.math.max
import kotlin.random.Random

/**
 * A mouse runs fast for a moment, stands still and looks around, then runs again. When a roaming mouse sees the snake it runs
 * the other way, faster. It only sees what is in front of it, so a snake that comes up behind its back is not noticed.
 *
 * A snake that carries cheese is the opposite: a roaming mouse smells it from far away, in any direction, runs towards it and
 * stands around the snake. When the cheese is gone the mice are dazed for a moment, so that the snake can eat them.
 */
internal class MouseGait(private val course: CritterCourse, private val visit: CritterVisit) {

    var isRunning = false
        private set

    var isFleeing = false
        private set

    private var timer = 0.2f
    private var lookTimer = 0f
    private var dazeTimer = 0f

    /**
     * [noticeScale] is how far away the snake is noticed, compared to normal: a sneaky snake is noticed from less far.
     * [smellsCheese] is whether the snake carries cheese.
     */
    fun update(dt: Float, arena: Arena, random: Random, snakeHead: Vec2?, noticeScale: Float, smellsCheese: Boolean) {
        timer -= dt
        if (smellsCheese && snakeHead != null && visit.mode == CritterMode.Roaming && course.isNear(snakeHead.x, snakeHead.y, CritterTuning.cheeseSmellDistance)) {
            gatherAround(dt, snakeHead)
            return
        }
        dazeTimer = max(0f, dazeTimer - dt)
        if (dazeTimer > 0f) {
            stayDazed()
        } else if (!isFleeing && snakeHead != null && noticesTheSnake(snakeHead, noticeScale)) {
            startFleeing(snakeHead)
        }
        if (isRunning) run(dt, arena, random) else standAndLook(dt, arena, random)
    }

    /** Runs to the snake, and stands around it once it is close. */
    private fun gatherAround(dt: Float, snakeHead: Vec2) {
        dazeTimer = CritterTuning.cheeseDazeSeconds
        isFleeing = false
        timer = gatherPause
        if (course.isNear(snakeHead.x, snakeHead.y, CritterTuning.cheeseCrowdDistance)) {
            isRunning = false
        } else {
            isRunning = true
            course.aimAt(snakeHead.x, snakeHead.y)
            course.moveAlong(CritterTuning.cheeseSpeed, dt)
        }
    }

    /** Stands still and looks around, without running away from the snake. */
    private fun stayDazed() {
        isFleeing = false
        isRunning = false
        timer = max(timer, gatherPause)
    }

    private fun noticesTheSnake(snakeHead: Vec2, noticeScale: Float): Boolean =
        visit.mode == CritterMode.Roaming && course.sees(snakeHead.x, snakeHead.y, CritterTuning.noticeDistance * noticeScale)

    private fun startFleeing(snakeHead: Vec2) {
        isFleeing = true
        isRunning = true
        course.runAwayFrom(snakeHead.x, snakeHead.y)
        timer = CritterTuning.fleeSeconds
    }

    private fun run(dt: Float, arena: Arena, random: Random) {
        course.moveAlong(if (isFleeing) CritterTuning.fleeSpeed else CritterTuning.mouseSpeed, dt)
        if (visit.mode == CritterMode.Roaming && arena.signedDistance(course.x, course.y) > -turnBackDepth && course.isHeadingOut(arena)) {
            course.aimInside(arena, random)
        }
        if (timer <= 0f) {
            isRunning = false
            isFleeing = false
            timer = if (visit.mode == CritterMode.Roaming) 0.7f + random.nextFloat() * 1.6f else 0.15f
            lookTimer = 0.2f
        }
    }

    private fun standAndLook(dt: Float, arena: Arena, random: Random) {
        lookTimer -= dt
        if (lookTimer <= 0f) {
            course.lookTheOtherWay()
            lookTimer = 0.25f + random.nextFloat() * 0.5f
        }
        if (timer <= 0f) {
            course.chooseDirection(visit.mode, arena, random)
            isRunning = true
            timer = if (visit.mode == CritterMode.Roaming) 0.35f + random.nextFloat() * 0.5f else 0.9f
        }
    }

    private companion object {
        /** A mouse that gathers around the snake waits this long before it does anything else, once the cheese is gone. */
        const val gatherPause = 0.2f

        /** A roaming mouse that runs closer to the wall than this turns back in. */
        const val turnBackDepth = 4f
    }
}
