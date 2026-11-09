package com.lukeneedham.snake.game

/**
 * What the rules are in a room, so that each room works in its own way: whether the hunger bar is shown and how it changes, whether
 * animals come out of the grass, and how fast the snake is. The [level] of a stage is what sets its numbers.
 */
class RoomConfig(
    val hungerMode: HungerMode,
    /** Whether the hunger bar is shown. */
    val showsHunger: Boolean,
    /** Whether the bar is only shown when the lessons teach it, and not for the whole time in the room. */
    val isTaught: Boolean = false,
    /** Whether animals come out of the grass of the room by themselves. */
    val spawnsCritters: Boolean = false,
    val level: LevelConfig = LevelConfig.first,
    /** How fast the snake is, as a part of its normal speed. */
    val snakeSpeedFactor: Float = 1f,
) {
    companion object {
        /** The house: the snake is safe and fed, and the bar is not there. */
        val house = RoomConfig(HungerMode.Full, showsHunger = false)

        /** The room of the tutorial: the bar is part of the lessons, which change it by hand. */
        val tutorial = RoomConfig(HungerMode.Manual, showsHunger = true, isTaught = true)

        /** On a path between rooms, nothing changes and the bar is away. */
        val path = RoomConfig(HungerMode.Manual, showsHunger = false)

        /** A stage: the game, as the [level] sets it. */
        fun stage(level: LevelConfig) = RoomConfig(HungerMode.Drains, showsHunger = true, spawnsCritters = true, level = level, snakeSpeedFactor = level.snake.speedFactor)
    }
}
