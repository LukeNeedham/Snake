package com.lukeneedham.snake.game

/** How often the mice and the toads come out of the grass and the birds fly in, and how many there can be at a time (never more than one bird). */
object SpawnTuning {
    const val firstMouseDelay = 0.5f
    const val firstToadDelay = 7f
    const val firstBirdDelay = 30f

    /** No bird comes before the score is this high. */
    const val firstBirdScore = 200

    /** Seconds before the next mouse can come: a random time between the two. */
    const val mouseDelayMin = 3f
    const val mouseDelayMax = 5.5f

    const val toadDelayMin = 6f
    const val toadDelayMax = 10f

    /** Seconds before the next bird can fly in, counted from when the last one came: birds are rare. */
    const val birdDelayMin = 40f
    const val birdDelayMax = 70f

    /** There is one mouse at the start, and one more for every this many points. */
    const val pointsPerExtraMouse = 800
    const val mostMice = 3

    const val pointsPerExtraToad = 600
    const val mostToads = 3
}
