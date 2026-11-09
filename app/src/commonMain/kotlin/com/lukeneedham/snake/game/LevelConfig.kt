package com.lukeneedham.snake.game

/**
 * What makes one level a challenge of its own: how fast the snake is and gets hungry, and how the animals come. A stage is given a level;
 * every number that a level could change is here, so that a new level is a new value of this and nothing else.
 */
class LevelConfig(
    /** How fast the snake is, as a part of its normal speed. */
    val snakeSpeedFactor: Float = 1f,
    /** How much of the bar is lost every second. */
    val hungerDrainPerSecond: Float = HungerTuning.drainPerSecond,
    val mice: CritterRate = CritterRate(
        firstDelay = SpawnTuning.firstMouseDelay,
        delayMin = SpawnTuning.mouseDelayMin,
        delayMax = SpawnTuning.mouseDelayMax,
        pointsPerExtra = SpawnTuning.pointsPerExtraMouse,
        most = SpawnTuning.mostMice,
    ),
    /** How fast the mice run, as a part of their normal speed. */
    val mouseSpeedFactor: Float = 1f,
    val toads: CritterRate = CritterRate(
        firstDelay = SpawnTuning.firstToadDelay,
        delayMin = SpawnTuning.toadDelayMin,
        delayMax = SpawnTuning.toadDelayMax,
        pointsPerExtra = SpawnTuning.pointsPerExtraToad,
        most = SpawnTuning.mostToads,
    ),
    /** The kinds of toads that come, and so the poisons that the level has. */
    val toadKinds: List<ToadKind> = ToadKind.entries,
    /** No bird comes before the score is this high. */
    val firstBirdScore: Int = SpawnTuning.firstBirdScore,
    val firstBirdDelay: Float = SpawnTuning.firstBirdDelay,
    val birdDelayMin: Float = SpawnTuning.birdDelayMin,
    val birdDelayMax: Float = SpawnTuning.birdDelayMax,
) {
    companion object {
        /** The first level, which is also the way the game is played when nothing else is said. */
        val first = LevelConfig()
    }
}
