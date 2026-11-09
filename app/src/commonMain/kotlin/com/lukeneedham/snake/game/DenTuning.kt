package com.lukeneedham.snake.game

/** The numbers that shape the den, the path out of it and the camera that follows the snake along it. All in art pixels. */
object DenTuning {
    /** Half of how wide the path is: the snake turns in circles of its own, so the path is wide enough to zigzag along. */
    const val pathHalfWidth = 11f

    /** How big the round floor of the den is: the snake curled up in a circle of its own turning radius fits with room to spare. */
    const val denRadius = 17f

    /** How far above the bottom edge of the world the middle of the den is. */
    const val denFromBottom = 66f

    /** How far the path reaches into the oval, so that it joins it without a seam. */
    const val pathOverlap = 4f

    /** Where on the screen, from the top, the camera keeps the snake while it follows it up the path: a fraction of the screen height. */
    const val followLine = 0.62f

    /** How quickly the camera catches up with the snake. */
    const val cameraEase = 6f

    /** How deep into the oval the head must be for the snake to be out of the intro. */
    const val exitDepth = 8f

    /** How many seconds the snake circles before the intro starts, so that its body already follows the circle. */
    const val warmUpSeconds = 1.6f
}
