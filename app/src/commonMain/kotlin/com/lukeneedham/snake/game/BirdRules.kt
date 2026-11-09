package com.lukeneedham.snake.game

/** How the birds of a level come, fly and land. There is never more than one bird at a time. */
class BirdRules(
    /** No bird comes before the score is this high. */
    val firstScore: Int = SpawnTuning.firstBirdScore,
    val firstDelay: Float = SpawnTuning.firstBirdDelay,
    /** Seconds before the next bird can fly in, counted from when the last one came: a random time between the two. */
    val delayMin: Float = SpawnTuning.birdDelayMin,
    val delayMax: Float = SpawnTuning.birdDelayMax,
    val cruiseSpeed: Float = BirdTuning.cruiseSpeed,
    val landingSpeed: Float = BirdTuning.landingSpeed,
    val climbSpeed: Float = BirdTuning.climbSpeed,
    val leaveSpeed: Float = BirdTuning.leaveSpeed,
    /** A bird that sees the snake waits this long, crouched, before it takes off: that is the only time to catch it. */
    val reactionSeconds: Float = BirdTuning.reactionSeconds,
    val staySecondsMin: Float = BirdTuning.staySecondsMin,
    val staySecondsMax: Float = BirdTuning.staySecondsMax,
    /** A bird never lands closer than this to the head of the snake. */
    val landClearance: Float = BirdTuning.landClearance,
    /** The snake can only eat a bird that is lower than this above the ground. */
    val eatHeight: Float = BirdTuning.eatHeight,
) {
    companion object {
        val standard = BirdRules()
    }
}
