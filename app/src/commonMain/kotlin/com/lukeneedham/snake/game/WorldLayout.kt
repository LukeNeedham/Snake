package com.lukeneedham.snake.game

/**
 * The map of the world. The screen is [Arena.width] by [Arena.height]. The oval of the game (the stage) is at the top. A path of bare
 * ground one screen long leads down from it to the snake's house, a misshapen floor of stone ringed with rocks, so that neither can be seen
 * from the other. Another path leads left from the house to the room of the tutorial, an oval just like the stage, which is on the screen
 * to the left of the house. The path up from the house is closed by a line of stones in the wall of the house at the start of the first game,
 * until the tutorial is done. The camera follows the snake: it shows the house or the room, and the path up between the house and the stage.
 */
class WorldLayout(val arena: Arena) {

    /** The left edge of the world: the room of the tutorial is on the screen to the left of the screen of the stage. */
    val worldLeft = -arena.width
    val worldWidth = arena.width * 2f

    val houseX = arena.centerX

    /** The house is one screen under the stage: the path between them is as long as the screen is high. */
    val houseY = (arena.height - WorldTuning.grassDepth) + arena.height + DenTuning.houseRadius

    /** The house and the room of the tutorial are in the middle of the screen at the bottom of the world. */
    val worldHeight = houseY + arena.height / 2f

    /** Where the camera is, as a world y, when it shows the house and the room. */
    val maxCameraY = worldHeight - arena.height

    /** Where the camera is, as a world x, when it shows the room. */
    val roomCameraX = worldLeft

    /** The floor of the house: where the snake lives. */
    val house = BlobBounds(houseX, houseY, DenTuning.houseRadius, listOf(Wobble(0.06f, 2, 0.7f), Wobble(0.05f, 3, 2.1f)))

    /** The room of the tutorial: an oval of the size of the stage, to the left of the house. */
    val room = OvalBounds(arena.centerX - arena.width, houseY, arena.radiusX, arena.radiusY)

    /** The path from the house up to the stage. */
    val pathUp: BoxBounds

    /** The path from the house to the left, to the room. */
    val pathLeft: BoxBounds

    /** The line of stones that closes the path up in the wall of the house, while it is closed. */
    val gate: BoxBounds

    /** Whether the stones block the path up: they do until the tutorial is done. */
    var isGateClosed = false

    /** Whether the way in to the stage, the path up, is grown shut with grass: the stage is then a perfect oval, and nothing gets in or out of it. */
    var isStageSealed = false

    /** All the bare ground of the map: the stage, the paths, the house and the room. */
    val terrain: Bounds

    /** The map with the path up cut off: everything but the way to the stage, which is a perfect oval. */
    val sealedTerrain: Bounds

    /** What the snake can move on: all the [terrain], but not through the stones of the gate while they are there, and not the path up once the stage is sealed. */
    val ground: Bounds

    /** Below this the snake has left the stage for the path, and the camera starts to follow it. */
    val mouthY = arena.height - WorldTuning.grassDepth - 4f

    /** The bubbles of the snake's thought, from the smallest one up: the last one is the big one with the mouse in it. */
    val bubbles = listOf(
        ThoughtBubble(houseX + 10f, houseY - 22f, 1.8f),
        ThoughtBubble(houseX + 15f, houseY - 34f, 2.8f),
        ThoughtBubble(houseX + 8f, houseY - 62f, 16f),
    )

    init {
        val top = arena.height - WorldTuning.grassDepth - DenTuning.pathOverlap
        pathUp = BoxBounds(arena.centerX, (top + houseY) / 2f, DenTuning.pathHalfWidth, (houseY - top) / 2f)
        pathLeft = BoxBounds((room.centerX + houseX) / 2f, houseY, (houseX - room.centerX) / 2f, DenTuning.leftPathHalfWidth)
        // The stones close the opening that the path up makes in the wall of the house, along the curve of the wall
        val edgeY = houseY - house.radiusAt(-halfPi)
        gate = BoxBounds(houseX, edgeY - DenTuning.gateOutside, DenTuning.pathHalfWidth + 3f, DenTuning.gateHalfThickness)
        terrain = UnionBounds(listOf(arena, pathUp, house, pathLeft, room))
        sealedTerrain = UnionBounds(listOf(arena, house, pathLeft, room))
        ground = SwitchBounds(GateBounds(terrain, gate) { isGateClosed }, sealedTerrain) { isStageSealed }
    }

    /** Whether something [height] tall that stands on [x], [baseY] reaches into the floor of the house, or comes within [margin] of it. */
    fun touchesFloor(x: Float, baseY: Float, height: Float, margin: Float): Boolean =
        house.signedDistance(x, baseY) < margin ||
            house.signedDistance(x, baseY - height / 2f) < margin ||
            house.signedDistance(x, baseY - height) < margin

    /** The snake curled up in its house: it circles the middle of the floor, and it has gone round long enough for its whole body to follow the circle. */
    fun curledSnake(): Snake {
        val heading = -halfPi
        // It turns clockwise, so the middle of its circle is to the right of where it goes
        val snake = Snake(arena, ground, houseX - SnakeTuning.turnRadius, houseY, heading)
        repeat((DenTuning.warmUpSeconds * warmUpFrames).toInt()) { snake.step(1f / warmUpFrames, 1f) }
        return snake
    }

    /** A snake in the middle of the room of the tutorial, going up, with its body straight behind it: for the lessons to start from. */
    fun snakeInRoom(): Snake = Snake(arena, ground, room.centerX, room.centerY + roomStartBelow, -halfPi)

    private companion object {
        const val halfPi = 1.5707964f
        const val warmUpFrames = 60f

        /** The snake of a lesson starts this far under the middle of the room. */
        const val roomStartBelow = 30f
    }
}
