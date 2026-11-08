package com.lukeneedham.snake.game

/** The numbers that shape the snake and how it eats. */
object SnakeTuning {
    const val speed = 30f

    const val turnRadius = 11f

    /** How wide the head is at its widest, at the back: it narrows to the nose. */
    const val headRadius = 3.6f

    /** How far in front of the head's centre the mouth is, where the snake eats. */
    const val mouthReach = 4.6f
    const val eatRadius = 4.4f

    /** How far from the wall the head is kept, past its own radius. */
    const val wallMargin = 0.8f

    const val bodyRadius = 2.7f

    /** The distance between two drawn discs of the body: the skin pattern repeats on discs. */
    const val bodySpacing = 2.2f

    /** The length one mouse adds to the snake. */
    const val sectionLength = 4f
    const val startSections = 8
    const val growRate = 14f

    /** The distance between two points of the trail that the body is sampled from. */
    const val trailStep = 0.5f

    /** Parts of the body closer than this to the head (along the body) can never be hit: the neck. */
    const val selfHitSkip = 10f
    const val selfHitDistance = 4.8f
}
