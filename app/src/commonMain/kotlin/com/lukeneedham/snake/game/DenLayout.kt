package com.lukeneedham.snake.game

/**
 * The story at the start happens on a second screen under the first: the world is twice as tall as the screen. The first screen is the
 * level (the oval); on the second the snake's den, a round floor ringed with rocks, is near the bottom, and a path of bare ground
 * leads from it up through the grass, past the top of the second screen and into the oval at the bottom of the first. The path is
 * always cut through the grass at the bottom of the oval, with or without the story.
 */
class DenLayout(val arena: Arena) {

    /** The height of the whole world: the level and the second screen. */
    val worldHeight = arena.height * 2f

    /** Where the camera is, as a world y, when it shows the second screen. */
    val introTop = arena.height

    val denX = arena.centerX
    val denY = worldHeight - DenTuning.denFromBottom

    /** The floor of the den: where the snake lives. */
    val den = CircleBounds(denX, denY, DenTuning.denRadius)

    /** The path from the den to the oval. */
    val path: BoxBounds

    /** Everything the snake can move on during the story: the den, the path and the oval. */
    val ground: Bounds

    /** The bubbles of the snake's thought, from the smallest one up: the last one is the big one with the mouse in it. */
    val bubbles = listOf(
        ThoughtBubble(denX + 10f, denY - 20f, 1.8f),
        ThoughtBubble(denX + 14f, denY - 30f, 2.8f),
        ThoughtBubble(denX + 6f, denY - 56f, 16f),
    )

    init {
        val top = arena.height - WorldTuning.grassDepth - DenTuning.pathOverlap
        path = BoxBounds(arena.centerX, (top + denY) / 2f, DenTuning.pathHalfWidth, (denY - top) / 2f)
        ground = UnionBounds(listOf(arena, path, den))
    }

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
    }
}
