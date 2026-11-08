package com.lukeneedham.snake.game

import kotlin.random.Random

/**
 * A mouse runs fast for a moment, stands still and looks around, then runs again. When a roaming mouse sees the snake it runs
 * the other way, faster. It only sees what is in front of it, so a snake that comes up behind its back is not noticed.
 */
internal class MouseGait(private val course: CritterCourse, private val visit: CritterVisit) {

    var isRunning = false
        private set

    var isFleeing = false
        private set

    private var timer = 0.2f
    private var lookTimer = 0f

    fun update(dt: Float, arena: Arena, random: Random, snakeHead: Vec2?) {
        timer -= dt
        if (!isFleeing && snakeHead != null && noticesTheSnake(snakeHead)) startFleeing(snakeHead)
        if (isRunning) run(dt, arena, random) else standAndLook(dt, arena, random)
    }

    private fun noticesTheSnake(snakeHead: Vec2): Boolean =
        visit.mode == CritterMode.Roaming && course.sees(snakeHead.x, snakeHead.y, CritterTuning.noticeDistance)

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
        /** A roaming mouse that runs closer to the wall than this turns back in. */
        const val turnBackDepth = 4f
    }
}
