package com.lukeneedham.snake.game

/** How often the mice and the toads come out of the grass, and how many there can be at a time. */
object SpawnTuning {
    const val firstMouseDelay = 0.5f
    const val firstToadDelay = 7f

    /** Seconds before the next mouse can come: a random time between the two. */
    const val mouseDelayMin = 3f
    const val mouseDelayMax = 5.5f

    const val toadDelayMin = 6f
    const val toadDelayMax = 10f

    /** There is one mouse at the start, and one more for every this many points. */
    const val pointsPerExtraMouse = 8
    const val mostMice = 3

    const val pointsPerExtraToad = 6
    const val mostToads = 3
}
