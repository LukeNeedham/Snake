package com.lukeneedham.snake.game

/** The numbers that shape the snake's house, the paths from it and the camera that follows the snake. All in art pixels. */
object DenTuning {
    /** Half of how wide the path up to the oval is: wide enough that the player has to steer the snake along it in a straight line. */
    const val pathHalfWidth = 15f

    /** Half of how wide the path to the left, to the room of the tutorial, is: wider still. */
    const val leftPathHalfWidth = 18f

    /** The average radius of the floor of the house: big enough for the snake to loop round in. The edge is a little misshapen. */
    const val houseRadius = 40f

    /**
     * How far the path up reaches into the oval. It has to reach well past the wall of the oval (more than the snake's distance from a wall), or
     * the snake near the join would be too close to the wall of the path and the wall of the oval at once, and could never get through.
     */
    const val pathOverlap = 14f

    /** Where on the screen, from the top, the camera keeps the snake while it follows it along the path: a fraction of the screen height. */
    const val followLine = 0.5f

    /** How quickly the camera catches up with the snake. */
    const val cameraEase = 6f

    /** How deep into the oval the head must be for the snake to be out of the intro. */
    const val exitDepth = 8f

    /** How many seconds the snake circles before the intro starts, so that its body already follows the circle. */
    const val warmUpSeconds = 1.6f

    /** The pile of stones that blocks the path up is this far above the house, and this thick. */
    const val gateAboveHouse = 16f
    const val gateHalfThickness = 7f

    /** How deep in the room of the tutorial, and in the house, the snake has to be to be there. */
    const val roomDepth = 10f
    const val homeDepth = 12f

    /** The camera shows the other room once the snake is this far past the line between the house and the room. */
    const val roomSwitch = 3f
}
