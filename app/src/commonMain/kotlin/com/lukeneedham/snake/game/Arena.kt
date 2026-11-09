package com.lukeneedham.snake.game

/**
 * The oval the snake lives in, inside a band of tall grass that fills the rest of the screen. [width] and [height] are the
 * size of the screen in art pixels: the oval is in the middle of it. The [shape] is how much of the screen the oval fills: the biggest one
 * that fits, or a smaller one, with more grass round it.
 */
class Arena(val width: Float, val height: Float, val shape: StageShape = StageShape.full) : Bounds {
    val centerX = width / 2f
    val centerY = height / 2f

    /** How wide and high the oval is from its middle, when it is the biggest one that fits the screen. */
    val fullRadiusX = width / 2f - WorldTuning.grassDepth
    val fullRadiusY = height / 2f - WorldTuning.grassDepth

    val radiusX = fullRadiusX * shape.widthFraction
    val radiusY = fullRadiusY * shape.heightFraction

    /** Where the bottom of the oval is. */
    val ovalBottom = centerY + radiusY

    private val oval = OvalBounds(centerX, centerY, radiusX, radiusY)

    /**
     * How much of the bottom of the world is under the navigation bar, in art pixels. The game is drawn edge to edge, behind the
     * system bars, so what the player touches at the bottom has to sit above this. The scene sets it once it knows the size of the screen.
     */
    var bottomInset = 0f

    /** The same screen with the oval of [shape]. */
    fun shaped(shape: StageShape): Arena = if (shape === this.shape) this else Arena(width, height, shape).also { it.bottomInset = bottomInset }

    /** How far the point is from the edge of the oval, along the way out: negative inside, positive in the grass. */
    override fun signedDistance(x: Float, y: Float): Float = oval.signedDistance(x, y)

    /** The unit vector that points out of the oval at the point. */
    override fun normal(x: Float, y: Float): Vec2 = oval.normal(x, y)
}
