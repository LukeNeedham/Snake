package com.lukeneedham.snake.game

/** Every number that shapes how the game plays. The world is measured in art pixels: one unit is one pixel of the pixel art. */
object GameTuning {
    /** The width of the world in art pixels; the height follows the aspect ratio of the screen. */
    const val WORLD_WIDTH = 108

    /** How thick the band of tall grass around the oval is. */
    const val GRASS_DEPTH = 8f

    const val SNAKE_SPEED = 30f
    const val TURN_RADIUS = 11f
    /** How wide the head is at its widest, at the back: it narrows to the nose. */
    const val HEAD_RADIUS = 3.6f

    /** How far in front of the head's centre the mouth is, where the snake eats. */
    const val MOUTH_REACH = 4.6f
    const val BODY_RADIUS = 2.7f

    /** The distance between two drawn discs of the body: the skin pattern repeats on discs. */
    const val BODY_SPACING = 2.2f

    /** The length one mouse adds to the snake. */
    const val SECTION_LENGTH = 4f
    const val START_SECTIONS = 8
    const val GROW_RATE = 14f
    const val TRAIL_STEP = 0.5f

    /** Parts of the body closer than this to the head (along the body) can never be hit: the neck. */
    const val SELF_HIT_SKIP = 10f
    const val SELF_HIT_DISTANCE = 4.8f
    const val EAT_RADIUS = 4.4f

    const val MOUSE_SPEED = 26f
    const val HOP_HEIGHT = 5f
    const val HOP_DURATION = 0.42f

    const val SPEEDY_FACTOR = 0.6f
}
