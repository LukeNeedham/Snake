package com.lukeneedham.snake.game

/**
 * The first screen of the world is the level (the oval). At the bottom of the world is the snake's den, a misshapen floor ringed with rocks,
 * and a path of bare ground leads from it up through the grass into the oval. The snake can go anywhere on the oval, the path and the
 * den: the camera follows it. With a [isLong] path (the first game, with the tutorial) the world is three screens tall, and the path goes on
 * and on, a stretch that repeats, until the player has learned the lessons along it. Otherwise the path is short, and the den has a door
 * in its wall that starts the tutorial again when the snake opens it.
 */
class DenLayout(val arena: Arena, val isLong: Boolean = false) {

    /** The height of the whole world. */
    val worldHeight = arena.height * (if (isLong) 3f else shortWorldScreens)

    /** Where the camera is, as a world y, when it shows the den at the bottom of the world. */
    val denCameraY = worldHeight - arena.height

    val denX = arena.centerX
    val denY = worldHeight - DenTuning.denFromBottom

    /** The floor of the den: where the snake lives. */
    val den = BlobBounds(denX, denY, DenTuning.denRadius, listOf(Wobble(0.06f, 2, 0.7f), Wobble(0.05f, 3, 2.1f)))

    /** The path from the den to the oval. */
    val path: BoxBounds

    /** Everything the snake can move on: the den, the path and the oval. */
    val ground: Bounds

    /** Below this the snake has left the oval for the path, and the camera starts to follow it. */
    val mouthY = arena.height - WorldTuning.grassDepth - 4f

    /** The rows of the path that repeats: it is the same all along, with a period of [loopPeriodRows] rows, from [loopTopRow] down to [loopBottomRow]. */
    val loopTopRow = arena.height.toInt() + 2
    val loopPeriodRows = (arena.height * DenTuning.loopPeriodFactor).toInt()
    val loopBottomRow = (denY - 58f).toInt()

    /** How far the snake is put back down the path, when it goes up past [loopWrapY] while the path goes on and on. */
    val loopPeriod: Float get() = loopPeriodRows.toFloat()
    val loopWrapY = arena.height * (1f + DenTuning.followLine) + 4f

    /** Half way along the path from the den to the oval. */
    val halfwayY = ((denY - DenTuning.denRadius) + arena.height) / 2f

    /** Whether the den has the door of the tutorial: only when the player has learned it already, and can do it again. */
    val hasDoor = !isLong

    /** The door is in the wall of the den, at the bottom: the snake opens it by going close to it. */
    val doorX = denX
    val doorY = denY + den.radiusAt(halfPi)

    /** The bubbles of the snake's thought, from the smallest one up: the last one is the big one with the mouse in it. */
    val bubbles = listOf(
        ThoughtBubble(denX + 10f, denY - 22f, 1.8f),
        ThoughtBubble(denX + 15f, denY - 34f, 2.8f),
        ThoughtBubble(denX + 8f, denY - 62f, 16f),
    )

    init {
        val top = arena.height - WorldTuning.grassDepth - DenTuning.pathOverlap
        path = BoxBounds(arena.centerX, (top + denY) / 2f, DenTuning.pathHalfWidth, (denY - top) / 2f)
        ground = UnionBounds(listOf(arena, path, den))
    }

    /** The row to take the look of the ground from at [row]: in the stretch of the path that repeats, the rows of one period over and over. */
    fun loopRow(row: Int): Int =
        if (row in loopTopRow until loopBottomRow) loopTopRow + (row - loopTopRow) % loopPeriodRows else row

    /** Whether something [height] tall that stands on [x], [baseY] reaches into the floor of the den, or comes within [margin] of it. */
    fun touchesFloor(x: Float, baseY: Float, height: Float, margin: Float): Boolean =
        den.signedDistance(x, baseY) < margin ||
            den.signedDistance(x, baseY - height / 2f) < margin ||
            den.signedDistance(x, baseY - height) < margin

    /** A snake that has just left the den: on the path at [y], going up, with its body straight behind it. For the lessons to start from. */
    fun snakeOnPath(y: Float): Snake = Snake(arena, ground, arena.centerX, y, -halfPi)

    /** The snake curled up in its den: it circles the middle of the den, and it has gone round long enough for its whole body to follow the circle. */
    fun curledSnake(): Snake {
        val heading = -halfPi
        // It turns clockwise, so the middle of its circle is to the right of where it goes
        val snake = Snake(arena, ground, denX - SnakeTuning.turnRadius, denY, heading)
        repeat((DenTuning.warmUpSeconds * warmUpFrames).toInt()) { snake.step(1f / warmUpFrames, 1f) }
        return snake
    }

    private companion object {
        const val halfPi = 1.5707964f
        const val warmUpFrames = 60f

        /** A short world is the oval and a little more than half a screen under it, where the den is. */
        const val shortWorldScreens = 1.5f
    }
}
