package com.lukeneedham.snake.game

/** The numbers that shape the den, the path out of it and the camera that follows the snake along it. All in art pixels. */
object DenTuning {
    /** Half of how wide the path is: wide enough that the player has to steer the snake along it in a straight line, not just bounce along the walls. */
    const val pathHalfWidth = 15f

    /** The average radius of the den floor: big enough for the snake to loop round in. The edge is a little misshapen, up to a bit more than a tenth either way. */
    const val denRadius = 40f

    /** How far above the bottom edge of the world the middle of the den is. */
    const val denFromBottom = 56f

    /**
     * How far the path reaches into the oval. It has to reach well past the wall of the oval (more than the snake's distance from a wall), or
     * the snake near the join would be too close to the wall of the path and the wall of the oval at once, and could never get through.
     */
    const val pathOverlap = 14f

    /** Where on the screen, from the top, the camera keeps the snake while it follows it along the path: a fraction of the screen height. */
    const val followLine = 0.62f

    /** How quickly the camera catches up with the snake. */
    const val cameraEase = 6f

    /** How deep into the oval the head must be for the snake to be out of the intro. */
    const val exitDepth = 8f

    /** How many seconds the snake circles before the intro starts, so that its body already follows the circle. */
    const val warmUpSeconds = 1.6f

    /** How long one stretch of the path that repeats is, as a part of the height of the screen. */
    const val loopPeriodFactor = 0.35f
}
